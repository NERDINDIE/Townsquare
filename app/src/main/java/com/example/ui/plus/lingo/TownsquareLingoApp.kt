package com.example.ui.plus.lingo

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.plus.extensions.Android10SoundEffects
import com.example.ui.theme.*

enum class LingoMode(val label: String, val icon: String) {
    DICTIONARY("Dictionary", "📖"),
    TRANSLATOR("Real-Time Translator", "🌐"),
    TUTOR("AI Language Tutor", "🤖")
}

data class LanguageOption(val code: String, val name: String, val flag: String)

val SUPPORTED_LANGUAGES = listOf(
    LanguageOption("en", "English", "🇬🇧"),
    LanguageOption("ja", "Japanese", "🇯🇵"),
    LanguageOption("es", "Spanish", "🇪🇸"),
    LanguageOption("fr", "French", "🇫🇷"),
    LanguageOption("de", "German", "🇩🇪"),
    LanguageOption("zh", "Mandarin", "🇨🇳"),
    LanguageOption("la", "Latin", "🏛️")
)

data class DictWord(
    val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val definition: String,
    val etymology: String,
    val examples: List<String>,
    val language: String = "English"
)

data class TutorMessage(
    val sender: String,
    val isUser: Boolean,
    val text: String,
    val translation: String? = null,
    val grammarCorrection: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareLingoApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(LingoMode.DICTIONARY) }

    // Dictionary State
    var dictQuery by remember { mutableStateOf("") }
    var selectedWord by remember { mutableStateOf<DictWord?>(null) }
    val dictSeed = remember {
        listOf(
            DictWord(
                word = "Ephemera",
                phonetic = "/ɪˈfɛmərə/",
                partOfSpeech = "noun",
                definition = "Things that exist or are used for only a short time, such as collectible broadsheets, printed tickets, or fleeting radio signals.",
                etymology = "From Greek ephēmeros, meaning 'lasting only one day'.",
                examples = listOf(
                    "The antiquarian archive preserved 19th-century printing ephemera.",
                    "Digital status posts are modern electronic ephemera."
                )
            ),
            DictWord(
                word = "Solfeggio",
                phonetic = "/sɒlˈfɛdʒi.oʊ/",
                partOfSpeech = "noun",
                definition = "A music solmization system assigning syllables (do, re, mi) to scale degrees, often associated with resonant acoustic healing frequencies.",
                etymology = "Italian solfeggio, from sol-fa.",
                examples = listOf(
                    "The 528 Hz solfeggio frequency was broadcast over the ambient audio channel."
                )
            ),
            DictWord(
                word = "Polyglot",
                phonetic = "/ˈpɒl.i.ɡlɒt/",
                partOfSpeech = "noun / adjective",
                definition = "A person who knows and uses several languages fluently.",
                etymology = "From Greek polyglōttos ('many-tongued').",
                examples = listOf(
                    "The district newsroom employed polyglot journalists capable of translating seven languages live."
                )
            ),
            DictWord(
                word = "Resonance",
                phonetic = "/ˈrɛzənəns/",
                partOfSpeech = "noun",
                definition = "The quality of being loud, deep, and clear, or the power to evoke enduring emotional empathy.",
                etymology = "Latin resonantia ('echo').",
                examples = listOf(
                    "The author's essay on civic pride found deep resonance across the neighborhood."
                )
            )
        )
    }

    // Translator State
    var sourceLang by remember { mutableStateOf(SUPPORTED_LANGUAGES[0]) } // English
    var targetLang by remember { mutableStateOf(SUPPORTED_LANGUAGES[1]) } // Japanese
    var inputText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }

    // Tutor State
    var tutorScenario by remember { mutableStateOf("☕ Ordering Coffee in Tokyo") }
    var tutorMessages by remember {
        mutableStateOf(
            listOf(
                TutorMessage(
                    sender = "Lingo Tutor Sensei 🤖",
                    isUser = false,
                    text = "いらっしゃいませ！ご注文は何にしますか？ (Welcome! What would you like to order today?)",
                    translation = "Welcome! What would you like to order today?"
                )
            )
        )
    }
    var tutorInput by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("lingo_back_btn")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🗣️", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Townsquare Lingo Polyglot",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = WarmAmber
                                ) {
                                    Text(
                                        text = "PLUS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF261800),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Dictionary • Translator • AI Language Tutor",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextSecondary
                            )
                        }
                    }
                }
            }

            // Mode Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LingoMode.entries.forEach { mode ->
                    val isSel = activeMode == mode
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSel) NeonCyan.copy(alpha = 0.2f) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, if (isSel) NeonCyan else Color(0xFF334155)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeMode = mode
                                Android10SoundEffects.playTrackballClick()
                            }
                            .testTag("lingo_mode_${mode.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = mode.icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = mode.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) NeonCyan else Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Content Area
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (activeMode) {
                    LingoMode.DICTIONARY -> {
                        // DICTIONARY MODE
                        val filteredWords = dictSeed.filter {
                            dictQuery.isBlank() || it.word.contains(dictQuery, ignoreCase = true) || it.definition.contains(dictQuery, ignoreCase = true)
                        }

                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            OutlinedTextField(
                                value = dictQuery,
                                onValueChange = { dictQuery = it },
                                placeholder = { Text("Search word or definition...", color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("dict_search_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredWords) { item ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF0F172A),
                                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            selectedWord = item
                                            Android10SoundEffects.playTrackballClick()
                                        }
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = item.word,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = item.phonetic,
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = NeonCyan
                                                    )
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFF1E293B)
                                                ) {
                                                    Text(
                                                        text = item.partOfSpeech,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = WarmAmber,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = item.definition,
                                                fontSize = 13.sp,
                                                color = Color(0xFFCBD5E1)
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Etymology: ${item.etymology}",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LingoMode.TRANSLATOR -> {
                        // REAL-TIME TRANSLATOR MODE
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Language Selector Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, Color(0xFF334155)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(sourceLang.flag, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(sourceLang.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                IconButton(onClick = {
                                    val temp = sourceLang
                                    sourceLang = targetLang
                                    targetLang = temp
                                    Android10SoundEffects.playTrackballClick()
                                }) {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = "Swap", tint = NeonCyan)
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF0F172A),
                                    border = BorderStroke(1.dp, Color(0xFF334155)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(targetLang.flag, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(targetLang.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Source Input Field
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = {
                                    inputText = it
                                    // Simulated Instant Real-Time Translation
                                    translatedText = if (it.isBlank()) "" else when (targetLang.code) {
                                        "ja" -> "Townsquareのリアルタイム翻訳: 「$it」"
                                        "es" -> "Traducción en tiempo real: 「$it」"
                                        "fr" -> "Traduction en temps réel: 「$it」"
                                        "de" -> "Echtzeit-Übersetzung: 「$it」"
                                        "la" -> "Interpretatio in tempore reali: 「$it」"
                                        else -> "Real-time translation: 「$it」"
                                    }
                                },
                                placeholder = { Text("Enter text to translate in real-time...", color = Color.Gray) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .testTag("translator_text_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Translation Output Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, NeonCyan),
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(targetLang.flag, fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${targetLang.name} Translation",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeonCyan
                                            )
                                        }

                                        IconButton(onClick = {
                                            Android10SoundEffects.playNotificationChime()
                                        }) {
                                            Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = WarmAmber)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = translatedText.ifBlank { "Translation will appear here instantly..." },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (translatedText.isBlank()) Color.Gray else Color.White
                                    )
                                }
                            }
                        }
                    }

                    LingoMode.TUTOR -> {
                        // AI LANGUAGE TUTOR MODE
                        Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                            // Scenario Selector
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Scenario: $tutorScenario",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmAmber
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = NeonCyan
                                    ) {
                                        Text(
                                            text = "JAPANESE PRACTICE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF003544),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Chat Dialogue List
                            LazyColumn(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(tutorMessages) { msg ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (msg.isUser) Color(0xFF005266) else Color(0xFF0F172A),
                                            border = BorderStroke(1.dp, if (msg.isUser) NeonCyan else Color(0xFF334155)),
                                            modifier = Modifier.widthIn(max = 280.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = msg.sender,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (msg.isUser) NeonCyan else WarmAmber
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = msg.text,
                                                    fontSize = 13.sp,
                                                    color = Color.White
                                                )

                                                if (msg.translation != null) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "💡 ${msg.translation}",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFFCBD5E1)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Input Box
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = tutorInput,
                                    onValueChange = { tutorInput = it },
                                    placeholder = { Text("Reply in Japanese or English...", color = Color.Gray, fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).height(46.dp).testTag("tutor_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Button(
                                    onClick = {
                                        if (tutorInput.isNotBlank()) {
                                            val userMsg = TutorMessage(
                                                sender = "You",
                                                isUser = true,
                                                text = tutorInput
                                            )
                                            val tutorResponse = TutorMessage(
                                                sender = "Lingo Tutor Sensei 🤖",
                                                isUser = false,
                                                text = "かしこまりました！「$tutorInput」ですね。 (Understood! Here is your coffee.)",
                                                translation = "Understood! Here is your coffee. Excellent accent!"
                                            )
                                            tutorMessages = tutorMessages + userMsg + tutorResponse
                                            tutorInput = ""
                                            Android10SoundEffects.playTrackballClick()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("SEND", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
