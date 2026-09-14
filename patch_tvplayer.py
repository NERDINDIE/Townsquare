with open("app/src/main/java/com/example/ui/components/TvComponents.kt", "r") as f:
    content = f.read()

# 1. Update signature
content = content.replace("    onToggleScanlineFx: () -> Unit,\n    onNextChannel: () -> Unit,", "    onToggleScanlineFx: () -> Unit,\n    onToggleRecording: (() -> Unit)? = null,\n    onNextChannel: () -> Unit,")

# 2. Add chat overlay state
content = content.replace("var showQualityDialog by remember { mutableStateOf(false) }", "var showQualityDialog by remember { mutableStateOf(false) }\n    var showChatOverlay by remember { mutableStateOf(false) }")

# 3. Add Chat Overlay UI
chat_ui = """
        // Chat Overlay
        if (showChatOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .align(Alignment.CenterEnd)
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    Text("Live Chat", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom) {
                        val messages = listOf(
                            "Alex: This broadcast is amazing!",
                            "Sam_99: I agree, the visuals are stunning.",
                            "Chris: Can't wait for the next segment.",
                            "Devin: Anyone know what time it ends?"
                        )
                        messages.forEach { msg ->
                            Text(msg, style = MaterialTheme.typography.bodySmall, color = Color.LightGray, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.weight(1f).height(36.dp).background(Color.DarkGray.copy(alpha=0.5f), RoundedCornerShape(18.dp)).padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text("Say something...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                            Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NeonCyan, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 2. Top Watermark & Status Overlay
"""
content = content.replace("        // 2. Top Watermark & Status Overlay", chat_ui)

# 4. Add Chat and Record buttons
buttons = """
                        // Chat Toggle
                        IconButton(
                            onClick = { showChatOverlay = !showChatOverlay },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.ChatBubbleOutline,
                                contentDescription = "Toggle Chat",
                                tint = if (showChatOverlay) NeonCyan else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Record Button
                        if (onToggleRecording != null) {
                            IconButton(onClick = onToggleRecording, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.RadioButtonChecked,
                                    contentDescription = "Record",
                                    tint = if (channel.isRecording) Color.Red else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Fullscreen
"""
content = content.replace("                        // Fullscreen", buttons)

with open("app/src/main/java/com/example/ui/components/TvComponents.kt", "w") as f:
    f.write(content)
