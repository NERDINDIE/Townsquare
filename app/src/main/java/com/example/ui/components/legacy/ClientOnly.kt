package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose equivalent of client-only.tsx; Android composition is already client-side. */
@Composable fun ClientOnly(content: @Composable () -> Unit = {}) = content()
