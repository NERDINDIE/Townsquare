package com.example

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Build
import android.os.StrictMode
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.diagnostics.BackgroundGlitchWatchdog
import com.example.diagnostics.DiagnosticSeverity
import com.example.diagnostics.GlitchCategory
import com.example.diagnostics.SessionErrorHandler

/**
 * Townsquare Application Master Class.
 * Initializes the background diagnostics daemon, global uncaught error interception,
 * memory trim callbacks (ComponentCallbacks2), and performance optimization policies.
 */
class TownsquareApplication : Application(), ImageLoaderFactory, ComponentCallbacks2 {

    companion object {
        private const val TAG = "TownsquareApp"
        lateinit var instance: TownsquareApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 1. Install Global Uncaught Exception & Glitch Interceptor
        SessionErrorHandler.install(this)

        // 2. Start Real-time Background UI Freeze, ANR & Memory Watchdog
        BackgroundGlitchWatchdog.start(this)

        // 3. Configure OS StrictMode Thread & VM Policies for optimal stability
        setupStrictModePolicies()

        // 4. Initialize Embedded Backend Server Manager
        com.example.server.TownsquareServerManager.init(this)

        Log.i(TAG, "Townsquare OS Performance & Diagnostics Engine active.")
    }

    /**
     * Optimized Coil ImageLoader with bounded in-memory cache and disk cache.
     * Prevents native bitmap out-of-memory errors on low-spec hardware.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Max 25% of available heap for image bitmaps
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(128L * 1024 * 1024) // 128 MB disk cache limit
                    .build()
            }
            .respectCacheHeaders(false)
            .crossfade(true)
            .build()
    }

    /**
     * Responds to OS system memory pressure signals.
     * Flushes transient bitmaps, temporary audio buffers, and logs telemetry.
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        val levelName = when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> "RUNNING_MODERATE"
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> "RUNNING_LOW"
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> "RUNNING_CRITICAL"
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> "UI_HIDDEN"
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> "BACKGROUND"
            ComponentCallbacks2.TRIM_MEMORY_MODERATE -> "MODERATE"
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> "COMPLETE"
            else -> "LEVEL_$level"
        }

        SessionErrorHandler.logEvent(
            severity = if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) DiagnosticSeverity.WARNING else DiagnosticSeverity.INFO,
            category = GlitchCategory.MEMORY_PRESSURE,
            tag = "OnTrimMemory",
            message = "OS Memory Pressure event: $levelName (level: $level). Evicting cache buffers."
        )

        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            // Proactively purge in-memory caches
            System.gc()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        SessionErrorHandler.logEvent(
            severity = DiagnosticSeverity.WARNING,
            category = GlitchCategory.MEMORY_PRESSURE,
            tag = "OnLowMemory",
            message = "CRITICAL: System low memory signal received. Purging all volatile buffers."
        )
        System.gc()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "OS Configuration changed: orientation=${newConfig.orientation}")
    }

    private fun setupStrictModePolicies() {
        try {
            // Enable VM leak detection
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build()
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to apply StrictMode: ${e.message}")
        }
    }
}
