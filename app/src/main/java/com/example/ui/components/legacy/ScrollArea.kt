package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/scroll-area.tsx. */
@Composable fun ScrollArea(content: @Composable () -> Unit = {}) = content()
