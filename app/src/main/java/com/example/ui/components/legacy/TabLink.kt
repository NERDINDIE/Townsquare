package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of TabLink.tsx. */
@Composable fun TabLink(title: String, href: String, content: @Composable () -> Unit = { LegacyPlaceholder(title) }) = content()
