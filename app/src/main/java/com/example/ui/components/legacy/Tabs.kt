package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/tabs.tsx. */
@Composable fun Tabs(items: List<String> = emptyList(), selected: Int = 0) = LegacyPlaceholder(items.getOrNull(selected) ?: "")
