package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of MiniPlayer.tsx. */
@Composable fun MiniPlayer(title: String = "", type: String = "") = LegacyPlaceholder(if (title.isBlank()) type else "$title\n$type")
