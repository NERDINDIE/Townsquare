package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/popover.tsx. */
@Composable fun Popover(content: @Composable () -> Unit = {}) = content()
