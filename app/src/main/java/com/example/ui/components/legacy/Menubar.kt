package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/menubar.tsx. */
@Composable fun Menubar(items: List<String> = emptyList()) = LegacyPlaceholder(items.joinToString("  |  "))
