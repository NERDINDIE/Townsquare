package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan

/**
 * Airwaves & Inclusivity Brand Identity Icon Set.
 * Every icon is rendered inside a circle with high-contrast, modern airwaves aesthetics,
 * and functions as a button that opens the sidebar (or performs onOpenSidebar action).
 */
enum class AirwavesBrandIconType {
    TOWNSQUARE_T,       // The letter T inside the circle
    UNIFIED_INBOX,      // An envelope inside a circle
    FORECAST,           // Cloud inside a circle
    WIDGETS,            // Bricks inside a circle
    OFFLINE,            // A slashed out circle
    MARKETPLACE,        // Shopping bag inside a circle
    MAPS,               // A map pin inside a circle
    ARCADE,             // A D-pad outline inside a circle
    LINGO_LAB,          // The letter A inside a circle
    BOOKWORM,           // A book inside a circle
    PHONE,              // A phone receiver connected inside a circle with its wire
    IOT_COMPANION,      // A clock outline inside a circle
    CAMERA_HUD,         // A camera lens inside a circle
    FILE_EXPLORER       // A folder inside a circle
}

@Composable
fun AirwavesBrandIcon(
    iconType: AirwavesBrandIconType,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    circleColor: Color = NeonCyan.copy(alpha = 0.15f),
    borderColor: Color = NeonCyan.copy(alpha = 0.6f),
    iconColor: Color = NeonCyan,
    contentDescription: String = iconType.name,
    onOpenSidebar: (() -> Unit)? = null
) {
    val clickableModifier = if (onOpenSidebar != null) {
        modifier
            .size(size)
            .clip(CircleShape)
            .clickable { onOpenSidebar() }
            .testTag("airwaves_brand_icon_${iconType.name.lowercase()}")
    } else {
        modifier
            .size(size)
            .clip(CircleShape)
            .testTag("airwaves_brand_icon_${iconType.name.lowercase()}")
    }

    Surface(
        modifier = clickableModifier,
        shape = CircleShape,
        color = circleColor,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            val innerIconSize = size * 0.58f

            when (iconType) {
                // 1. Townsquare's rounded square T icon -> The letter T inside the circle
                AirwavesBrandIconType.TOWNSQUARE_T -> {
                    Text(
                        text = "T",
                        fontSize = (size.value * 0.55f).sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = iconColor
                    )
                }

                // 2. Townsquare's unified Inbox -> an envelope inside a circle
                AirwavesBrandIconType.UNIFIED_INBOX -> {
                    Icon(
                        imageVector = Icons.Default.Mail,
                        contentDescription = contentDescription,
                        tint = iconColor,
                        modifier = Modifier.size(innerIconSize)
                    )
                }

                // 3. Forecast -> Cloud inside a circle
                AirwavesBrandIconType.FORECAST -> {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = contentDescription,
                        tint = iconColor,
                        modifier = Modifier.size(innerIconSize)
                    )
                }

                // 4. Widgets -> Bricks inside a circle
                AirwavesBrandIconType.WIDGETS -> {
                    Canvas(modifier = Modifier.size(innerIconSize)) {
                        val w = this.size.width
                        val h = this.size.height
                        val strokeW = 1.8f.dp.toPx()
                        val brickH = h / 3f
                        val brickW = w / 2f

                        // Outer brick wall outline
                        drawRoundRect(
                            color = iconColor,
                            topLeft = Offset(0f, 0f),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(2f.dp.toPx(), 2f.dp.toPx()),
                            style = Stroke(width = strokeW)
                        )
                        // Horizontal divider 1
                        drawLine(
                            color = iconColor,
                            start = Offset(0f, brickH),
                            end = Offset(w, brickH),
                            strokeWidth = strokeW
                        )
                        // Horizontal divider 2
                        drawLine(
                            color = iconColor,
                            start = Offset(0f, brickH * 2),
                            end = Offset(w, brickH * 2),
                            strokeWidth = strokeW
                        )
                        // Vertical divider row 1 (staggered)
                        drawLine(
                            color = iconColor,
                            start = Offset(brickW, 0f),
                            end = Offset(brickW, brickH),
                            strokeWidth = strokeW
                        )
                        // Vertical divider row 2 (offset)
                        drawLine(
                            color = iconColor,
                            start = Offset(w * 0.28f, brickH),
                            end = Offset(w * 0.28f, brickH * 2),
                            strokeWidth = strokeW
                        )
                        drawLine(
                            color = iconColor,
                            start = Offset(w * 0.72f, brickH),
                            end = Offset(w * 0.72f, brickH * 2),
                            strokeWidth = strokeW
                        )
                        // Vertical divider row 3
                        drawLine(
                            color = iconColor,
                            start = Offset(brickW, brickH * 2),
                            end = Offset(brickW, h),
                            strokeWidth = strokeW
                        )
                    }
                }

                // 5. Offline -> A slashed out circle
                AirwavesBrandIconType.OFFLINE -> {
                    Canvas(modifier = Modifier.size(innerIconSize)) {
                        val strokeW = 2f.dp.toPx()
                        val r = (this.size.minDimension / 2f) - (strokeW / 2f)
                        val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)

                        // Circle outline
                        drawCircle(
                            color = iconColor,
                            radius = r,
                            center = centerOffset,
                            style = Stroke(width = strokeW)
                        )
                        // Diagonal slash
                        val cos45 = (r * 0.7071f)
                        drawLine(
                            color = iconColor,
                            start = Offset(centerOffset.x - cos45, centerOffset.y - cos45),
                            end = Offset(centerOffset.x + cos45, centerOffset.y + cos45),
                            strokeWidth = strokeW
                        )
                    }
                }

                // 6. Marketplace -> Shopping bag inside a circle
                AirwavesBrandIconType.MARKETPLACE -> {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = contentDescription,
                        tint = iconColor,
                        modifier = Modifier.size(innerIconSize)
                    )
                }

                // 7. Maps -> A map pin inside a circle
                AirwavesBrandIconType.MAPS -> {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = contentDescription,
                        tint = iconColor,
                        modifier = Modifier.size(innerIconSize)
                    )
                }

                // 8. Arcade -> A D-pad outline inside a circle
                AirwavesBrandIconType.ARCADE -> {
                    Canvas(modifier = Modifier.size(innerIconSize)) {
                        val w = this.size.width
                        val h = this.size.height
                        val arm = w * 0.35f
                        val strokeW = 2f.dp.toPx()

                        val path = Path().apply {
                            val x1 = (w - arm) / 2f
                            val x2 = (w + arm) / 2f
                            val y1 = (h - arm) / 2f
                            val y2 = (h + arm) / 2f

                            moveTo(x1, 0f)
                            lineTo(x2, 0f)
                            lineTo(x2, y1)
                            lineTo(w, y1)
                            lineTo(w, y2)
                            lineTo(x2, y2)
                            lineTo(x2, h)
                            lineTo(x1, h)
                            lineTo(x1, y2)
                            lineTo(0f, y2)
                            lineTo(0f, y1)
                            lineTo(x1, y1)
                            close()
                        }
                        drawPath(path = path, color = iconColor, style = Stroke(width = strokeW))
                    }
                }

                // 9. Lingo Lab -> The letter A inside a circle
                AirwavesBrandIconType.LINGO_LAB -> {
                    Text(
                        text = "A",
                        fontSize = (size.value * 0.55f).sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = iconColor
                    )
                }

                // 10. Bookworm -> A book inside a circle
                AirwavesBrandIconType.BOOKWORM -> {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = contentDescription,
                        tint = iconColor,
                        modifier = Modifier.size(innerIconSize)
                    )
                }

                // 11. Phone -> A phone receiver connected inside a circle with its wire
                AirwavesBrandIconType.PHONE -> {
                    Canvas(modifier = Modifier.size(innerIconSize)) {
                        val w = this.size.width
                        val h = this.size.height
                        val strokeW = 2f.dp.toPx()

                        // Phone handset (top-left to top-right diagonal)
                        val receiverPath = Path().apply {
                            moveTo(w * 0.18f, h * 0.25f)
                            cubicTo(w * 0.15f, h * 0.08f, w * 0.40f, h * 0.05f, w * 0.45f, h * 0.20f)
                            lineTo(w * 0.38f, h * 0.32f)
                            cubicTo(w * 0.45f, h * 0.48f, w * 0.58f, h * 0.60f, w * 0.72f, h * 0.68f)
                            lineTo(w * 0.82f, h * 0.58f)
                            cubicTo(w * 0.95f, h * 0.65f, w * 0.95f, h * 0.90f, w * 0.80f, h * 0.92f)
                            cubicTo(w * 0.40f, h * 0.98f, w * 0.08f, h * 0.60f, w * 0.18f, h * 0.25f)
                            close()
                        }
                        drawPath(path = receiverPath, color = iconColor, style = Stroke(width = strokeW))

                        // Curly wire leading from receiver base down
                        val wirePath = Path().apply {
                            moveTo(w * 0.78f, h * 0.90f)
                            cubicTo(w * 0.90f, h * 0.95f, w * 0.90f, h * 1.05f, w * 0.68f, h * 1.0f)
                            cubicTo(w * 0.45f, h * 0.95f, w * 0.45f, h * 1.1f, w * 0.25f, h * 1.05f)
                        }
                        drawPath(path = wirePath, color = iconColor, style = Stroke(width = strokeW))
                    }
                }

                // 12. IoT Companion -> A clock outline inside a circle
                AirwavesBrandIconType.IOT_COMPANION -> {
                    Canvas(modifier = Modifier.size(innerIconSize)) {
                        val strokeW = 2f.dp.toPx()
                        val r = (this.size.minDimension / 2f) - (strokeW / 2f)
                        val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)

                        // Clock outline
                        drawCircle(
                            color = iconColor,
                            radius = r,
                            center = centerOffset,
                            style = Stroke(width = strokeW)
                        )
                        // Clock hands (pointing to 3:00 / 10:10 style)
                        drawLine(
                            color = iconColor,
                            start = centerOffset,
                            end = Offset(centerOffset.x, centerOffset.y - (r * 0.55f)),
                            strokeWidth = strokeW
                        )
                        drawLine(
                            color = iconColor,
                            start = centerOffset,
                            end = Offset(centerOffset.x + (r * 0.45f), centerOffset.y),
                            strokeWidth = strokeW
                        )
                    }
                }

                // 13. Camera/HUD -> A camera lens inside a circle
                AirwavesBrandIconType.CAMERA_HUD -> {
                    Canvas(modifier = Modifier.size(innerIconSize)) {
                        val strokeW = 2f.dp.toPx()
                        val r = (this.size.minDimension / 2f) - (strokeW / 2f)
                        val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)

                        // Outer lens ring
                        drawCircle(
                            color = iconColor,
                            radius = r,
                            center = centerOffset,
                            style = Stroke(width = strokeW)
                        )
                        // Middle aperture ring
                        drawCircle(
                            color = iconColor,
                            radius = r * 0.6f,
                            center = centerOffset,
                            style = Stroke(width = strokeW * 0.8f)
                        )
                        // Inner pupil
                        drawCircle(
                            color = iconColor,
                            radius = r * 0.25f,
                            center = centerOffset
                        )
                    }
                }

                // 14. File Explorer -> A folder inside a circle
                AirwavesBrandIconType.FILE_EXPLORER -> {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = contentDescription,
                        tint = iconColor,
                        modifier = Modifier.size(innerIconSize)
                    )
                }
            }
        }
    }
}
