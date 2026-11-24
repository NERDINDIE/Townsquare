package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

object NetworkUtils {
    fun isConnected(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (_: Exception) {
            true
        }
    }
}

/**
 * Observes live device network connectivity state.
 */
@Composable
fun rememberIsConnectedToInternet(): Boolean {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(NetworkUtils.isConnected(context)) }

    DisposableEffect(context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm == null) {
            return@DisposableEffect onDispose {}
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
            }

            override fun onLost(network: Network) {
                isConnected = NetworkUtils.isConnected(context)
            }

            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                isConnected = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        try {
            cm.registerNetworkCallback(request, callback)
        } catch (_: Exception) {
            isConnected = NetworkUtils.isConnected(context)
        }

        onDispose {
            try {
                cm.unregisterNetworkCallback(callback)
            } catch (_: Exception) {}
        }
    }

    return isConnected
}
