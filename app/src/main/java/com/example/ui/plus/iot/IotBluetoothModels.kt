package com.example.ui.plus.iot

import androidx.compose.ui.graphics.Color

/**
 * Universal Bluetooth Device Types supported across Townsquare IoT Companion.
 */
enum class BluetoothDeviceType(
    val label: String,
    val iconEmoji: String,
    val categoryColor: Color
) {
    AUDIO_HEADPHONES("Headphones & Earbuds", "🎧", Color(0xFF00D2FF)),
    AUDIO_SPEAKER("Wireless Speaker", "🔊", Color(0xFF38BDF8)),
    SMARTWATCH("Smartwatch", "⌚", Color(0xFF10B981)),
    FITNESS_BAND("Fitness Band", "🏃", Color(0xFFF59E0B)),
    CAR_AUDIO_OBD("Car Audio & OBD-II", "🚗", Color(0xFFEF4444)),
    KEYBOARD_MOUSE("Input Peripheral", "⌨️", Color(0xFFA855F7)),
    GAME_CONTROLLER("Game Controller", "🎮", Color(0xFFEC4899)),
    IOT_SENSOR_BEACON("Smart Home Sensor", "🏡", Color(0xFF06B6D4)),
    GENERIC_BLE("Generic BLE Peripheral", "📡", Color(0xFF94A3B8))
}

/**
 * Connection states for Bluetooth devices.
 */
enum class BluetoothConnectionState(val label: String, val badgeColor: Color) {
    CONNECTED("Connected", Color(0xFF10B981)),
    CONNECTING("Connecting...", Color(0xFFF59E0B)),
    DISCONNECTED("Disconnected", Color(0xFF64748B)),
    PAIRING("Pairing...", Color(0xFF38BDF8))
}

/**
 * Universal Bluetooth Device entity representing any connected or paired peripheral.
 */
data class BluetoothDeviceEntity(
    val id: String,
    val name: String,
    val macAddress: String,
    val type: BluetoothDeviceType,
    val connectionState: BluetoothConnectionState,
    val batteryLevel: Int? = 85, // 0 - 100%, null if wired / wall powered
    val signalRssi: Int = -52, // dBm (e.g. -45 is excellent, -85 is weak)
    val firmwareVersion: String = "v2.4.1",
    val codecOrProfile: String = "LDAC 990kbps",
    val isAutoConnectEnabled: Boolean = true,
    val isLowLatencyMode: Boolean = false,
    val isNotificationSyncEnabled: Boolean = true,
    val isAudioForwardingEnabled: Boolean = true,
    val lastConnectedTime: String = "Active now"
)

/**
 * Scanned nearby Bluetooth peripheral discovered during radar scan.
 */
data class BluetoothScanItem(
    val id: String,
    val name: String,
    val macAddress: String,
    val type: BluetoothDeviceType,
    val rssi: Int = -68,
    val isPaired: Boolean = false
)
