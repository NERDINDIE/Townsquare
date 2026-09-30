package com.example.ui.plus.catalogs

import androidx.compose.runtime.Composable
import com.example.ui.plus.marketplace.TownsquareMarketplaceApp

@Composable
fun TownsquareCatalogsApp(onBack: () -> Unit) {
    TownsquareMarketplaceApp(
        onBack = onBack,
        initialTab = 1
    )
}
