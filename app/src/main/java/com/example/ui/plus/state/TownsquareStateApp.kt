package com.example.ui.plus.state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareStateApp(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        TopAppBar(
            title = { Text("State Services", color = Color.White) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Civil Services, Taxes, Billing & Voting Dashboard", color = Color.White)
        }
    }
}
