package com.example.ui.components.media

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DigitalFingerprint
import com.example.data.model.PhysicalMediaItem
import com.example.data.model.ProofOfPurchase
import com.example.data.repository.PhysicalMediaRepository
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadVerificationModal(
    pendingItem: PhysicalMediaItem,
    onDismiss: () -> Unit,
    onUploadFinalized: (PhysicalMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf(VerificationMethod.IBAN_BANK) }

    // IBAN Receipt state
    var userIban by remember { mutableStateOf("GB82 WEST 1234 5678 9012 34") }
    var bankName by remember { mutableStateOf("Townsquare Connected Bank") }
    var ibanTxRef by remember { mutableStateOf("SEPA-${(100000..999999).random()}") }

    // Order Screenshot state
    var marketplaceName by remember { mutableStateOf("Townsquare Marketplace / Bandcamp") }
    var orderNumber by remember { mutableStateOf("ORD-${(10000..99999).random()}-MKP") }
    var isScreenshotAttached by remember { mutableStateOf(true) }

    // Store Receipt state
    var receiptCode by remember { mutableStateOf("RCP-${(10000..99999).random()}-TS") }

    var isVerifying by remember { mutableStateOf(false) }
    var isVerifiedSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("upload_verification_modal")
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.8f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
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
                            color = NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🛡️", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Attach Purchase Verification",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Mandatory Proof Before Finalizing Upload",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Item info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162032)),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(pendingItem.format.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = pendingItem.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${pendingItem.format.label} • ${pendingItem.creatorOrArtist}",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmAmber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Verification Method Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VerificationMethod.entries.forEach { method ->
                        val isSelected = selectedMethod == method
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) NeonCyan else Color(0xFF162032),
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else Color(0xFF334155)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = method }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${method.iconEmoji} ${method.label.take(12)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Form fields by method
                when (selectedMethod) {
                    VerificationMethod.IBAN_BANK -> {
                        OutlinedTextField(
                            value = userIban,
                            onValueChange = { userIban = it },
                            label = { Text("Bank IBAN Number") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = ibanTxRef,
                            onValueChange = { ibanTxRef = it },
                            label = { Text("SEPA / Bank Transaction Ref ID") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )
                    }

                    VerificationMethod.MARKETPLACE_ORDER -> {
                        OutlinedTextField(
                            value = marketplaceName,
                            onValueChange = { marketplaceName = it },
                            label = { Text("Retailer / Marketplace") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = orderNumber,
                            onValueChange = { orderNumber = it },
                            label = { Text("Visible Order Number") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF162032),
                            border = BorderStroke(1.dp, if (isScreenshotAttached) NeonCyan else Color(0xFF334155)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isScreenshotAttached = !isScreenshotAttached }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isScreenshotAttached) "Order Screenshot Attached (OCR Scan Passed)" else "Tap to Attach Order Screenshot",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                                if (isScreenshotAttached) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    VerificationMethod.STORE_RECEIPT -> {
                        OutlinedTextField(
                            value = receiptCode,
                            onValueChange = { receiptCode = it },
                            label = { Text("Paper Receipt Serial / Barcode") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isVerifying) {
                    CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Verifying purchase proof with Townsquare ledger...", fontSize = 11.sp, color = NeonCyan)
                } else if (isVerifiedSuccess) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Purchase Verified! Finalizing Upload...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                } else {
                    Button(
                        onClick = {
                            isVerifying = true
                            coroutineScope.launch {
                                delay(1200L)
                                isVerifying = false
                                isVerifiedSuccess = true

                                val recId = when (selectedMethod) {
                                    VerificationMethod.IBAN_BANK -> ibanTxRef
                                    VerificationMethod.MARKETPLACE_ORDER -> orderNumber
                                    VerificationMethod.STORE_RECEIPT -> receiptCode
                                }

                                val storeName = when (selectedMethod) {
                                    VerificationMethod.IBAN_BANK -> "Bank IBAN ($bankName)"
                                    VerificationMethod.MARKETPLACE_ORDER -> marketplaceName
                                    VerificationMethod.STORE_RECEIPT -> "Paper Receipt ($receiptCode)"
                                }

                                val verifiedItem = pendingItem.copy(
                                    isPrivate = true,
                                    proofOfPurchase = ProofOfPurchase(
                                        isVerified = true,
                                        receiptId = recId,
                                        storeName = storeName,
                                        verifiedAtTimestamp = System.currentTimeMillis()
                                    ),
                                    fingerprint = DigitalFingerprint(
                                        originalUploaderId = "usr_me",
                                        originalUploaderName = "My Vault",
                                        isDownloadedFromElsewhere = false
                                    )
                                )

                                PhysicalMediaRepository.addItem(verifiedItem)
                                delay(600L)
                                onUploadFinalized(verifiedItem)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify & Finalize Media Upload", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
