package com.example.server

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.audio.AudioPlayerManager
import com.example.diagnostics.PerformanceOptimizationEngine
import com.example.diagnostics.SessionErrorHandler
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Server Request Telemetry Log Entry
 */
data class ServerRequestLog(
    val id: String = UUID.randomUUID().toString().take(8),
    val timestamp: Long = System.currentTimeMillis(),
    val method: String,
    val path: String,
    val clientIp: String,
    val status: Int = 200
) {
    val formattedTime: String
        get() = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}

/**
 * Global Townsquare OS Backend Server Manager Singleton.
 * Manages embedded server lifecycle, port settings, request logs, and remote control integration.
 */
object TownsquareServerManager {
    private const val TAG = "TownsquareServerMgr"

    private var serverInstance: TownsquareEmbeddedServer? = null
    private var appContext: Context? = null

    // Reactive Compose state
    var isServerRunning by mutableStateOf(false)
    var serverPort by mutableIntStateOf(8080)
    var serverUrl by mutableStateOf("http://127.0.0.1:8080")
    var totalRequestsServed by mutableLongStateOf(0L)
    var lastRequestTime by mutableLongStateOf(0L)

    val requestLogs = mutableStateListOf<ServerRequestLog>()

    // Media callback hook
    var onRemotePlaybackToggle: () -> Unit = {}
    var onRemoteNextTrack: () -> Unit = {}
    var onRemotePrevTrack: () -> Unit = {}
    var onRemoteStop: () -> Unit = {}

    fun init(context: Context) {
        if (appContext != null) return
        appContext = context.applicationContext
    }

    fun startServer(port: Int = 8080, context: Context? = null) {
        val ctx = context?.applicationContext ?: appContext ?: return
        if (isServerRunning) return

        serverPort = port
        serverInstance = TownsquareEmbeddedServer(
            context = ctx,
            port = port,
            onRemoteCommand = { cmd, _ ->
                handleRemoteCommand(cmd, ctx)
            }
        )

        serverInstance?.start(
            onSuccess = { url ->
                isServerRunning = true
                serverUrl = url
                Log.i(TAG, "Server started successfully at $url")
            },
            onError = { error ->
                isServerRunning = false
                Log.e(TAG, "Server failed to start: $error")
            }
        )
    }

    fun stopServer() {
        serverInstance?.stop()
        serverInstance = null
        isServerRunning = false
        Log.i(TAG, "Server stopped.")
    }

    fun toggleServer(context: Context) {
        if (isServerRunning) {
            stopServer()
        } else {
            startServer(serverPort, context)
        }
    }

    fun recordRequest(method: String, path: String, clientIp: String, status: Int = 200) {
        totalRequestsServed++
        lastRequestTime = System.currentTimeMillis()

        val log = ServerRequestLog(
            method = method,
            path = path,
            clientIp = clientIp,
            status = status
        )

        if (requestLogs.size >= 50) {
            requestLogs.removeAt(requestLogs.size - 1)
        }
        requestLogs.add(0, log)
    }

    fun clearLogs() {
        requestLogs.clear()
        totalRequestsServed = 0L
    }

    private fun handleRemoteCommand(command: String, context: Context) {
        when (command) {
            "toggle", "play", "pause" -> {
                onRemotePlaybackToggle()
            }
            "next" -> {
                onRemoteNextTrack()
            }
            "prev", "previous" -> {
                onRemotePrevTrack()
            }
            "stop" -> {
                onRemoteStop()
            }
            "heal", "optimize" -> {
                PerformanceOptimizationEngine.runOptimizationSweep(context)
            }
        }
    }
}
