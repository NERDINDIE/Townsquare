package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/dialog.tsx. */
@Composable fun Dialog(content: @Composable () -> Unit = {}) = content()
