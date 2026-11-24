package com.example.ui.components.media

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhysicalMediaItem
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun ProvenanceFingerprintBanner(
    item: PhysicalMediaItem,
    onOpenTransformativeStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fp = item.fingerprint
    val isDownloaded = fp.isDownloadedFromElsewhere

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("provenance_fingerprint_banner_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDownloaded && !fp.isResharePermitted) Color(0xFF1F1810) else Color(0xFF0D1B2A)
        ),
        border = BorderStroke(
            1.dp,
            if (isDownloaded && !fp.isResharePermitted) WarmAmber.copy(alpha = 0.8f) else NeonCyan.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDownloaded) Icons.Default.Fingerprint else Icons.Default.VerifiedUser,
                        contentDescription = "Fingerprint",
                        tint = if (isDownloaded && !fp.isResharePermitted) WarmAmber else NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDownloaded) "Digital Fingerprint: #${fp.fingerprintHash}" else "Vault Original • #${fp.fingerprintHash}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (item.proofOfPurchase.isVerified) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NeonCyan.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🛡️ Verified Owner", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (isDownloaded) {
                Text(
                    text = "Origin: Downloaded from ${fp.originalUploaderName}",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (!fp.isResharePermitted) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔒 Reshare Locked (Requires >50% Derivative Work)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = onOpenTransformativeStudio,
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Transform (${fp.derivativePercentage.toInt()}%)", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Unlocked: Derivative Work Verified (${fp.derivativePercentage.toInt()}%)",
                            fontSize = 11.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Text(
                    text = "Personal Archive Item • Off to public sharing by default",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}
