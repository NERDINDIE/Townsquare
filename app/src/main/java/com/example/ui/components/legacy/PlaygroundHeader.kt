package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of PlaygroundHeader.tsx. */
@Composable fun PlaygroundHeader(profile: String = "Child Profile") = LegacyPlaceholder("Playground  •  $profile")
