package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/sheet.tsx. */
@Composable fun Sheet(content: @Composable () -> Unit = {}) = content()
