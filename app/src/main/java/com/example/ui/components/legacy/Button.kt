package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/button.tsx. */
@Composable fun Button(label: String = "", onClick: () -> Unit = {}) = LegacyPlaceholder(label)
