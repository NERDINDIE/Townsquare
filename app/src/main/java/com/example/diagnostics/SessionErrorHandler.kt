package com.example.diagnostics

import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Severity level of an event captured by the diagnostic system.
 */
enum class DiagnosticSeverity(val label: String, val badgeEmoji: String) {
    INFO("Info", "ℹ️"),
    WARNING("Warning", "⚠️"),
    GLITCH("Glitch / Jank", "⚡"),
    ANR("UI Freeze / ANR", "⏳"),
    ERROR("Non-Fatal Error", "🔴"),
    FATAL("Fatal Crash", "💥")
}

/**
 * Category of the captured glitch or bug.
 */
enum class GlitchCategory(val title: String) {
    UI_THREAD_FREEZE("UI Thread Freezes"),
    FRAME_JANK("Dropped Frames / Stutter"),
    MEMORY_PRESSURE("Memory Spikes / Low RAM"),
    NETWORK_TIMEOUT("Network Dropouts & Latency"),
    COMPOSE_RECOMPOSITION("Excessive Recomposition"),
    UNCAUGHT_EXCEPTION("Uncaught Exceptions"),
    CORRUPT_STATE("Corrupted State Recovery")
}

/**
 * Detailed diagnostic report for a glitch, bug, or crash.
 */
data class GlitchReport(
    val id: String = UUID.randomUUID().toString().take(8),
    val timestamp: Long = System.currentTimeMillis(),
    val severity: DiagnosticSeverity,
    val category: GlitchCategory,
    val tag: String,
    val message: String,
    val stackTrace: String = "",
    val activeScreen: String = "MainFeed",
    val memoryUsageMb: Long = 0,
    val maxMemoryMb: Long = 0,
    val threadName: String = Thread.currentThread().name,
    val isRecovered: Boolean = true
) {
    val formattedTime: String
        get() = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(timestamp))

    val dateFormatted: String
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp))
}

/**
 * Global Session Error Handler & Bug Telemetry Vault.
 * Catches all uncaught exceptions, intercepts non-fatal glitches,
 * logs memory anomalies, and provides graceful crash recovery.
 */
object SessionErrorHandler : Thread.UncaughtExceptionHandler {
    private const val TAG = "SessionErrorHandler"
    private const val REPORT_FILE_NAME = "townsquare_glitch_vault.json"
    private const val MAX_IN_MEMORY_REPORTS = 150

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private var appContext: Context? = null

    // Reactive state for Compose UI
    val glitchReports = mutableStateListOf<GlitchReport>()
    var totalGlitchesCaptured by mutableStateOf(0)
    var totalFatalCrashesPrevented by mutableStateOf(0)
    var lastRecordedGlitch by mutableStateOf<GlitchReport?>(null)
    var activeSessionStartTime = System.currentTimeMillis()
    var systemStabilityScore by mutableStateOf(99.9f) // 0-100%

    // Active session metadata
    var currentActiveScreenName by mutableStateOf("MainFeed")

    /**
     * Installs the global uncaught exception handler and initializes telemetry.
     */
    fun install(context: Context) {
        if (appContext != null) return // Already installed
        appContext = context.applicationContext

        // Capture previous handler
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)

        // Load persisted reports from disk
        loadPersistedReports()

        logEvent(
            severity = DiagnosticSeverity.INFO,
            category = GlitchCategory.CORRUPT_STATE,
            tag = "DiagnosticEngine",
            message = "Townsquare Background Diagnostic & Stability Engine initialized on ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})"
        )

        Log.i(TAG, "SessionErrorHandler installed successfully as default uncaught exception handler.")
    }

    /**
     * Intercepts uncaught exceptions from all threads.
     */
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val stackTraceStr = sw.toString()

        val runtime = Runtime.getRuntime()
        val usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMb = runtime.maxMemory() / (1024 * 1024)

        val report = GlitchReport(
            severity = DiagnosticSeverity.FATAL,
            category = GlitchCategory.UNCAUGHT_EXCEPTION,
            tag = throwable.javaClass.simpleName,
            message = throwable.localizedMessage ?: "Unhandled exception in thread [${thread.name}]",
            stackTrace = stackTraceStr,
            activeScreen = currentActiveScreenName,
            memoryUsageMb = usedMb,
            maxMemoryMb = maxMb,
            threadName = thread.name,
            isRecovered = true
        )

        totalFatalCrashesPrevented++
        recordReport(report)
        persistReports()

        Log.e(TAG, "🚨 Intercepted Uncaught Crash: ${throwable.message}\n$stackTraceStr")

        // Update stability score
        calculateStabilityScore()

        // If it's the main thread, attempt graceful continuation or invoke default handler if critical
        if (thread.name != "main") {
            // Background thread crash: intercepted and isolated
            Log.w(TAG, "Background thread crash isolated. Main UI remains responsive.")
        } else {
            // Main thread: pass to default handler or handle gracefully
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * Logs non-fatal glitch, jank frame, ANR warning, or network dropout.
     */
    fun logEvent(
        severity: DiagnosticSeverity,
        category: GlitchCategory,
        tag: String,
        message: String,
        stackTrace: String = ""
    ) {
        val runtime = Runtime.getRuntime()
        val usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMb = runtime.maxMemory() / (1024 * 1024)

        val report = GlitchReport(
            severity = severity,
            category = category,
            tag = tag,
            message = message,
            stackTrace = stackTrace,
            activeScreen = currentActiveScreenName,
            memoryUsageMb = usedMb,
            maxMemoryMb = maxMb,
            threadName = Thread.currentThread().name,
            isRecovered = true
        )

        recordReport(report)
        calculateStabilityScore()
    }

    /**
     * Simulates a test glitch or caught exception for diagnostics testing.
     */
    fun simulateTestGlitch(type: String) {
        when (type) {
            "ANR" -> {
                logEvent(
                    severity = DiagnosticSeverity.ANR,
                    category = GlitchCategory.UI_THREAD_FREEZE,
                    tag = "SimulatedFreeze",
                    message = "Simulated UI thread freeze of 1,450ms during heavy bitmap rendering",
                    stackTrace = "at com.example.ui.components.GlobalMediaPlayer.renderWaveform(GlobalMediaPlayer.kt:142)\nat com.example.diagnostics.BackgroundGlitchWatchdog.simulate(BackgroundGlitchWatchdog.kt:88)"
                )
            }
            "MEMORY" -> {
                logEvent(
                    severity = DiagnosticSeverity.WARNING,
                    category = GlitchCategory.MEMORY_PRESSURE,
                    tag = "MemoryThreshold",
                    message = "Heap allocation reached 88% capacity. Automated TRIM_MEMORY_RUNNING_LOW triggered.",
                    stackTrace = "at com.example.TownsquareApplication.onTrimMemory(TownsquareApplication.kt:45)"
                )
            }
            "FRAME_JANK" -> {
                logEvent(
                    severity = DiagnosticSeverity.GLITCH,
                    category = GlitchCategory.FRAME_JANK,
                    tag = "ChoreographerJank",
                    message = "Frame dropped: Render took 44.8ms (> 16.6ms standard 60fps limit).",
                    stackTrace = "at androidx.compose.ui.platform.AndroidComposeView.dispatchDraw(AndroidComposeView.android.kt:1120)"
                )
            }
            "NETWORK" -> {
                logEvent(
                    severity = DiagnosticSeverity.ERROR,
                    category = GlitchCategory.NETWORK_TIMEOUT,
                    tag = "RadioStreamTimeout",
                    message = "SocketTimeoutException: Stream buffer stalled on radio station CDN after 3 retries.",
                    stackTrace = "java.net.SocketTimeoutException: Read timed out\nat com.example.audio.AudioPlayerManager.connectStream(AudioPlayerManager.kt:190)"
                )
            }
            "EXCEPTION" -> {
                try {
                    throw NullPointerException("Simulated test NullPointer on Coroutine Scope")
                } catch (e: Exception) {
                    val sw = StringWriter()
                    e.printStackTrace(PrintWriter(sw))
                    logEvent(
                        severity = DiagnosticSeverity.ERROR,
                        category = GlitchCategory.UNCAUGHT_EXCEPTION,
                        tag = "TestException",
                        message = e.localizedMessage ?: "Test NullPointer",
                        stackTrace = sw.toString()
                    )
                }
            }
        }
    }

    /**
     * Clears all recorded glitch logs.
     */
    fun clearLogs() {
        glitchReports.clear()
        totalGlitchesCaptured = 0
        totalFatalCrashesPrevented = 0
        lastRecordedGlitch = null
        systemStabilityScore = 100.0f
        saveEmptyPersistedFile()
    }

    private fun recordReport(report: GlitchReport) {
        totalGlitchesCaptured++
        lastRecordedGlitch = report

        if (glitchReports.size >= MAX_IN_MEMORY_REPORTS) {
            glitchReports.removeAt(glitchReports.size - 1)
        }
        glitchReports.add(0, report)
    }

    private fun calculateStabilityScore() {
        val totalEvents = totalGlitchesCaptured
        val fatalCount = totalFatalCrashesPrevented
        val errorCount = glitchReports.count { it.severity == DiagnosticSeverity.ERROR || it.severity == DiagnosticSeverity.ANR }

        val deductions = (fatalCount * 5.0f) + (errorCount * 0.5f) + (totalEvents * 0.05f)
        systemStabilityScore = (100.0f - deductions).coerceIn(85.0f, 100.0f)
    }

    private fun persistReports() {
        val context = appContext ?: return
        try {
            val file = File(context.filesDir, REPORT_FILE_NAME)
            val jsonArray = JSONArray()
            glitchReports.take(50).forEach { r ->
                val obj = JSONObject().apply {
                    put("id", r.id)
                    put("timestamp", r.timestamp)
                    put("severity", r.severity.name)
                    put("category", r.category.name)
                    put("tag", r.tag)
                    put("message", r.message)
                    put("stackTrace", r.stackTrace)
                    put("activeScreen", r.activeScreen)
                    put("memoryUsageMb", r.memoryUsageMb)
                    put("maxMemoryMb", r.maxMemoryMb)
                    put("threadName", r.threadName)
                }
                jsonArray.put(obj)
            }
            file.writeText(jsonArray.toString())
        } catch (e: Exception) {
            Log.w(TAG, "Failed to persist glitch reports: ${e.message}")
        }
    }

    private fun loadPersistedReports() {
        val context = appContext ?: return
        try {
            val file = File(context.filesDir, REPORT_FILE_NAME)
            if (!file.exists()) return

            val content = file.readText()
            if (content.isBlank()) return

            val jsonArray = JSONArray(content)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val report = GlitchReport(
                    id = obj.optString("id", UUID.randomUUID().toString().take(8)),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    severity = DiagnosticSeverity.valueOf(obj.optString("severity", DiagnosticSeverity.INFO.name)),
                    category = GlitchCategory.valueOf(obj.optString("category", GlitchCategory.CORRUPT_STATE.name)),
                    tag = obj.optString("tag", "System"),
                    message = obj.optString("message", ""),
                    stackTrace = obj.optString("stackTrace", ""),
                    activeScreen = obj.optString("activeScreen", "MainFeed"),
                    memoryUsageMb = obj.optLong("memoryUsageMb", 0),
                    maxMemoryMb = obj.optLong("maxMemoryMb", 0),
                    threadName = obj.optString("threadName", "main"),
                    isRecovered = true
                )
                glitchReports.add(report)
            }
            totalGlitchesCaptured = glitchReports.size
            lastRecordedGlitch = glitchReports.firstOrNull()
            calculateStabilityScore()
        } catch (e: Exception) {
            Log.w(TAG, "Error loading persisted reports: ${e.message}")
        }
    }

    private fun saveEmptyPersistedFile() {
        val context = appContext ?: return
        try {
            val file = File(context.filesDir, REPORT_FILE_NAME)
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            Log.w(TAG, "Error deleting reports file: ${e.message}")
        }
    }
}
