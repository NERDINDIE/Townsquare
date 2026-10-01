package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/toggle-group.tsx. */
@Composable fun ToggleGroup(items: List<String> = emptyList()) = LegacyPlaceholder(items.joinToString("  "))
