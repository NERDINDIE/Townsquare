package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ArticleCard.tsx. */
@Composable fun ArticleCard(title: String = "", excerpt: String = "") = LegacyPlaceholder(title.ifBlank { excerpt })
