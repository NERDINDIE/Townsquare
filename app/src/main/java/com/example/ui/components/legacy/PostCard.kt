package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of PostCard.tsx. */
@Composable fun PostCard(author: String = "", content: String = "") = LegacyPlaceholder(if (author.isBlank()) content else "$author\n$content")
