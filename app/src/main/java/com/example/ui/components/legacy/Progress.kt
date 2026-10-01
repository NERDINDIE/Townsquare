package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/progress.tsx. */
@Composable fun Progress(value: Int = 0) = LegacyPlaceholder("$value%")
