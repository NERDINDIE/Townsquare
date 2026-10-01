package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.verification.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CitizenIdVerificationDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onVerificationSuccess: (VerificationCertificate) -> Unit = {}
) {
    if (!isOpen) return

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf(VerificationStep.SELECT_DOCUMENT) }
    var selectedDocType by remember { mutableStateOf(DocumentType.GOVERNMENT_ID) }
    var citizenNameInput by remember { mutableStateOf("Alexander Vance") }
    var docNumberInput by remember { mutableStateOf("TS-8274-118A") }
    var dobInput by remember { mutableStateOf("1988-06-14") }

    // Scanner state
    var isScanningFront by remember { mutableStateOf(false) }
    var isScanningBack by remember { mutableStateOf(false) }
    var frontScanned by remember { mutableStateOf(false) }
    var backScanned by remember { mutableStateOf(false) }

    // Biometrics state
    var isCheckingLiveness by remember { mutableStateOf(false) }
    var livenessProgress by remember { mutableFloatStateOf(0f) }
    var livenessPassed by remember { mutableStateOf(false) }

    // Final result
    var issuedCert by remember { mutableStateOf<VerificationCertificate?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            color = DarkBg,
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Civic ID Verification", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text(text = "Townsquare Cryptographic Trust Protocol", style = MaterialTheme.typography.labelSmall, color = DarkTextSecondary)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DarkTextMuted)
                    }
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Steps progress indicator
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val stepIndex = currentStep.ordinal
                    listOf("Document", "Scan ID", "Biometrics", "Certify").forEachIndexed { idx, label ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = when {
                                    idx < stepIndex -> Color(0xFF30D158)
                                    idx == stepIndex -> NeonCyan
                                    else -> DarkSurfaceVariant
                                },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (idx < stepIndex) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    } else {
                                        Text(text = "${idx + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (idx == stepIndex) Color(0xFF003544) else DarkTextMuted)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = label, fontSize = 9.sp, color = if (idx <= stepIndex) Color.White else DarkTextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step content
                when (currentStep) {
                    VerificationStep.SELECT_DOCUMENT -> {
                        Text(text = "1. SELECT IDENTITY CREDENTIAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        DocumentType.entries.forEach { doc ->
                            val isSel = selectedDocType == doc
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) NeonCyan.copy(alpha = 0.1f) else DarkSurface,
                                border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedDocType = doc }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = doc.iconEmoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = doc.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text(text = doc.description, fontSize = 11.sp, color = DarkTextSecondary)
                                    }
                                    RadioButton(selected = isSel, onClick = { selectedDocType = doc })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = citizenNameInput,
                            onValueChange = { citizenNameInput = it },
                            label = { Text("Legal Citizen Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = docNumberInput,
                            onValueChange = { docNumberInput = it },
                            label = { Text("Credential Number") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { currentStep = VerificationStep.SCAN_DOCUMENT },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Proceed to Optical Scan", fontWeight = FontWeight.Bold)
                        }
                    }

                    VerificationStep.SCAN_DOCUMENT -> {
                        Text(text = "2. OPTICAL DOCUMENT SCANNER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Front Scan Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, if (frontScanned) Color(0xFF30D158) else DarkBorder),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = if (frontScanned) Icons.Default.CheckCircle else Icons.Default.DocumentScanner, contentDescription = null, tint = if (frontScanned) Color(0xFF30D158) else WarmAmber)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = "Front Document Face", fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(text = if (frontScanned) "High-res MRZ scan verified" else "Capture front of $docNumberInput", fontSize = 11.sp, color = DarkTextSecondary)
                                    }
                                }
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isScanningFront = true
                                            delay(1000L)
                                            isScanningFront = false
                                            frontScanned = true
                                            Toast.makeText(context, "Front scan completed!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (frontScanned) Color(0xFF30D158) else NeonCyan, contentColor = Color(0xFF003544)),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !isScanningFront
                                ) {
                                    Text(if (isScanningFront) "Scanning..." else if (frontScanned) "Retake" else "Scan Front", fontSize = 11.sp)
                                }
                            }
                        }

                        // Back Scan Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, if (backScanned) Color(0xFF30D158) else DarkBorder),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = if (backScanned) Icons.Default.CheckCircle else Icons.Default.QrCodeScanner, contentDescription = null, tint = if (backScanned) Color(0xFF30D158) else WarmAmber)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = "Back Barcode / NFC Chip", fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(text = if (backScanned) "Cryptographic barcode read" else "Scan barcode or tap smart chip", fontSize = 11.sp, color = DarkTextSecondary)
                                    }
                                }
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isScanningBack = true
                                            delay(1000L)
                                            isScanningBack = false
                                            backScanned = true
                                            Toast.makeText(context, "Back scan completed!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (backScanned) Color(0xFF30D158) else NeonCyan, contentColor = Color(0xFF003544)),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !isScanningBack
                                ) {
                                    Text(if (isScanningBack) "Scanning..." else if (backScanned) "Retake" else "Scan Back", fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { currentStep = VerificationStep.BIOMETRIC_LIVENESS },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            enabled = frontScanned && backScanned
                        ) {
                            Text("Continue to Biometric Check", fontWeight = FontWeight.Bold)
                        }
                    }

                    VerificationStep.BIOMETRIC_LIVENESS -> {
                        Text(text = "3. BIOMETRIC FACE LIVENESS CHECK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Camera Viewfinder Simulator
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF0A1220),
                            border = BorderStroke(2.dp, if (livenessPassed) Color(0xFF30D158) else NeonCyan),
                            modifier = Modifier.fillMaxWidth().height(220.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (livenessPassed) Color(0xFF30D158).copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.1f),
                                        border = BorderStroke(2.dp, if (livenessPassed) Color(0xFF30D158) else NeonCyan),
                                        modifier = Modifier.size(100.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (livenessPassed) Icons.Default.Check else Icons.Default.Face,
                                                contentDescription = null,
                                                tint = if (livenessPassed) Color(0xFF30D158) else NeonCyan,
                                                modifier = Modifier.size(54.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = if (livenessPassed) "Liveness Test Passed • 3D Biometrics Match" else if (isCheckingLiveness) "Blink and tilt head slightly..." else "Position face within camera frame",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (livenessPassed) Color(0xFF30D158) else Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (!livenessPassed) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isCheckingLiveness = true
                                        livenessProgress = 0.2f
                                        delay(800L)
                                        livenessProgress = 0.6f
                                        delay(800L)
                                        livenessProgress = 1.0f
                                        isCheckingLiveness = false
                                        livenessPassed = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                enabled = !isCheckingLiveness
                            ) {
                                Text(if (isCheckingLiveness) "Verifying Biometrics..." else "Start Face Liveness Check", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    scope.launch {
                                        currentStep = VerificationStep.CRYPTOGRAPHIC_SIGNING
                                        val req = IdVerificationRequest(
                                            citizenName = citizenNameInput,
                                            dateOfBirth = dobInput,
                                            documentType = selectedDocType,
                                            documentNumber = docNumberInput,
                                            biometricLivenessPassed = true,
                                            documentFrontScanCaptured = true,
                                            documentBackScanCaptured = true
                                        )
                                        val cert = CitizenIdVerificationEngine.verifyIdentity(req)
                                        issuedCert = cert
                                        currentStep = VerificationStep.CERTIFICATE_ISSUED
                                        onVerificationSuccess(cert)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30D158), contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text("Generate Cryptographic Certificate", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    VerificationStep.CRYPTOGRAPHIC_SIGNING -> {
                        Box(modifier = Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = NeonCyan)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "Validating Against Townsquare Civic Ledger...", fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = "Generating SHA-256 Citizen Block Certificate", fontSize = 11.sp, color = DarkTextSecondary)
                            }
                        }
                    }

                    VerificationStep.CERTIFICATE_ISSUED -> {
                        val cert = issuedCert
                        if (cert != null) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0C241D),
                                border = BorderStroke(2.dp, Color(0xFF30D158)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "OFFICIAL VERIFICATION CERTIFICATE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF30D158))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF30D158)
                                        ) {
                                            Text(text = cert.tier.badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0C241D), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(text = cert.citizenName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = Color.White)
                                    Text(text = "Document: ${cert.documentType.title} • ${cert.documentNumber}", fontSize = 11.sp, color = DarkTextSecondary)

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(text = "HASH: ${cert.cryptographicHash}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = NeonCyan)
                                    Text(text = "SEAL: ${cert.civicLedgerSignature}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = WarmAmber)

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(text = "Authorized: ${cert.authorizedBy} • Valid to ${cert.expiryDate}", fontSize = 10.sp, color = DarkTextMuted)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30D158), contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text("Complete & Close", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
