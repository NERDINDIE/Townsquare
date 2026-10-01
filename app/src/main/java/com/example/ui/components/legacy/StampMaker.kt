package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of StampMaker.tsx. */
@Composable fun StampMaker(shape: String = "square", icon: String = "Star", color: Long = 0xFF4B5563) = LegacyPlaceholder("$shape • $icon")
