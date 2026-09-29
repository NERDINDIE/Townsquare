package com.example.ui.plus.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class NewsUpdate(val title: String, val content: String, val time: String)

@Composable
fun NewsblogSection() {
    var updates by remember { mutableStateOf(listOf(NewsUpdate("Welcome", "The pulse of the geek world", "Just now"))) }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(10000L)
            updates = listOf(NewsUpdate("Live Update", "New geeky content detected at ${java.time.LocalTime.now()}", "Just now")) + updates
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Updates!", style = MaterialTheme.typography.headlineMedium, color = Color.White) }
        items(updates.size) { index ->
            val update = updates[index]
            Surface(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("news_update_item"),
                color = Color(0xFFA52A2A),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(update.title, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(update.content, color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun GeekLiveSkin(modifier: Modifier = Modifier) {
    val sections = listOf(
        "1-UP" to "All about games! Keep updated with the latest news and articles!",
        "Anime Shinbun" to "Coming right from Japan's anime studios. Don't miss a beat",
        "Retro" to "Gotta be fresh, can be a bit antique. The biggest retro channel on Geek Live",
        "Comics, Memes, and Humor" to "The laughstock of the geeks!",
        "Live Channels" to "Catch it on live! Here's the channels you can watch.",
        "Newsstand" to "News about every topic!",
        "Street" to "The community lives here.",
        "Bookstore" to "New books, comics, mangas, reviews lies here.",
        "Newsblog" to "The pulse of the geek world"
    )
    
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize().background(Color(0xFF1A1A1A))) {
        // Navigation Bar (Tabs)
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            containerColor = Color(0xFF2D2D2D),
            contentColor = NeonCyan
        ) {
            sections.forEachIndexed { index, (title, _) ->
                Tab(
                    selected = selectedSection == index,
                    onClick = { selectedSection = index },
                    text = { Text(title) }
                )
            }
        }

        // Content Section
        if (sections[selectedSection].first == "Newsblog") {
            NewsblogSection()
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = sections[selectedSection].first,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = sections[selectedSection].second,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray
                )
            }
        }
    }
}
