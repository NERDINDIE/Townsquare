package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/collapsible.tsx. */
@Composable fun Collapsible(content: @Composable () -> Unit = {}) = content()
