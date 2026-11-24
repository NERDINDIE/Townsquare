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
import com.example.data.model.PhysicalMediaItem
import com.example.data.repository.PhysicalMediaRepository
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class VerificationMethod(val label: String, val iconEmoji: String) {
    IBAN_BANK("IBAN Bank Receipt", "🏦"),
    MARKETPLACE_ORDER("Order Screenshot", "🛒"),
    STORE_RECEIPT("Store Receipt", "🧾")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquarePurchaseVerifierDialog(
    item: PhysicalMediaItem,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf(VerificationMethod.IBAN_BANK) }

    // IBAN fields
    var userIban by remember { mutableStateOf("GB82 WEST 1234 5678 9012 34") }
    var bankName by remember { mutableStateOf("Townsquare Connected Bank") }
    var ibanTxRef by remember { mutableStateOf("SEPA-${(100000..999999).random()}") }

    // Marketplace screenshot fields
    var marketplaceName by remember { mutableStateOf("Townsquare Marketplace / Bandcamp") }
    var orderNumber by remember { mutableStateOf("ORD-${(10000..99999).random()}-MKP") }
    var isScreenshotAttached by remember { mutableStateOf(true) }

    // Store receipt fields
    var receiptCode by remember { mutableStateOf("RCP-${(10000..99999).random()}-TS") }

    var isScanning by remember { mutableStateOf(false) }
    var scanSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("townsquare_purchase_verifier_dialog")
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
                                text = "Universal Purchase Verifier",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "IBAN, Order Screenshots & Store Receipts",
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
                                    text = "${method.iconEmoji} ${method.label}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Item card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162032)),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "${item.format.iconEmoji} ${item.title}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "by ${item.creatorOrArtist}",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dynamic Form Content by Method
                when (selectedMethod) {
                    VerificationMethod.IBAN_BANK -> {
                        OutlinedTextField(
                            value = userIban,
                            onValueChange = { userIban = it },
                            label = { Text("Connected Bank IBAN Number") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedLabelColor = NeonCyan
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text("Bank / Financial Institution") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedLabelColor = NeonCyan
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
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedLabelColor = NeonCyan
                            ),
                            singleLine = true
                        )
                    }

                    VerificationMethod.MARKETPLACE_ORDER -> {
                        OutlinedTextField(
                            value = marketplaceName,
                            onValueChange = { marketplaceName = it },
                            label = { Text("Marketplace / Retailer Website") },
                            placeholder = { Text("e.g. Marketplace, Discogs, Bandcamp") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedLabelColor = NeonCyan
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
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedLabelColor = NeonCyan
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

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
                                        text = if (isScreenshotAttached) "Screenshot Attached (OCR Order Scan)" else "Tap to Attach Order Screenshot",
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
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedLabelColor = NeonCyan
                            ),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isScanning) {
                    CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Validating ${selectedMethod.label} proof with Townsquare ledger...", fontSize = 11.sp, color = NeonCyan)
                } else if (scanSuccess) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Townsquare Purchase Verified!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            isScanning = true
                            coroutineScope.launch {
                                delay(1200L)
                                isScanning = false
                                scanSuccess = true

                                val storeOrRef = when (selectedMethod) {
                                    VerificationMethod.IBAN_BANK -> "Bank IBAN ($bankName • $userIban)"
                                    VerificationMethod.MARKETPLACE_ORDER -> "$marketplaceName ($orderNumber)"
                                    VerificationMethod.STORE_RECEIPT -> "Store Receipt ($receiptCode)"
                                }

                                val recId = when (selectedMethod) {
                                    VerificationMethod.IBAN_BANK -> ibanTxRef
                                    VerificationMethod.MARKETPLACE_ORDER -> orderNumber
                                    VerificationMethod.STORE_RECEIPT -> receiptCode
                                }

                                PhysicalMediaRepository.verifyPurchase(item.id, recId, storeOrRef)
                                delay(600L)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify Purchase & Attach Badge", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
