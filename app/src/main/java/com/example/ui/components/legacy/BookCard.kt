package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of BookCard.tsx. */
@Composable fun BookCard(title: String = "", author: String = "") = LegacyPlaceholder(if (author.isBlank()) title else "$title — $author")
