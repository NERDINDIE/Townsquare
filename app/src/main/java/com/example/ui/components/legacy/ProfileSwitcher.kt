package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ProfileSwitcher.tsx. */
@Composable fun ProfileSwitcher(profiles: List<String> = listOf("Jane Doe")) = LegacyPlaceholder(profiles.joinToString("\n"))
