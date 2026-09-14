package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.MediaSuperappViewModel

@Composable
fun EngagementDashboardScreen(viewModel: MediaSuperappViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Viewer Engagement Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Data visualization coming soon.", style = MaterialTheme.typography.bodyLarge)
    }
}
