package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/toaster.tsx. */
@Composable fun Toaster(messages: List<String> = emptyList()) = LegacyPlaceholder(messages.joinToString("\n"))
