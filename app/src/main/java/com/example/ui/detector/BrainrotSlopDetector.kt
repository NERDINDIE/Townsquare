package com.example.ui.detector

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import androidx.compose.runtime.mutableStateListOf

enum class SlopSeverity(val label: String, val badgeColorHex: Long) {
    CLEAN("Clean / Genuine Work", 0xFF10B981),
    LOW_RISK("Low Suspicion / Mostly Original", 0xFF06B6D4),
    MODERATE_SLOP("Moderate Brainrot / Low Effort", 0xFFF59E0B),
    SEVERE_BRAINROT("High Brainrot Slop / AI Junk", 0xFFEF4444)
}

data class BrainrotDetectionResult(
    val score: Int, // 0 - 100, where 100 is pure uncurated slop
    val severity: SlopSeverity,
    val summary: String,
    val triggersDetected: List<String>,
    val linguisticPatterns: List<String>,
    val structuralIssues: List<String>,
    val authenticityScore: Int, // 0 - 100
    val recommendation: String,
    val isAiGeneratedAssessment: Boolean = false
)

object BrainrotSlopDetector {

    // Common brainrot phrases, spam tropes, internet slop heuristics
    private val BRAINROT_PHRASES = listOf(
        "skibidi", "rizz", "gyatt", "sigma", "fanum tax", "mewing", "baby gronk",
        "livvy dunne", "grimace shake", "kai cenat", "looksmaxxing", "ohio",
        "delulu", "bussin", "no cap fr fr", "ratio", "npc energy", "brain nourishment",
        "quandale dingle", "blud think he", "bro really said"
    )

    private val ENGAGEMENT_BAIT_PATTERNS = listOf(
        "wait until the end", "wait till the end", "you won't believe what happens",
        "watch till the end", "like and subscribe or else", "part 2 in comments",
        "drop a 100 in the comments", "share with 5 friends", "mind blowing secret",
        "doctors don't want you to know", "secret algorithm trick", "tap to see who visits your profile",
        "100% real no fake", "free robux", "infinite money glitch", "satisfying soap cutting",
        "subway surfers gameplay background", "family guy clips under", "ai voice clone"
    )

    private val AI_SLOP_CLICHES = listOf(
        "in today's fast-paced digital world", "delve into", "testament to",
        "tapestry of", "beacon of hope", "it is important to remember",
        "revolutionize the landscape", "seamless integration", "ever-evolving realm",
        "journey through the depths", "unlock the true potential", "game-changer",
        "unwavering dedication", "dynamic interplay", "vibrant symphony",
        "shed light on", "plethora of", "realm of possibilities"
    )

    private val SUSPICIOUS_DOMAINS = listOf(
        "buzzfeed-clickbait", "viralcontenthub", "free-crypto-giveaway",
        "ad-farm", "fastcash-trends", "tiktokviralfeeds"
    )

    // Manual training dataset for custom slop and brainrot phrases
    val customTrainingPhrases = mutableStateListOf<String>()

    fun addTrainingPhrase(phrase: String) {
        val trimmed = phrase.trim().lowercase()
        if (trimmed.isNotBlank() && !customTrainingPhrases.contains(trimmed)) {
            customTrainingPhrases.add(trimmed)
        }
    }

    fun removeTrainingPhrase(phrase: String) {
        customTrainingPhrases.remove(phrase)
    }

    /**
     * Local rule-based heuristic detector that works instantly offline or as fallback.
     */
    fun analyzeContentHeuristically(
        titleOrUrl: String,
        bodyText: String
    ): BrainrotDetectionResult {
        val combined = "$titleOrUrl\n$bodyText".lowercase()
        val detectedTriggers = mutableListOf<String>()
        val linguisticIssues = mutableListOf<String>()
        val structuralIssues = mutableListOf<String>()

        var score = 0

        // 1. Brainrot vernacular check & custom trained slop
        var brainrotHits = 0
        val allPhrases = BRAINROT_PHRASES + customTrainingPhrases
        allPhrases.forEach { phrase ->
            if (combined.contains(phrase)) {
                brainrotHits++
                if (customTrainingPhrases.contains(phrase)) {
                    detectedTriggers.add("Custom Trained Slop Pattern: '$phrase'")
                } else {
                    detectedTriggers.add("Brainrot Slang: '$phrase'")
                }
            }
        }
        score += (brainrotHits * 18).coerceAtMost(60)

        // 2. Engagement bait check
        var baitHits = 0
        ENGAGEMENT_BAIT_PATTERNS.forEach { bait ->
            if (combined.contains(bait)) {
                baitHits++
                detectedTriggers.add("Clickbait/Engagement Loop: '$bait'")
            }
        }
        score += (baitHits * 15).coerceAtMost(45)

        // 3. AI Slop clichés check
        var clicheHits = 0
        AI_SLOP_CLICHES.forEach { cliche ->
            if (combined.contains(cliche)) {
                clicheHits++
                linguisticIssues.add("Formulaic AI Cliché: '$cliche'")
            }
        }
        score += (clicheHits * 12).coerceAtMost(36)

        // 4. Excessive emoji spam check
        val emojiRegex = Regex("[\\p{So}\\p{Cn}]")
        val emojiCount = emojiRegex.findAll(combined).count()
        if (emojiCount > 8) {
            score += 15
            structuralIssues.add("High visual stimulation: $emojiCount emojis detected in brief text")
        }

        // 5. ALL CAPS shouting ratio
        val letters = "$titleOrUrl $bodyText".filter { it.isLetter() }
        val upperLetters = letters.filter { it.isUpperCase() }
        if (letters.length > 20 && (upperLetters.length.toFloat() / letters.length) > 0.45f) {
            score += 20
            structuralIssues.add("Aggressive capitalization (CAPS lock engagement bait)")
        }

        // 6. Suspicious / Shortened Domains
        SUSPICIOUS_DOMAINS.forEach { domain ->
            if (titleOrUrl.lowercase().contains(domain)) {
                score += 30
                detectedTriggers.add("Flagged Content Farm / Adware Domain")
            }
        }

        // 7. Repetitive word loops (Subway Surfers split-screen transcript behavior)
        val words = combined.split("\\s+".toRegex()).filter { it.length > 3 }
        if (words.size > 20) {
            val uniqueWords = words.toSet()
            val repetitionRatio = 1.0f - (uniqueWords.size.toFloat() / words.size.toFloat())
            if (repetitionRatio > 0.65f) {
                score += 20
                structuralIssues.add("High syntactic redundancy: repetitive low-entropy phrasing ($repetitionRatio lexical repetition)")
            }
        }

        val finalScore = score.coerceIn(0, 100)
        val authenticity = (100 - finalScore).coerceIn(0, 100)

        val severity = when {
            finalScore >= 65 -> SlopSeverity.SEVERE_BRAINROT
            finalScore >= 40 -> SlopSeverity.MODERATE_SLOP
            finalScore >= 20 -> SlopSeverity.LOW_RISK
            else -> SlopSeverity.CLEAN
        }

        val summary = when (severity) {
            SlopSeverity.SEVERE_BRAINROT -> "High concentrations of algorithmically engineered brainrot, engagement bait or low-entropy AI filler detected."
            SlopSeverity.MODERATE_SLOP -> "Noticeable clickbait phrases or formulaic filler that may diminish human editorial depth."
            SlopSeverity.LOW_RISK -> "Minor internet slang or marketing buzzwords, but largely original and coherent content."
            SlopSeverity.CLEAN -> "Genuine, well-structured human or curated media with natural linguistic variety."
        }

        val recommendation = when (severity) {
            SlopSeverity.SEVERE_BRAINROT -> "Recommend hiding or flagging as low-effort slop. Mute automated content loops."
            SlopSeverity.MODERATE_SLOP -> "Verify sources and check for original editorial analysis before sharing."
            SlopSeverity.LOW_RISK -> "Safe for general civic reading."
            SlopSeverity.CLEAN -> "Verified high-quality piece suitable for Civic Archives & Broadside."
        }

        return BrainrotDetectionResult(
            score = finalScore,
            severity = severity,
            summary = summary,
            triggersDetected = detectedTriggers.distinct(),
            linguisticPatterns = linguisticIssues.distinct(),
            structuralIssues = structuralIssues.distinct(),
            authenticityScore = authenticity,
            recommendation = recommendation,
            isAiGeneratedAssessment = false
        )
    }

    /**
     * Deep AI inspection using Firebase AI Logic (Gemini API) if available, with automatic
     * seamless fallback to heuristic detector if network or credentials are absent.
     */
    suspend fun analyzeWithAiFlow(
        sourceUrlOrTitle: String,
        contentBody: String
    ): BrainrotDetectionResult = withContext(Dispatchers.IO) {
        val heuristicBaseline = analyzeContentHeuristically(sourceUrlOrTitle, contentBody)

        try {
            val generativeModel = Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    temperature = 0.2f
                }
            )

            val prompt = """
                Analyze the following content from the web or community feed to detect "brainrot", "AI slop", "content farm spam", or "low-effort clickbait".
                
                Content Title/URL: $sourceUrlOrTitle
                Content Text: $contentBody
                
                Return a JSON object conforming to this schema:
                {
                   "score": integer (0 to 100, where 100 is extreme brainrot slop),
                   "severity": string ("CLEAN", "LOW_RISK", "MODERATE_SLOP", or "SEVERE_BRAINROT"),
                   "summary": string (concise 1-2 sentence assessment),
                   "triggersDetected": array of strings (specific slop terms or engagement tricks found),
                   "linguisticPatterns": array of strings (AI cliches or unnatural syntax),
                   "structuralIssues": array of strings (e.g. high repetition, emoji spam, clickbait cliffhanger),
                   "authenticityScore": integer (0 to 100),
                   "recommendation": string (recommendation for users)
                }
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val jsonText = response.text?.trim() ?: throw IllegalStateException("Empty response")
            val cleanJson = if (jsonText.startsWith("```json")) {
                jsonText.removePrefix("```json").substringBeforeLast("```").trim()
            } else if (jsonText.startsWith("```")) {
                jsonText.removePrefix("```").substringBeforeLast("```").trim()
            } else {
                jsonText
            }

            val json = JSONObject(cleanJson)
            val score = json.optInt("score", heuristicBaseline.score)
            val sevStr = json.optString("severity", heuristicBaseline.severity.name)
            val severity = try {
                SlopSeverity.valueOf(sevStr)
            } catch (_: Exception) {
                heuristicBaseline.severity
            }

            val summary = json.optString("summary", heuristicBaseline.summary)
            val triggers = mutableListOf<String>()
            val triggersArr = json.optJSONArray("triggersDetected")
            if (triggersArr != null) {
                for (i in 0 until triggersArr.length()) {
                    triggers.add(triggersArr.getString(i))
                }
            }

            val linguistic = mutableListOf<String>()
            val lingArr = json.optJSONArray("linguisticPatterns")
            if (lingArr != null) {
                for (i in 0 until lingArr.length()) {
                    linguistic.add(lingArr.getString(i))
                }
            }

            val structural = mutableListOf<String>()
            val structArr = json.optJSONArray("structuralIssues")
            if (structArr != null) {
                for (i in 0 until structArr.length()) {
                    structural.add(structArr.getString(i))
                }
            }

            val authenticity = json.optInt("authenticityScore", 100 - score)
            val recommendation = json.optString("recommendation", heuristicBaseline.recommendation)

            BrainrotDetectionResult(
                score = score.coerceIn(0, 100),
                severity = severity,
                summary = summary,
                triggersDetected = if (triggers.isNotEmpty()) triggers else heuristicBaseline.triggersDetected,
                linguisticPatterns = if (linguistic.isNotEmpty()) linguistic else heuristicBaseline.linguisticPatterns,
                structuralIssues = if (structural.isNotEmpty()) structural else heuristicBaseline.structuralIssues,
                authenticityScore = authenticity.coerceIn(0, 100),
                recommendation = recommendation,
                isAiGeneratedAssessment = true
            )
        } catch (_: Exception) {
            // Graceful offline/local rule-based fallback
            heuristicBaseline
        }
    }
}
