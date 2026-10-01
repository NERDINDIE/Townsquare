package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/switch.tsx. */
@Composable fun Switch(checked: Boolean = false) = LegacyPlaceholder(if (checked) "On" else "Off")
