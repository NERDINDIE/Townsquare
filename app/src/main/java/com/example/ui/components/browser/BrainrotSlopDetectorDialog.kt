package com.example.ui.components.browser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.detector.BrainrotDetectionResult
import com.example.ui.detector.BrainrotSlopDetector
import com.example.ui.detector.SlopSeverity
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.launch

@Composable
fun BrainrotSlopDetectorDialog(
    isOpen: Boolean,
    initialUrlOrTitle: String = "",
    initialSnippet: String = "",
    onDismissRequest: () -> Unit
) {
    if (!isOpen) return

    val scope = rememberCoroutineScope()
    var targetInput by remember(initialUrlOrTitle) { mutableStateOf(initialUrlOrTitle) }
    var textSnippetInput by remember(initialSnippet) { mutableStateOf(initialSnippet) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember {
        mutableStateOf<BrainrotDetectionResult?>(
            if (initialUrlOrTitle.isNotBlank() || initialSnippet.isNotBlank()) {
                BrainrotSlopDetector.analyzeContentHeuristically(initialUrlOrTitle, initialSnippet)
            } else null
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("brainrot_detector_dialog"),
        shape = RoundedCornerShape(16.dp),
        containerColor = Color(0xFF0D1524),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEF4444).copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🛡️", fontSize = 18.sp)
                    }
                }
                Column {
                    Text(
                        text = "Brainrot & Slop Detector",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Audit Community posts, web pages & outer internet for low-effort junk",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Input Fields
                OutlinedTextField(
                    value = targetInput,
                    onValueChange = { targetInput = it },
                    label = { Text("URL, Video Headline, or Topic Title") },
                    placeholder = { Text("e.g. https://tiktok.com/@clip or Clickbait title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("slop_detector_target_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = textSnippetInput,
                    onValueChange = { textSnippetInput = it },
                    label = { Text("Body text, subtitles, or captions (optional)") },
                    placeholder = { Text("Paste article snippet, commentary or dialogue...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp, max = 130.dp)
                        .testTag("slop_detector_snippet_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Manual Training Data Section for Detector
                var isTrainingExpanded by remember { mutableStateOf(false) }
                var newTrainingPhraseInput by remember { mutableStateOf("") }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF131D2E),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isTrainingExpanded = !isTrainingExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎓", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Custom Detector Training Data",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${BrainrotSlopDetector.customTrainingPhrases.size} custom trained slop patterns",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonCyan
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isTrainingExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = NeonCyan
                            )
                        }

                        if (isTrainingExpanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = newTrainingPhraseInput,
                                onValueChange = { newTrainingPhraseInput = it },
                                label = { Text("Add custom slop phrase or keyword") },
                                placeholder = { Text("e.g. 'fake news bait', 'bot loop'") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    if (newTrainingPhraseInput.isNotBlank()) {
                                        BrainrotSlopDetector.addTrainingPhrase(newTrainingPhraseInput)
                                        newTrainingPhraseInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text("+ Train Detector with Phrase", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (BrainrotSlopDetector.customTrainingPhrases.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Trained Patterns:", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                BrainrotSlopDetector.customTrainingPhrases.forEach { phrase ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("\"$phrase\"", fontSize = 11.sp, color = Color.White, modifier = Modifier.weight(1f))
                                        IconButton(
                                            onClick = { BrainrotSlopDetector.removeTrainingPhrase(phrase) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }

                // Quick presets buttons to test outer internet content
                Text(
                    text = "Quick Test Presets (Outer Web & Feed):",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.LightGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .clickable {
                                targetInput = "https://viral-clip.com/watch-till-end"
                                textSnippetInput = "BRO REALLY SAID THAT 💀 wait until the end!! like and subscribe for part 2 no cap fr fr skibidi rizz infinite money glitch"
                                analysisResult = BrainrotSlopDetector.analyzeContentHeuristically(targetInput, textSnippetInput)
                            }
                            .testTag("preset_brainrot_sample")
                    ) {
                        Text(
                            text = "⚡ Viral Slop",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .clickable {
                                targetInput = "The Future of Digital Media Integration"
                                textSnippetInput = "In today's fast-paced digital world, it is important to remember the profound tapestry of technological breakthroughs that revolutionize the landscape."
                                analysisResult = BrainrotSlopDetector.analyzeContentHeuristically(targetInput, textSnippetInput)
                            }
                            .testTag("preset_ai_cliche_sample")
                    ) {
                        Text(
                            text = "🤖 AI Cliches",
                            fontSize = 11.sp,
                            color = WarmAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .clickable {
                                targetInput = "Townsquare Civic Gazette: Old Port Heritage"
                                textSnippetInput = "Archival records from the 1894 lighthouse committee illuminate the maritime navigation practices used prior to modern radar instrumentation."
                                analysisResult = BrainrotSlopDetector.analyzeContentHeuristically(targetInput, textSnippetInput)
                            }
                            .testTag("preset_clean_sample")
                    ) {
                        Text(
                            text = "🏛️ Clean Civic",
                            fontSize = 11.sp,
                            color = Color(0xFF10B981),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Analyze Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            isAnalyzing = true
                            scope.launch {
                                analysisResult = BrainrotSlopDetector.analyzeWithAiFlow(targetInput, textSnippetInput)
                                isAnalyzing = false
                            }
                        },
                        enabled = !isAnalyzing && (targetInput.isNotBlank() || textSnippetInput.isNotBlank()),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("slop_detector_analyze_ai_button")
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF003544), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Analyzing...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Deep AI Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            analysisResult = BrainrotSlopDetector.analyzeContentHeuristically(targetInput, textSnippetInput)
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.testTag("slop_detector_quick_scan_button")
                    ) {
                        Text("Fast Scan", fontSize = 12.sp, color = Color.White)
                    }
                }

                // Display Results
                analysisResult?.let { res ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
                        border = BorderStroke(1.dp, Color(res.severity.badgeColorHex).copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .testTag("slop_detector_results_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(res.severity.badgeColorHex).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(res.severity.badgeColorHex))
                                ) {
                                    Text(
                                        text = res.severity.label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(res.severity.badgeColorHex),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = "Slop Index: ${res.score}/100",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(res.severity.badgeColorHex)
                                )
                            }

                            // Authenticity Progress Bar
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Authenticity Integrity", fontSize = 10.sp, color = Color.LightGray)
                                    Text("${res.authenticityScore}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                LinearProgressIndicator(
                                    progress = { res.authenticityScore / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = if (res.authenticityScore >= 60) Color(0xFF10B981) else Color(0xFFEF4444),
                                    trackColor = Color(0xFF1E293B),
                                )
                            }

                            Text(
                                text = res.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )

                            if (res.triggersDetected.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Detected Slop Patterns:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                    res.triggersDetected.forEach { trigger ->
                                        Text("• $trigger", fontSize = 10.sp, color = Color(0xFFFCA5A5))
                                    }
                                }
                            }

                            if (res.linguisticPatterns.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Formulaic AI Syntax:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                                    res.linguisticPatterns.forEach { ling ->
                                        Text("• $ling", fontSize = 10.sp, color = Color(0xFFFDE68A))
                                    }
                                }
                            }

                            if (res.structuralIssues.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Structural Traps:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    res.structuralIssues.forEach { st ->
                                        Text("• $st", fontSize = 10.sp, color = Color(0xFFA5F3FC))
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0B132B),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("💡", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = res.recommendation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (res.isAiGeneratedAssessment) {
                                Text(
                                    text = "✨ Verified via Gemini AI Content Quality Audit",
                                    fontSize = 9.sp,
                                    color = NeonCyan.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("slop_detector_close_button")
            ) {
                Text("Close", color = NeonCyan)
            }
        }
    )
}
