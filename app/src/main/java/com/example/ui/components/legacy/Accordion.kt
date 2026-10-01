package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/accordion.tsx. */
@Composable fun Accordion(content: @Composable () -> Unit = {}) = content()
