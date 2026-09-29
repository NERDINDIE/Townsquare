package com.example.ui.plus.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareExtensionsApp(onBack: () -> Unit) {
    var isGeekLiveActive by remember { mutableStateOf(false) }
    var isFandomTimesActive by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        TopAppBar(
            title = { Text("Extensions Builder", color = Color.White) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )
        
        if (isGeekLiveActive) {
            GeekLiveSkin(modifier = Modifier.fillMaxSize())
        } else if (isFandomTimesActive) {
            FandomTimesSkin(modifier = Modifier.fillMaxSize())
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Installed Extensions", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { isGeekLiveActive = true },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Geek Live (Alpha)", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Legacy skin extension", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { isFandomTimesActive = true },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("The Fandom Times Live", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Fandom news portal skin", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}
