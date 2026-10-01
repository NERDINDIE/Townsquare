package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/chart.tsx. */
@Composable fun Chart(values: List<Float> = emptyList()) = LegacyPlaceholder("Chart (${values.size} points)")
