package com.example.ui.plus.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class Article(val title: String, val description: String, val imageUrl: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FandomTimesSkin(modifier: Modifier = Modifier) {
    val navItems = listOf(
        "Newsreels", "The File", "Columnists", "The Fandom Radio", "Paper Edition", "Channels"
    )
    var selectedItem by remember { mutableStateOf(navItems[0]) }
    
    // Simulated news state
    var articles by remember { mutableStateOf(emptyList<Article>()) }
    LaunchedEffect(Unit) {
        articles = listOf(
            Article("The Fandom Times: Comic Con Special", "The biggest event of the year returns with exclusive studio panels and celebrity guest appearances.", "https://example.com/con.jpg"),
            Article("New Retro Console Review", "We dive deep into the latest revival of 90s gaming hardware. Is it worth the hype?", "https://example.com/retro.jpg"),
            Article("Upcoming Sci-Fi Blockbusters", "From interstellar travel to dystopian futures, here's what to look forward to this season.", "https://example.com/scifi.jpg"),
            Article("Community Spotlight: Fan Art Winners", "Celebrating the creativity of our readers with a showcase of this month's best fan art.", "https://example.com/fanart.jpg")
        )
    }

    Row(modifier = modifier.fillMaxSize().background(Color(0xFFF0F0F0))) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(160.dp)
                .fillMaxHeight()
                .background(Color(0xFFF5F5F5))
                .padding(top = 25.dp)
        ) {
            navItems.forEach { item ->
                Text(
                    text = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedItem = item }
                        .padding(16.dp),
                    color = if (selectedItem == item) Color.Black else Color.Gray,
                    fontWeight = if (selectedItem == item) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
        
        // Main Content
        LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
            item {
                Text(
                    text = "The Fandom Times",
                    fontSize = 25.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFDC143C),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            items(articles) { article ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .clickable { /* Handle click */ },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column {
                        AsyncImage(
                            model = article.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(article.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF333333))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(article.description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF777777))
                        }
                    }
                }
            }
        }
    }
}
