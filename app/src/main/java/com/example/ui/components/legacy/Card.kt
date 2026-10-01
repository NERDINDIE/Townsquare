package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/card.tsx. */
@Composable fun Card(content: @Composable () -> Unit = {}) = content()
