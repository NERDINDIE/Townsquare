package com.example.server

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.example.audio.AudioPlayerManager
import com.example.diagnostics.BackgroundGlitchWatchdog
import com.example.diagnostics.DiagnosticSeverity
import com.example.diagnostics.GlitchCategory
import com.example.diagnostics.SessionErrorHandler
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Embedded Native HTTP / REST & Remote Control Server for Townsquare OS.
 * Runs locally inside the Android environment on a configurable port (default 8080).
 * Provides LAN discovery, media playback remote control, diagnostic telemetry streaming,
 * and REST APIs for web companion interfaces and IoT mesh clients.
 */
class TownsquareEmbeddedServer(
    private val context: Context,
    val port: Int = 8080,
    private val onRemoteCommand: (String, JSONObject) -> Unit = { _, _ -> }
) {
    companion object {
        private const val TAG = "TownsquareServer"
    }

    private var serverSocket: ServerSocket? = null
    private val isRunning = AtomicBoolean(false)
    private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var serverJob: Job? = null

    val startTime = System.currentTimeMillis()

    fun start(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (isRunning.get()) {
            onSuccess(getFormattedServerUrl())
            return
        }

        serverJob = serverScope.launch {
            try {
                serverSocket = ServerSocket(port)
                isRunning.set(true)
                val url = getFormattedServerUrl()
                Log.i(TAG, "🟢 Townsquare Embedded Server listening at $url")

                withContext(Dispatchers.Main) {
                    onSuccess(url)
                }

                SessionErrorHandler.logEvent(
                    severity = DiagnosticSeverity.INFO,
                    category = GlitchCategory.CORRUPT_STATE,
                    tag = "EmbeddedServer",
                    message = "Townsquare OS Backend Server started on port $port ($url)"
                )

                while (isActive && isRunning.get()) {
                    try {
                        val clientSocket = serverSocket?.accept() ?: break
                        launch(Dispatchers.IO) {
                            handleClient(clientSocket)
                        }
                    } catch (e: Exception) {
                        if (!isRunning.get()) break
                        Log.w(TAG, "Socket accept error: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                isRunning.set(false)
                Log.e(TAG, "Failed to bind server socket on port $port: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Failed to start server")
                }
            }
        }
    }

    fun stop() {
        if (!isRunning.getAndSet(false)) return
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            Log.w(TAG, "Error closing server socket: ${e.message}")
        }
        serverSocket = null
        serverJob?.cancel()
        serverJob = null

        SessionErrorHandler.logEvent(
            severity = DiagnosticSeverity.INFO,
            category = GlitchCategory.CORRUPT_STATE,
            tag = "EmbeddedServer",
            message = "Townsquare OS Backend Server stopped."
        )
        Log.i(TAG, "🔴 Townsquare Embedded Server stopped.")
    }

    fun getLocalIpAddress(): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        val host = addr.hostAddress
                        if (host != null && !host.startsWith("127.")) {
                            return host
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error resolving local IP: ${e.message}")
        }
        return "127.0.0.1"
    }

    fun getFormattedServerUrl(): String = "http://${getLocalIpAddress()}:$port"

    private fun handleClient(socket: Socket) {
        try {
            socket.soTimeout = 10000 // 10s timeout
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val outputStream = socket.getOutputStream()

            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) return

            val method = parts[0].uppercase()
            val fullPath = parts[1]
            val path = fullPath.substringBefore("?")
            val queryParams = if (fullPath.contains("?")) fullPath.substringAfter("?") else ""

            // Read headers
            val headers = mutableMapOf<String, String>()
            var line: String?
            var contentLength = 0
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrBlank()) break
                val colonIdx = line!!.indexOf(":")
                if (colonIdx > 0) {
                    val key = line!!.substring(0, colonIdx).trim().lowercase()
                    val value = line!!.substring(colonIdx + 1).trim()
                    headers[key] = value
                    if (key == "content-length") {
                        contentLength = value.toIntOrNull() ?: 0
                    }
                }
            }

            // Read body if POST/PUT
            var body = ""
            if (contentLength > 0) {
                val charArray = CharArray(contentLength)
                var readTotal = 0
                while (readTotal < contentLength) {
                    val read = reader.read(charArray, readTotal, contentLength - readTotal)
                    if (read == -1) break
                    readTotal += read
                }
                body = String(charArray, 0, readTotal)
            }

            // Log telemetry
            TownsquareServerManager.recordRequest(method, path, socket.inetAddress.hostAddress ?: "unknown")

            // Handle CORS preflight
            if (method == "OPTIONS") {
                sendResponse(outputStream, 200, "OK", "text/plain", "")
                return
            }

            // Route handling
            when {
                path == "/" || path == "/index.html" -> {
                    val html = buildDashboardHtml()
                    sendResponse(outputStream, 200, "OK", "text/html; charset=UTF-8", html)
                }

                path == "/api/status" -> {
                    val json = buildStatusJson()
                    sendResponse(outputStream, 200, "OK", "application/json", json.toString(2))
                }

                path == "/api/diagnostics" || path == "/api/diagnostics/glitches" -> {
                    val json = buildDiagnosticsJson()
                    sendResponse(outputStream, 200, "OK", "application/json", json.toString(2))
                }

                path == "/api/remote/control" && method == "POST" -> {
                    val responseJson = handleRemoteControl(body)
                    sendResponse(outputStream, 200, "OK", "application/json", responseJson.toString(2))
                }

                path == "/api/mesh/nodes" -> {
                    val json = buildMeshNodesJson()
                    sendResponse(outputStream, 200, "OK", "application/json", json.toString(2))
                }

                path == "/api/news" -> {
                    val json = buildNewsJson()
                    sendResponse(outputStream, 200, "OK", "application/json", json.toString(2))
                }

                path == "/api/channels" -> {
                    val json = buildChannelsJson()
                    sendResponse(outputStream, 200, "OK", "application/json", json.toString(2))
                }

                else -> {
                    val errorJson = JSONObject().apply {
                        put("status", 404)
                        put("error", "Endpoint not found: $path")
                        put("availableEndpoints", JSONArray(listOf(
                            "/", "/api/status", "/api/diagnostics", "/api/remote/control",
                            "/api/news", "/api/channels", "/api/mesh/nodes"
                        )))
                    }
                    sendResponse(outputStream, 404, "Not Found", "application/json", errorJson.toString(2))
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error handling client connection: ${e.message}")
        } finally {
            try {
                socket.close()
            } catch (ignored: Exception) {}
        }
    }

    private fun sendResponse(
        out: OutputStream,
        statusCode: Int,
        statusText: String,
        contentType: String,
        body: String
    ) {
        val writer = BufferedWriter(OutputStreamWriter(out, "UTF-8"))
        val bodyBytes = body.toByteArray(Charsets.UTF_8)

        writer.write("HTTP/1.1 $statusCode $statusText\r\n")
        writer.write("Content-Type: $contentType\r\n")
        writer.write("Content-Length: ${bodyBytes.size}\r\n")
        writer.write("Access-Control-Allow-Origin: *\r\n")
        writer.write("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n")
        writer.write("Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With\r\n")
        writer.write("Server: TownsquareOS-Embedded/1.0 (Android)\r\n")
        writer.write("Connection: close\r\n")
        writer.write("\r\n")
        writer.flush()

        out.write(bodyBytes)
        out.flush()
    }

    private fun buildStatusJson(): JSONObject {
        val runtime = Runtime.getRuntime()
        val usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMb = runtime.maxMemory() / (1024 * 1024)

        return JSONObject().apply {
            put("server", "Townsquare OS Native Embedded Backend")
            put("version", "1.0")
            put("status", "ONLINE")
            put("uptimeSeconds", (System.currentTimeMillis() - startTime) / 1000)
            put("stabilityScore", SessionErrorHandler.systemStabilityScore)
            put("device", "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})")
            put("memory", JSONObject().apply {
                put("usedMb", usedMb)
                put("maxMb", maxMb)
                put("freeMb", maxMb - usedMb)
            })
            put("watchdog", JSONObject().apply {
                put("isRunning", BackgroundGlitchWatchdog.isRunning)
                put("uiLatencyMs", BackgroundGlitchWatchdog.currentUiLatencyMs)
                put("estimatedFps", BackgroundGlitchWatchdog.currentFps)
                put("totalAnrsDetected", BackgroundGlitchWatchdog.totalAnrsDetected)
                put("totalJankFramesDetected", BackgroundGlitchWatchdog.totalJankFramesDetected)
            })
            put("totalErrorsCaptured", SessionErrorHandler.totalGlitchesCaptured)
            put("totalFatalCrashesPrevented", SessionErrorHandler.totalFatalCrashesPrevented)
        }
    }

    private fun buildDiagnosticsJson(): JSONObject {
        val arr = JSONArray()
        SessionErrorHandler.glitchReports.take(25).forEach { r ->
            arr.put(JSONObject().apply {
                put("id", r.id)
                put("timestamp", r.dateFormatted)
                put("severity", r.severity.name)
                put("category", r.category.title)
                put("tag", r.tag)
                put("message", r.message)
                put("screen", r.activeScreen)
                put("thread", r.threadName)
                put("recovered", r.isRecovered)
            })
        }

        return JSONObject().apply {
            put("totalCaptured", SessionErrorHandler.totalGlitchesCaptured)
            put("stabilityScore", SessionErrorHandler.systemStabilityScore)
            put("recentReports", arr)
        }
    }

    private fun handleRemoteControl(body: String): JSONObject {
        return try {
            val json = if (body.isNotBlank()) JSONObject(body) else JSONObject()
            val command = json.optString("command", "").lowercase()

            onRemoteCommand(command, json)

            JSONObject().apply {
                put("success", true)
                put("executedCommand", command)
                put("timestamp", System.currentTimeMillis())
                put("message", "Remote command '$command' dispatched to Townsquare OS media manager.")
            }
        } catch (e: Exception) {
            JSONObject().apply {
                put("success", false)
                put("error", e.message ?: "Failed to parse command")
            }
        }
    }

    private fun buildMeshNodesJson(): JSONObject {
        return JSONObject().apply {
            put("meshStatus", "ACTIVE_DISCOVERY")
            put("localNodeId", "TS-NODE-${Build.MODEL.take(6).uppercase()}")
            put("activePeersCount", 4)
            put("peers", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", "NODE-RADIO-RELAY-1")
                    put("type", "BROADCAST_REPEATER")
                    put("rssi", -54)
                    put("hopCount", 1)
                })
                put(JSONObject().apply {
                    put("id", "NODE-CIVIC-ALERT-2")
                    put("type", "EMERGENCY_BEACON")
                    put("rssi", -68)
                    put("hopCount", 1)
                })
                put(JSONObject().apply {
                    put("id", "NODE-KIOSK-MARKET-3")
                    put("type", "COMMUNITY_HUB")
                    put("rssi", -72)
                    put("hopCount", 2)
                })
            })
        }
    }

    private fun buildNewsJson(): JSONObject {
        return JSONObject().apply {
            put("edition", "Townsquare Morning Broadsheet - Latest Edition")
            put("timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()))
            put("headlines", JSONArray().apply {
                put(JSONObject().apply {
                    put("title", "Highland District Approves Clean Energy Microgrid Expansion")
                    put("category", "Local Civic")
                    put("author", "Elena Rostova")
                    put("urgency", "High")
                })
                put(JSONObject().apply {
                    put("title", "Historic Grand Central Station Restores Antique Clock Tower")
                    put("category", "Culture & Heritage")
                    put("author", "Marcus Vance")
                    put("urgency", "Normal")
                })
                put(JSONObject().apply {
                    put("title", "Annual Autumn Makers & Farmers Fair Opens This Saturday")
                    put("category", "Community Events")
                    put("author", "Claire Sterling")
                    put("urgency", "Normal")
                })
            })
        }
    }

    private fun buildChannelsJson(): JSONObject {
        return JSONObject().apply {
            put("totalChannels", 6)
            put("channels", JSONArray().apply {
                put(JSONObject().apply {
                    put("id", "ch_tctv_1")
                    put("name", "TCTV 1: Civic & Broadsheet Live")
                    put("type", "TV_BROADCAST")
                    put("resolution", "1080p60")
                    put("status", "ON_AIR")
                })
                put(JSONObject().apply {
                    put("id", "ch_vintage_news")
                    put("name", "Townsquare Public Access 4")
                    put("type", "TV_PUBLIC_ACCESS")
                    put("resolution", "720p30")
                    put("status", "ON_AIR")
                })
                put(JSONObject().apply {
                    put("id", "rad_985_fm")
                    put("name", "98.5 Townsquare Classic Jazz & Blues")
                    put("type", "RADIO_FM")
                    put("bitrate", "320kbps")
                    put("status", "LIVE_STREAM")
                })
                put(JSONObject().apply {
                    put("id", "rad_1041_dispatch")
                    put("name", "104.1 Hourly News & Weather Dispatch")
                    put("type", "RADIO_FM")
                    put("bitrate", "192kbps")
                    put("status", "LIVE_STREAM")
                })
            })
        }
    }

    private fun buildDashboardHtml(): String {
        val ip = getLocalIpAddress()
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Townsquare OS Embedded Server</title>
    <style>
        :root {
            --bg-color: #0b0f19;
            --surface: #141c2e;
            --accent: #00e5ff;
            --amber: #ffb300;
            --text: #f0f4f8;
            --text-dim: #94a3b8;
            --border: #1e293b;
            --card: #162035;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, sans-serif; }
        body { background: var(--bg-color); color: var(--text); padding: 24px; }
        header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 16px; margin-bottom: 24px; }
        .badge { background: rgba(0, 229, 255, 0.15); color: var(--accent); padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 600; border: 1px solid var(--accent); }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px; }
        .card { background: var(--card); border: 1px solid var(--border); border-radius: 12px; padding: 20px; }
        .card h3 { color: var(--accent); margin-bottom: 12px; font-size: 16px; display: flex; align-items: center; gap: 8px; }
        .stat-val { font-size: 28px; font-weight: bold; color: var(--text); }
        .stat-lbl { font-size: 12px; color: var(--text-dim); }
        .btn-group { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 12px; }
        button { background: var(--surface); color: var(--accent); border: 1px solid var(--accent); border-radius: 8px; padding: 8px 16px; cursor: pointer; font-weight: 600; transition: all 0.2s; }
        button:hover { background: var(--accent); color: var(--bg-color); }
        pre { background: #070a10; border: 1px solid var(--border); border-radius: 8px; padding: 12px; color: #38bdf8; font-family: monospace; font-size: 12px; overflow-x: auto; max-height: 240px; }
    </style>
</head>
<body>
    <header>
        <div>
            <h1>🏛️ Townsquare OS Backend Console</h1>
            <p style="color: var(--text-dim); font-size: 14px;">Native Android Embedded HTTP / REST & Remote Control Server</p>
        </div>
        <span class="badge">● SERVER ACTIVE (:$port)</span>
    </header>

    <div class="grid">
        <div class="card">
            <h3>⚡ System Telemetry</h3>
            <div style="display: flex; gap: 24px; margin-bottom: 12px;">
                <div>
                    <div class="stat-val" id="stability-score">${SessionErrorHandler.systemStabilityScore}%</div>
                    <div class="stat-lbl">Stability Score</div>
                </div>
                <div>
                    <div class="stat-val" id="glitch-count">${SessionErrorHandler.totalGlitchesCaptured}</div>
                    <div class="stat-lbl">Glitches Intercepted</div>
                </div>
                <div>
                    <div class="stat-val" id="crashes-prevented">${SessionErrorHandler.totalFatalCrashesPrevented}</div>
                    <div class="stat-lbl">Crashes Prevented</div>
                </div>
            </div>
            <p style="font-size: 13px; color: var(--text-dim);">Background Glitch Watchdog is actively monitoring main looper thread and frame cadence.</p>
        </div>

        <div class="card">
            <h3>🎛️ Media Remote Control</h3>
            <p style="font-size: 13px; color: var(--text-dim); margin-bottom: 12px;">Send real-time remote commands directly to Townsquare OS media players:</p>
            <div class="btn-group">
                <button onclick="sendCommand('toggle')">⏯️ Play / Pause</button>
                <button onclick="sendCommand('next')">⏭️ Next Track</button>
                <button onclick="sendCommand('prev')">⏮️ Prev Track</button>
                <button onclick="sendCommand('stop')">⏹️ Stop</button>
                <button onclick="sendCommand('heal')">🩺 Self-Heal Memory</button>
            </div>
            <div id="cmd-status" style="margin-top: 10px; font-size: 12px; color: var(--amber);"></div>
        </div>

        <div class="card">
            <h3>📡 Available REST Endpoints</h3>
            <div class="btn-group" style="margin-bottom: 12px;">
                <button onclick="fetchApi('/api/status')">/api/status</button>
                <button onclick="fetchApi('/api/diagnostics')">/api/diagnostics</button>
                <button onclick="fetchApi('/api/channels')">/api/channels</button>
                <button onclick="fetchApi('/api/news')">/api/news</button>
                <button onclick="fetchApi('/api/mesh/nodes')">/api/mesh/nodes</button>
            </div>
            <pre id="api-output">// Click an endpoint above to view live JSON response...</pre>
        </div>
    </div>

    <script>
        async function fetchApi(endpoint) {
            const out = document.getElementById('api-output');
            out.textContent = 'Loading ' + endpoint + '...';
            try {
                const res = await fetch(endpoint);
                const data = await res.json();
                out.textContent = JSON.stringify(data, null, 2);
            } catch (err) {
                out.textContent = 'Error: ' + err.message;
            }
        }

        async function sendCommand(cmd) {
            const status = document.getElementById('cmd-status');
            status.textContent = 'Sending ' + cmd + '...';
            try {
                const res = await fetch('/api/remote/control', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ command: cmd })
                });
                const data = await res.json();
                status.textContent = '✓ Executed: ' + data.message;
            } catch (err) {
                status.textContent = '✗ Failed: ' + err.message;
            }
        }
    </script>
</body>
</html>
        """.trimIndent()
    }
}
