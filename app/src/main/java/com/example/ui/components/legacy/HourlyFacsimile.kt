package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of HourlyFacsimile.tsx. */
@Composable fun HourlyFacsimile(headlines: List<String> = emptyList()) = LegacyPlaceholder(headlines.joinToString("\n").ifBlank { "Could not load the latest facsimile edition." })
