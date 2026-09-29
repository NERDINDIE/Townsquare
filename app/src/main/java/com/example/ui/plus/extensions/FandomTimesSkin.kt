package com.example.ui.plus.extensions

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class FandomArticle(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val category: String,
    val author: String = "Xavier",
    val content: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam."
)

data class FandomLiveUpdate(
    val id: Long,
    val title: String,
    val time: String,
    val body: String = "The pulse of the geek world"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FandomTimesSkin(
    modifier: Modifier = Modifier
) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }

    // Colors as requested
    val crimsonRed = Color(0xFFD2042D)
    val darkBrownRed = Color(0xFFA52A2A)

    if (!isLoggedIn) {
        // Render the preinstalled skin's custom Courier New Login View
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .border(3.dp, crimsonRed, RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "THE FANDOM TIMES",
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = crimsonRed
                )
                Text(
                    text = "Skin Portal Login",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("email", fontFamily = FontFamily.Monospace) },
                    placeholder = { Text("email", fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = crimsonRed,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = crimsonRed
                    ),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("enter a password", fontFamily = FontFamily.Monospace) },
                    placeholder = { Text("enter a password", fontFamily = FontFamily.Monospace) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password"),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = crimsonRed,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = crimsonRed
                    ),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (emailInput.isNotBlank() && passwordInput.isNotBlank()) {
                                isLoggedIn = true
                            } else {
                                loginError = "Please fill in email and password."
                            }
                        }
                    )
                )

                if (loginError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(loginError, color = crimsonRed, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (emailInput.isNotBlank() && passwordInput.isNotBlank()) {
                            isLoggedIn = true
                        } else {
                            loginError = "Please fill in email and password."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = crimsonRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Login", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { isLoggedIn = true }
                ) {
                    Text("Skip / Guest Access", color = darkBrownRed, fontFamily = FontFamily.Monospace)
                }
            }
        }
    } else {
        // Main Portal Dashboard after Login
        var isSideNavOpen by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var selectedTab by remember { mutableStateOf("Newsreels") }
        var activeArticleDetail by remember { mutableStateOf<FandomArticle?>(null) }

        // JS setInterval(addNewsUpdate, 10000) simulation for "The Live"
        var liveUpdates by remember {
            mutableStateOf(
                listOf(
                    FandomLiveUpdate(1L, "The Live", "09:00", "The pulse of the geek world"),
                    FandomLiveUpdate(0L, "The Newsblog", "08:30", "The pulse of the geek world")
                )
            )
        }

        LaunchedEffect(Unit) {
            var counter = 2L
            while (isActive) {
                delay(10000L)
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val timeStr = LocalTime.now().format(formatter)
                liveUpdates = listOf(
                    FandomLiveUpdate(
                        id = counter++,
                        title = "The Live",
                        time = timeStr,
                        body = "Breaking Update: ${listOf(
                            "New Comic Con reveals are coming in fast!",
                            "Rumor: Classic anime franchise receives high-budget movie adaptation.",
                            "Retro hardware price analysis: Collectors hunt vintage cartridges.",
                            "Manga author releases exclusive behind-the-scenes sketch log.",
                            "Fandom community elects new artist spotlight champion of the month."
                        ).random()}"
                    )
                ) + liveUpdates
            }
        }

        // Standard news tiles content
        val allArticles = remember {
            listOf(
                FandomArticle(
                    "art1",
                    "New Sci-Fi Series Shatters Streaming Records",
                    "Critics call it the finest space odyssey of the decade, featuring masterful set design and deep world-building.",
                    "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=800&auto=format&fit=crop&q=60",
                    "Newsreels",
                    "Xavier",
                    "The newest sci-fi drama has captured the imagination of millions around the globe. Set in a remote planetary system, it raises serious philosophical questions about human exploration, digital consciousness, and the future of community."
                ),
                FandomArticle(
                    "art2",
                    "Inside the Vault: Unreleased 90s Game Prototypes",
                    "A retrospective look into lost gaming titles, preserved cartridges, and developer interviews.",
                    "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=800&auto=format&fit=crop&q=60",
                    "The File",
                    "Lara Croft",
                    "Deep in archival drawers, physical game chips remained unplayed for thirty years. We trace the history of these designs and extract the original game graphics for live emulation."
                ),
                FandomArticle(
                    "art3",
                    "The Rise of Indie Comic Book Collectives",
                    "How local self-publishing initiatives are redefining author ownership and art styles.",
                    "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=800&auto=format&fit=crop&q=60",
                    "Columnists",
                    "Stan Moore",
                    "By forming collaborative guilds, artists bypass giant distribution hubs. They write high-risk tales that explore niche subcultures with beautiful hand-inked details."
                ),
                FandomArticle(
                    "art4",
                    "The Fandom Radio: Retro Synthwave Showcase",
                    "Tune in to our latest 24/7 tape stream, discussing late-night driving beats and lo-fi aesthetics.",
                    "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=60",
                    "The Fandom Radio",
                    "DJ Neo",
                    "From neon horizons to tape saturation, our radio channel delivers deep synthesizer loops for studying or drawing comics."
                ),
                FandomArticle(
                    "art5",
                    "The Paper Edition: Print Special #4",
                    "Our annual compilation has finished rolling off the Heidelberg rotary press. How to order copy #1.",
                    "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=800&auto=format&fit=crop&q=60",
                    "Paper Edition",
                    "Chief Editor",
                    "We have hand-bound 500 copies of this special edition on heavy linen paper, filled with interviews and retro layouts."
                ),
                FandomArticle(
                    "art6",
                    "Top 10 Followable Artists Channels This Month",
                    "Our curated selection of independent visual artists, illustrators, and animators you must follow.",
                    "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=800&auto=format&fit=crop&q=60",
                    "Channels",
                    "Aria",
                    "Meet the creators who are building worlds on paper, digital canvas, and canvas frames, with active communities."
                )
            )
        }

        // Filter articles by category and search query
        val displayedArticles = remember(selectedTab, searchQuery) {
            allArticles.filter { article ->
                (article.category == selectedTab || selectedTab == "Newsreels") &&
                        (article.title.contains(searchQuery, ignoreCase = true) ||
                                article.description.contains(searchQuery, ignoreCase = true))
            }
        }

        Box(modifier = modifier.fillMaxSize().background(Color(0xFFF0F0F0))) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar (retro-themed layout)
                Surface(
                    color = Color.White,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { isSideNavOpen = !isSideNavOpen },
                                    modifier = Modifier.testTag("toggle-button")
                                ) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = crimsonRed)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "The Fandom Times",
                                    fontSize = 22.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = crimsonRed
                                )
                            }

                            // Search Field
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("search", fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = crimsonRed, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(42.dp)
                                    .testTag("mysearch"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = crimsonRed,
                                    unfocusedBorderColor = Color.LightGray
                                ),
                                textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                                singleLine = true
                            )
                        }
                    }
                }

                // Main body layout
                Row(modifier = Modifier.fillMaxSize()) {
                    // Animated Side Navigation as requested
                    AnimatedVisibility(
                        visible = isSideNavOpen,
                        enter = slideInHorizontally() + fadeIn(),
                        exit = slideOutHorizontally() + fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(170.dp)
                                .fillMaxHeight(),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 16.dp)
                            ) {
                                Text(
                                    text = "Sidenav",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )

                                val navItems = listOf(
                                    "Newsreels",
                                    "The File",
                                    "Columnists",
                                    "The Fandom Radio",
                                    "Paper Edition",
                                    "Channels"
                                )

                                navItems.forEach { item ->
                                    val isSelected = selectedTab == item
                                    Text(
                                        text = item,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = if (isSelected) crimsonRed else Color.Black,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedTab = item
                                                isSideNavOpen = false // close on selection
                                            }
                                            .background(if (isSelected) crimsonRed.copy(alpha = 0.08f) else Color.Transparent)
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Log out button
                                Button(
                                    onClick = { isLoggedIn = false },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = crimsonRed),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Logout", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }

                    // Main feed workspace
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        // "The Live" auto-updating news updates container (D2042D, A52A2A, white border)
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(crimsonRed, RoundedCornerShape(8.dp))
                                    .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "⚡ THE LIVE UPDATES (Preps every 10s)",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color.White.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Interval active",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Render live updates in red-brown tiles
                                liveUpdates.take(3).forEach { update ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(darkBrownRed, RoundedCornerShape(4.dp))
                                            .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = update.title,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = update.time,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 10.sp,
                                                    color = Color.White.copy(alpha = 0.8f)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = update.body,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Feed Header
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "THE LATEST",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Category: $selectedTab",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Text(
                                    text = "${displayedArticles.size} articles found",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = crimsonRed
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // News Tiles lists (flexible wrap-like vertical list with beautiful animations)
                        if (displayedArticles.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No articles found matching search query.", fontFamily = FontFamily.Monospace, color = Color.Gray)
                                }
                            }
                        } else {
                            items(displayedArticles) { article ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable { activeArticleDetail = article },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column {
                                        AsyncImage(
                                            model = article.imageUrl,
                                            contentDescription = article.title,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Surface(
                                                color = crimsonRed.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = article.category.uppercase(),
                                                    color = crimsonRed,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = article.title,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFF333333)
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = article.description,
                                                fontSize = 13.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFF777777),
                                                maxLines = 3
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Author: ${article.author}",
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = Color.Gray
                                                )

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "Read More",
                                                        fontSize = 11.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold,
                                                        color = crimsonRed
                                                    )
                                                    Icon(
                                                        Icons.Default.ArrowForward,
                                                        contentDescription = null,
                                                        tint = crimsonRed,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Footer as requested
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier.padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "© 2026 The Fandom Times Live • All Rights Reserved",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(48.dp))
                        }
                    }
                }
            }

            // Article Reader Dialog/Overlay
            activeArticleDetail?.let { article ->
                AlertDialog(
                    onDismissRequest = { activeArticleDetail = null },
                    title = {
                        Text(
                            text = article.title,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = crimsonRed,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                            item {
                                AsyncImage(
                                    model = article.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("By ${article.author}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(article.category, fontFamily = FontFamily.Monospace, color = crimsonRed, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = article.content,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { activeArticleDetail = null },
                            colors = ButtonDefaults.buttonColors(containerColor = crimsonRed)
                        ) {
                            Text("Close", fontFamily = FontFamily.Monospace)
                        }
                    }
                )
            }
        }
    }
}
