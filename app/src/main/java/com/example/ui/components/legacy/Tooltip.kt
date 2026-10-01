package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/tooltip.tsx. */
@Composable fun Tooltip(text: String = "", content: @Composable () -> Unit = {}) = content()
