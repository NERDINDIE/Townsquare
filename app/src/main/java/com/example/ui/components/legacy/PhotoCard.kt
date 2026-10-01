package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of PhotoCard.tsx. */
@Composable fun PhotoCard(alt: String = "", caption: String? = null) = LegacyPlaceholder(caption ?: alt)
