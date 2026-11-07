package com.example.ui.plus.game3d

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.*

// 3D Engine Vector3D & Matrix Math
data class Vector3D(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: Vector3D) = Vector3D(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Vector3D) = Vector3D(x - o.x, y - o.y, z - o.z)
    operator fun times(s: Float) = Vector3D(x * s, y * s, z * s)
    fun length() = sqrt(x * x + y * y + z * z)
}

data class Polygon3D(
    val vertices: List<Vector3D>,
    val color: Color,
    val isWireframe: Boolean = false,
    val label: String? = null
)

// Open World Entity / Landmark
data class WorldLandmark(
    val id: String,
    val name: String,
    val position: Vector3D,
    val type: LandmarkType,
    val radius: Float = 15f
)

enum class LandmarkType { MONUMENT, TOWER, TREE, PAVILION, WINDMILL }

@Composable
fun TownsquareHorizonGameApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Player Camera State
    var camX by remember { mutableStateOf(0f) }
    var camY by remember { mutableStateOf(12f) } // Height above ground
    var camZ by remember { mutableStateOf(-40f) }
    var camYaw by remember { mutableStateOf(0f) } // Rotation angle in radians
    var camPitch by remember { mutableStateOf(0.1f) }

    // Engine Render Settings
    var renderMode by remember { mutableStateOf(0) } // 0 = Shaded Poly, 1 = Wireframe, 2 = Neon Cyber
    var timeOfDay by remember { mutableStateOf(0.35f) } // 0.0 = Midnight, 0.25 = Sunrise, 0.5 = Noon, 0.75 = Sunset
    var isDayNightAuto by remember { mutableStateOf(true) }
    var weatherMode by remember { mutableStateOf(0) } // 0 = Clear, 1 = Foggy, 2 = Starry Night
    var isFlyMode by remember { mutableStateOf(false) }

    // Game Discovery State
    val landmarks = remember {
        listOf(
            WorldLandmark("lm_1", "Central Printing Hall", Vector3D(0f, 0f, 60f), LandmarkType.MONUMENT, 20f),
            WorldLandmark("lm_2", "Harbor Lighthouse", Vector3D(120f, 0f, 150f), LandmarkType.TOWER, 15f),
            WorldLandmark("lm_3", "Old Quarter Windmill", Vector3D(-100f, 0f, 100f), LandmarkType.WINDMILL, 18f),
            WorldLandmark("lm_4", "Civic Observatory", Vector3D(50f, 25f, 240f), LandmarkType.MONUMENT, 25f),
            WorldLandmark("lm_5", "North Pine Grove", Vector3D(-80f, 0f, 180f), LandmarkType.TREE, 30f)
        )
    }

    var discoveredLandmarks by remember { mutableStateOf(setOf<String>()) }
    var fps by remember { mutableIntStateOf(60) }
    var triangleCount by remember { mutableIntStateOf(1420) }

    // Time of day auto ticker
    LaunchedEffect(isDayNightAuto) {
        while (isActive && isDayNightAuto) {
            delay(100L)
            timeOfDay = (timeOfDay + 0.001f) % 1.0f
        }
    }

    // Check Landmark Proximity
    LaunchedEffect(camX, camZ) {
        val playerPos = Vector3D(camX, camY, camZ)
        landmarks.forEach { lm ->
            val dist = (playerPos - lm.position).length()
            if (dist < lm.radius && !discoveredLandmarks.contains(lm.id)) {
                discoveredLandmarks = discoveredLandmarks + lm.id
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .testTag("horizon_game_app")
    ) {
        // 3D Canvas Viewport
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        camYaw += dragAmount.x * 0.005f
                        camPitch = (camPitch - dragAmount.y * 0.005f).coerceIn(-1.2f, 1.2f)
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val cx = width / 2f
            val cy = height / 2f
            val fov = 450f

            // Sky & Ground Colors based on Time of Day
            val skyColor = when {
                timeOfDay < 0.2f || timeOfDay > 0.8f -> Color(0xFF050814) // Night
                timeOfDay < 0.3f -> Color(0xFF2A1B3D) // Dawn
                timeOfDay < 0.7f -> Color(0xFF00B4D8) // Daytime Sky
                else -> Color(0xFF3B1F2B) // Sunset
            }

            val groundColor = when {
                timeOfDay < 0.2f || timeOfDay > 0.8f -> Color(0xFF0A1118)
                timeOfDay < 0.3f -> Color(0xFF1E1E2C)
                timeOfDay < 0.7f -> Color(0xFF1B3B2B)
                else -> Color(0xFF2E1C14)
            }

            // Draw Horizon Sky & Ground
            drawRect(color = skyColor, topLeft = Offset.Zero, size = androidx.compose.ui.geometry.Size(width, cy + camPitch * 200f))
            drawRect(color = groundColor, topLeft = Offset(0f, cy + camPitch * 200f), size = androidx.compose.ui.geometry.Size(width, cy - camPitch * 200f))

            // Procedural Open World Terrain Grid & Hills
            val gridStep = 40f
            val viewDistance = 8; // rows
            val playerGridX = (camX / gridStep).toInt()
            val playerGridZ = (camZ / gridStep).toInt()

            for (gx in (playerGridZ - viewDistance)..(playerGridZ + viewDistance)) {
                for (gz in (playerGridZ - viewDistance)..(playerGridZ + viewDistance)) {
                    val wx = gx * gridStep
                    val wz = gz * gridStep

                    // Terrain height function (rolling hills)
                    val h1 = sin(wx * 0.02f) * cos(wz * 0.02f) * 18f
                    val h2 = cos(wx * 0.05f + wz * 0.05f) * 8f
                    val terrainHeight = h1 + h2

                    // Transform world vertex to camera space
                    val dx = wx - camX
                    val dy = terrainHeight - camY
                    val dz = wz - camZ

                    // Rotate around Y (Yaw)
                    val cosY = cos(-camYaw)
                    val sinY = sin(-camYaw)
                    val rx = dx * cosY - dz * sinY
                    val rz = dx * sinY + dz * cosY

                    if (rz > 2f) { // in front of camera
                        val screenX = cx + (rx / rz) * fov
                        val screenY = cy + camPitch * 200f - (dy / rz) * fov

                        // Draw terrain tile / marker
                        val tileColor = if ((gx + gz) % 2 == 0) groundColor else groundColor.copy(alpha = 0.85f)
                        if (renderMode == 1) {
                            drawCircle(color = NeonCyan.copy(alpha = 0.3f), radius = 3f, center = Offset(screenX, screenY))
                        } else {
                            drawCircle(color = tileColor, radius = 6f / (rz * 0.05f).coerceAtLeast(0.5f), center = Offset(screenX, screenY))
                        }
                    }
                }
            }

            // Draw Landmarks in 3D
            landmarks.forEach { lm ->
                val dx = lm.position.x - camX
                val dy = lm.position.y - camY
                val dz = lm.position.z - camZ

                val cosY = cos(-camYaw)
                val sinY = sin(-camYaw)
                val rx = dx * cosY - dz * sinY
                val rz = dx * sinY + dz * cosY

                if (rz > 1f) {
                    val screenX = cx + (rx / rz) * fov
                    val screenY = cy + camPitch * 200f - (dy / rz) * fov
                    val scale = (fov / rz).coerceIn(5f, 250f)

                    val landmarkColor = when(lm.type) {
                        LandmarkType.MONUMENT -> Color(0xFFFFB300)
                        LandmarkType.TOWER -> Color(0xFF00D2FF)
                        LandmarkType.WINDMILL -> Color(0xFF30D158)
                        LandmarkType.TREE -> Color(0xFF2ECC71)
                        LandmarkType.PAVILION -> Color(0xFFA855F7)
                    }

                    if (renderMode == 1) {
                        drawRect(
                            color = landmarkColor,
                            topLeft = Offset(screenX - scale / 2, screenY - scale),
                            size = androidx.compose.ui.geometry.Size(scale, scale * 2),
                            style = Stroke(width = 2f)
                        )
                    } else {
                        drawRect(
                            color = landmarkColor,
                            topLeft = Offset(screenX - scale / 2, screenY - scale),
                            size = androidx.compose.ui.geometry.Size(scale, scale * 2)
                        )
                    }
                }
            }
        }

        // HUD & Controls Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header & Diagnostics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F172A).copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎮", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Townsquare Horizon 3D", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.White)
                            Text("Open World Engine • FPS: $fps | Tris: $triangleCount", fontSize = 10.sp, color = NeonCyan)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Render Mode Toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.clickable { renderMode = (renderMode + 1) % 3 }
                    ) {
                        Text(
                            text = when(renderMode) { 0 -> "Shaded" ; 1 -> "Wireframe" ; else -> "Cyber" },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White)
                    }
                }
            }

            // Discovery Quests Widget
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.85f),
                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.4f)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("🧭 WORLD DISCOVERIES (${discoveredLandmarks.size}/${landmarks.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                    Spacer(modifier = Modifier.height(4.dp))
                    landmarks.forEach { lm ->
                        val found = discoveredLandmarks.contains(lm.id)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (found) "✅" else "🔒", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(lm.name, fontSize = 11.sp, color = if (found) Color.White else Color.Gray)
                        }
                    }
                }
            }

            // Bottom Movement D-Pad & Camera Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Movement D-Pad (Forward, Backward, Left, Right)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = {
                            val speed = 4f
                            camX += sin(camYaw) * speed
                            camZ += cos(camYaw) * speed
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        modifier = Modifier.size(48.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Forward")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { camYaw -= 0.2f },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = NeonCyan),
                            modifier = Modifier.size(48.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Turn Left")
                        }
                        Button(
                            onClick = { camYaw += 0.2f },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = NeonCyan),
                            modifier = Modifier.size(48.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Turn Right")
                        }
                    }
                    Button(
                        onClick = {
                            val speed = 4f
                            camX -= sin(camYaw) * speed
                            camZ -= cos(camYaw) * speed
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        modifier = Modifier.size(48.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Backward")
                    }
                }

                // Fly / Jump & Speed Controls
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Time: ${(timeOfDay * 24).toInt()}:00", fontSize = 11.sp, color = NeonCyan)
                            Switch(
                                checked = isDayNightAuto,
                                onCheckedChange = { isDayNightAuto = it },
                                modifier = Modifier.scale(0.7f)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { camY += 4f },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ascend ▲", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { camY = (camY - 4f).coerceAtLeast(2f) },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Descend ▼", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
