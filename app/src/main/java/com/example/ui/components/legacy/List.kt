package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/list.tsx. */
@Composable fun List(items: List<String> = emptyList()) = LegacyPlaceholder(items.joinToString("\n"))
