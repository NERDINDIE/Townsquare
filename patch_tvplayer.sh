cat << 'INNER_EOF' > app/src/main/java/com/example/ui/components/TvComponents.kt.patch
--- app/src/main/java/com/example/ui/components/TvComponents.kt
+++ app/src/main/java/com/example/ui/components/TvComponents.kt
@@ -83,6 +83,7 @@
     onToggleFullscreen: () -> Unit,
     onSelectQuality: (String) -> Unit,
     onToggleScanlineFx: () -> Unit,
+    onToggleRecording: (() -> Unit)? = null,
     onNextChannel: () -> Unit,
     onPrevChannel: () -> Unit,
     modifier: Modifier = Modifier
@@ -90,6 +91,7 @@
     var showQualityDialog by remember { mutableStateOf(false) }
+    var showChatOverlay by remember { mutableStateOf(false) }
 
     val infiniteTransition = rememberInfiniteTransition(label = "broadcast_anim")
@@ -156,6 +158,54 @@
         }
 
+        // Chat Overlay
+        if (showChatOverlay) {
+            Box(
+                modifier = Modifier
+                    .fillMaxHeight()
+                    .width(280.dp)
+                    .align(Alignment.CenterEnd)
+                    .background(Color.Black.copy(alpha = 0.6f))
+            ) {
+                Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
+                    Text("Live Chat", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
+                    Spacer(modifier = Modifier.height(8.dp))
+                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom) {
+                        // Sample chat messages
+                        val messages = listOf(
+                            "Alex: This broadcast is amazing!",
+                            "Sam_99: I agree, the visuals are stunning.",
+                            "Chris: Can't wait for the next segment.",
+                            "Devin: Anyone know what time it ends?"
+                        )
+                        messages.forEach { msg ->
+                            Text(msg, style = MaterialTheme.typography.bodySmall, color = Color.LightGray, modifier = Modifier.padding(vertical = 2.dp))
+                        }
+                    }
+                    Spacer(modifier = Modifier.height(8.dp))
+                    Row(verticalAlignment = Alignment.CenterVertically) {
+                        Box(
+                            modifier = Modifier.weight(1f).height(36.dp).background(Color.DarkGray.copy(alpha=0.5f), RoundedCornerShape(18.dp)).padding(horizontal = 12.dp),
+                            contentAlignment = Alignment.CenterStart
+                        ) {
+                            Text("Say something...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
+                        }
+                        Spacer(modifier = Modifier.width(8.dp))
+                        IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
+                            Icon(Icons.Default.Send, contentDescription = "Send", tint = NeonCyan, modifier = Modifier.size(16.dp))
+                        }
+                    }
+                }
+            }
+        }
+
         // 2. Top Watermark & Status Overlay
         Row(
@@ -370,6 +420,29 @@
                         }
 
+                        // Chat Toggle
+                        IconButton(
+                            onClick = { showChatOverlay = !showChatOverlay },
+                            modifier = Modifier.size(36.dp)
+                        ) {
+                            Icon(
+                                imageVector = Icons.Default.ChatBubbleOutline,
+                                contentDescription = "Toggle Chat",
+                                tint = if (showChatOverlay) NeonCyan else Color.White,
+                                modifier = Modifier.size(18.dp)
+                            )
+                        }
+
+                        // Record Button
+                        if (onToggleRecording != null) {
+                            IconButton(onClick = onToggleRecording, modifier = Modifier.size(36.dp)) {
+                                Icon(
+                                    imageVector = Icons.Default.RadioButtonChecked,
+                                    contentDescription = "Record",
+                                    tint = if (channel.isRecording) Color.Red else Color.White,
+                                    modifier = Modifier.size(18.dp)
+                                )
+                            }
+                        }
+
                         // Fullscreen
INNER_EOF
patch -p0 < app/src/main/java/com/example/ui/components/TvComponents.kt.patch
