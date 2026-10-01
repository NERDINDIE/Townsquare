package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/alert.tsx. */
@Composable fun Alert(message: String = "", content: @Composable () -> Unit = {}) = if (message.isBlank()) content() else LegacyPlaceholder(message)
