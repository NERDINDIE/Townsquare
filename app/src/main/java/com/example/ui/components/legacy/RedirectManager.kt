package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of RedirectManager.tsx; navigation/auth is owned by the Android host. */
@Composable fun RedirectManager(content: @Composable () -> Unit = {}) = content()
