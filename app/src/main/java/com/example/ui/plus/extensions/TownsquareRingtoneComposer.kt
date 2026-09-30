package com.example.ui.plus.extensions

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

data class RingtoneNote(
    val noteName: String, // e.g. "C4", "D4", "E4", "F4", "G4", "A4", "B4", "C5", "REST"
    val frequency: Double, // Hz
    val durationMs: Int = 200
)

data class PresetRingtone(
    val id: String,
    val title: String,
    val subtitle: String,
    val tempoBpm: Int,
    val notes: List<RingtoneNote>
)

object RingtoneFrequencies {
    val NOTE_MAP = mapOf(
        "C4" to 261.63,
        "D4" to 293.66,
        "E4" to 329.63,
        "F4" to 349.23,
        "G4" to 392.00,
        "A4" to 440.00,
        "B4" to 493.88,
        "C5" to 523.25,
        "D5" to 587.33,
        "E5" to 659.25,
        "F5" to 698.46,
        "G5" to 783.99,
        "A5" to 880.00,
        "REST" to 0.0
    )

    fun createNote(name: String, durationMs: Int = 200): RingtoneNote {
        return RingtoneNote(name, NOTE_MAP[name] ?: 0.0, durationMs)
    }

    val PRESETS = listOf(
        PresetRingtone(
            id = "civic_bell",
            title = "🔔 Townsquare Civic Chime",
            subtitle = "Modern classic civic notification bell",
            tempoBpm = 140,
            notes = listOf(
                createNote("E4", 250),
                createNote("G4", 250),
                createNote("C5", 400),
                createNote("REST", 100),
                createNote("G4", 250),
                createNote("E4", 500)
            )
        ),
        PresetRingtone(
            id = "nokia_nostalgia",
            title = "📱 Gran Vals Nostalgia",
            subtitle = "Iconic 90s monophonic phone cadence",
            tempoBpm = 160,
            notes = listOf(
                createNote("E5", 150),
                createNote("D5", 150),
                createNote("F4", 250),
                createNote("G4", 250),
                createNote("C5", 150),
                createNote("B4", 150),
                createNote("D4", 250),
                createNote("E4", 250),
                createNote("B4", 150),
                createNote("A4", 150),
                createNote("C4", 250),
                createNote("E4", 250),
                createNote("A4", 450)
            )
        ),
        PresetRingtone(
            id = "retro_8bit",
            title = "🕹️ 8-Bit Retro Anthem",
            subtitle = "Upbeat arcade chip synth melody",
            tempoBpm = 180,
            notes = listOf(
                createNote("C4", 120),
                createNote("E4", 120),
                createNote("G4", 120),
                createNote("C5", 240),
                createNote("G4", 120),
                createNote("C5", 360),
                createNote("REST", 80),
                createNote("D5", 150),
                createNote("E5", 300)
            )
        ),
        PresetRingtone(
            id = "analog_rotary",
            title = "☎️ Rotary Dial Telephone Bell",
            subtitle = "Classic dual-tone analog ringer",
            tempoBpm = 120,
            notes = listOf(
                createNote("A4", 80),
                createNote("F4", 80),
                createNote("A4", 80),
                createNote("F4", 80),
                createNote("A4", 80),
                createNote("REST", 300),
                createNote("A4", 80),
                createNote("F4", 80),
                createNote("A4", 80),
                createNote("F4", 80),
                createNote("A4", 80)
            )
        )
    )
}

@Composable
fun TownsquareRingtoneComposer(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeNotes by remember {
        mutableStateOf(RingtoneFrequencies.PRESETS.first().notes)
    }
    var ringtoneTitle by remember { mutableStateOf("My Custom Ringtone") }
    var tempoBpm by remember { mutableIntStateOf(140) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPlayingIndex by remember { mutableIntStateOf(-1) }
    var selectedWaveform by remember { mutableStateOf("SINE") } // SINE, SQUARE, RETRO_SAW
    var isSavedAlertActive by remember { mutableStateOf(false) }

    fun playTonePcm(frequency: Double, durationMs: Int, waveform: String) {
        if (frequency <= 0) {
            Thread.sleep(durationMs.toLong())
            return
        }
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            if (numSamples <= 0) return
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val rawVal = when (waveform) {
                    "SQUARE" -> if (sin(2.0 * Math.PI * frequency * t) >= 0) 0.6 else -0.6
                    "RETRO_SAW" -> 2.0 * ((frequency * t) - kotlin.math.floor(frequency * t + 0.5)) * 0.5
                    else -> sin(2.0 * Math.PI * frequency * t) * 0.8 // SINE
                }
                // Apply subtle envelope attack/decay to prevent audio pop
                val envelope = when {
                    i < 200 -> i / 200.0
                    i > numSamples - 200 -> (numSamples - i) / 200.0
                    else -> 1.0
                }
                buffer[i] = (rawVal * envelope * Short.MAX_VALUE).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong())
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {}
    }

    fun playSequence() {
        if (isPlaying) return
        isPlaying = true
        scope.launch(Dispatchers.Default) {
            for (i in activeNotes.indices) {
                if (!isActive) break
                currentPlayingIndex = i
                val note = activeNotes[i]
                playTonePcm(note.frequency, note.durationMs, selectedWaveform)
            }
            currentPlayingIndex = -1
            isPlaying = false
        }
    }

    fun playSingleNote(noteName: String) {
        scope.launch(Dispatchers.Default) {
            val freq = RingtoneFrequencies.NOTE_MAP[noteName] ?: 440.0
            playTonePcm(freq, 200, selectedWaveform)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("ringtone_composer_hero")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = WarmAmber.copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("🎼 Ringtone & Chime Composer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Text("Step sequencer & sound synthesizer for custom alert tones", style = MaterialTheme.typography.labelSmall, color = DarkTextSecondary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonCyan
                        ) {
                            Text(
                                text = "STUDIO FX",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF003544),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Transport Bar & Ringtone Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = ringtoneTitle,
                            onValueChange = { ringtoneTitle = it },
                            label = { Text("Ringtone Title") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Button(
                            onClick = { playSequence() },
                            enabled = !isPlaying && activeNotes.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) DarkBorder else NeonCyan,
                                contentColor = Color(0xFF003544)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(56.dp).testTag("play_ringtone_btn")
                        ) {
                            Icon(if (isPlaying) Icons.Default.VolumeUp else Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isPlaying) "Playing" else "Play", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Waveform & Preset Chimes
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("PRESET CLASSIC CHIMES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarmAmber)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(RingtoneFrequencies.PRESETS) { preset ->
                        val isSelected = activeNotes == preset.notes
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) WarmAmber.copy(alpha = 0.25f) else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) WarmAmber else DarkBorder),
                            modifier = Modifier.clickable {
                                activeNotes = preset.notes
                                ringtoneTitle = preset.title
                                tempoBpm = preset.tempoBpm
                            }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(preset.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) WarmAmber else Color.White)
                                Text(preset.subtitle, fontSize = 10.sp, color = DarkTextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // Waveform Synth Engine
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("SYNTHESIS WAVEFORM", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("SINE" to "〰️ Pure Sine", "SQUARE" to "⏹️ 8-Bit Square", "RETRO_SAW" to "📐 Retro Saw").forEach { (type, label) ->
                        val isSel = selectedWaveform == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) NeonCyan else DarkSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder),
                            modifier = Modifier.weight(1f).clickable { selectedWaveform = type }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color(0xFF003544) else Color.White,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Sequencer Roll / Active Notes
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("NOTE SEQUENCE (${activeNotes.size} Steps)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        TextButton(onClick = { activeNotes = emptyList() }) {
                            Text("Clear All", fontSize = 11.sp, color = CoralRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (activeNotes.isEmpty()) {
                        Text("No notes added yet. Tap on the piano keyboard below to compose your melody!", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 12.dp))
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            itemsIndexed(activeNotes) { index, note ->
                                val isCurrent = currentPlayingIndex == index
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCurrent) MintTeal else if (note.noteName == "REST") DarkSurfaceVariant else NeonCyan.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, if (isCurrent) MintTeal else if (note.noteName == "REST") DarkBorder else NeonCyan),
                                    modifier = Modifier.clickable {
                                        // Remove note on click
                                        activeNotes = activeNotes.filterIndexed { i, _ -> i != index }
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(note.noteName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isCurrent) Color(0xFF003544) else Color.White)
                                        Text("${note.durationMs}ms", fontSize = 9.sp, color = DarkTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Piano Roll Keyboard to Add Notes
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("PIANO KEYPAD (Tap note to append)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarmAmber)

                val octaves = listOf(
                    listOf("C4", "D4", "E4", "F4", "G4", "A4", "B4"),
                    listOf("C5", "D5", "E5", "F5", "G5", "A5", "REST")
                )

                octaves.forEach { rowNotes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowNotes.forEach { noteName ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (noteName == "REST") DarkSurfaceVariant else Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (noteName == "REST") DarkBorder else NeonCyan.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clickable {
                                        playSingleNote(noteName)
                                        activeNotes = activeNotes + RingtoneFrequencies.createNote(noteName, 200)
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = noteName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (noteName == "REST") WarmAmber else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Save Ringtone Action
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Apply as Active Ringtone", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Text(if (isSavedAlertActive) "Active Townsquare notification chime ✓" else "Set this synthesized sequence as your custom app alert tone", fontSize = 11.sp, color = if (isSavedAlertActive) MintTeal else DarkTextSecondary)
                    }

                    Button(
                        onClick = {
                            isSavedAlertActive = true
                            Toast.makeText(context, "🎉 Ringtone '$ringtoneTitle' applied as default alert!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Chime", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
