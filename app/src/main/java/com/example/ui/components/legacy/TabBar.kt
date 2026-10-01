package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of TabBar.tsx. */
@Composable fun TabBar(tabs: List<String> = emptyList(), activeTab: String? = null) = LegacyPlaceholder((tabs.ifEmpty { listOf(activeTab ?: "New Tab") }).joinToString("  |  "))
