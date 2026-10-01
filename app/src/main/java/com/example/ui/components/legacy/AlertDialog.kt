package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/alert-dialog.tsx. */
@Composable fun AlertDialog(content: @Composable () -> Unit = {}) = content()
