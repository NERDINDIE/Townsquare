package com.example.ui.components.legacy

import androidx.compose.runtime.Composable

/** Compose conversion of ui/checkbox.tsx. */
@Composable fun Checkbox(checked: Boolean = false, onCheckedChange: (Boolean) -> Unit = {}) = LegacyPlaceholder(if (checked) "☑" else "☐")
