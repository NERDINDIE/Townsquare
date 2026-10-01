package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/toggle.tsx. */
@Composable fun Toggle(pressed: Boolean = false) = LegacyPlaceholder(if (pressed) "Pressed" else "Not pressed")
