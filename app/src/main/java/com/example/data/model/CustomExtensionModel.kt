package com.example.data.model

data class CustomExtensionManifest(
    val id: String,
    val name: String,
    val author: String = "Community Creator",
    val version: String = "1.0",
    val description: String = "Custom user-created skin extension for Townsquare",
    val category: String = "APP_SKIN", // APP_SKIN, WELCOME_SKIN, WIRE_TICKER
    val primaryColorHex: String = "#00D2FF",
    val secondaryColorHex: String = "#FF9500",
    val backgroundColorHex: String = "#0A0E17",
    val surfaceColorHex: String = "#131C2E",
    val fontStyle: String = "MONOSPACE", // MONOSPACE, SERIF, SANS, PIXEL
    val layoutType: String = "TERMINAL", // TERMINAL, TILES, BROADSHEET, MAGAZINE, MOBILE_3G
    val enableSoundFx: Boolean = true,
    val enableCrtScanlines: Boolean = false,
    val enableTopTicker: Boolean = true,
    val enableNavKeypad: Boolean = false,
    val customFeedUrl: String = "",
    val welcomeMotto: String = "Custom Extension Edition"
)
