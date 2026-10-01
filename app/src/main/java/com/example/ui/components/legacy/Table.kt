package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/table.tsx. */
@Composable fun Table(rows: List<List<String>> = emptyList()) = LegacyPlaceholder("Table (${rows.size} rows)")
