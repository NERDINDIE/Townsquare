package com.example.ui.plus.books

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

enum class BookShelfCategory(val label: String, val emoji: String) {
    READING("Currently Reading", "📖"),
    WANT_TO_READ("Want to Read", "🔖"),
    COMPLETED("Completed", "✅"),
    QUOTES("Quotes & Highlights", "💬")
}

data class UserBook(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val totalPages: Int,
    var currentPage: Int = 0,
    var category: BookShelfCategory = BookShelfCategory.READING,
    var rating: Int = 5,
    var reviewNote: String = "",
    val favoriteQuote: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareBookwormApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedShelf by remember { mutableStateOf(BookShelfCategory.READING) }
    var searchQuery by remember { mutableStateOf("") }
    var isAddBookOpen by remember { mutableStateOf(false) }

    // Initial Seed Books
    var userBooks by remember {
        mutableStateOf(
            listOf(
                UserBook(
                    id = "b1",
                    title = "The Architecture of Stillness",
                    author = "Maya Lin",
                    coverUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80",
                    totalPages = 280,
                    currentPage = 142,
                    category = BookShelfCategory.READING,
                    rating = 5,
                    reviewNote = "Inspiring insights on mindfulness, architecture, and quiet spaces.",
                    favoriteQuote = "Inner stillness is not the absence of sound, but the presence of focus."
                ),
                UserBook(
                    id = "b2",
                    title = "Foundry Letterpress & Broadsheet Printing",
                    author = "Julian Vance",
                    coverUrl = "https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=800&q=80",
                    totalPages = 340,
                    currentPage = 340,
                    category = BookShelfCategory.COMPLETED,
                    rating = 5,
                    reviewNote = "Masterpiece history of 19th-century typography and local publishing.",
                    favoriteQuote = "Ink on linen paper preserves human memory better than digital bits."
                ),
                UserBook(
                    id = "b3",
                    title = "Akihabara Cyberpunk & Anime Aesthetics",
                    author = "Kenji Sato",
                    coverUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80",
                    totalPages = 210,
                    currentPage = 0,
                    category = BookShelfCategory.WANT_TO_READ,
                    rating = 4,
                    reviewNote = "On wishlist for autumn reading."
                ),
                UserBook(
                    id = "b4",
                    title = "Synthesizers & Solfeggio Acoustic Waveform",
                    author = "DJ Kieran Scott",
                    coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800&q=80",
                    totalPages = 195,
                    currentPage = 80,
                    category = BookShelfCategory.READING,
                    rating = 5,
                    reviewNote = "Fascinating sound engineering handbook."
                ),
                UserBook(
                    id = "b5",
                    title = "Animal Farm",
                    author = "George Orwell",
                    coverUrl = "https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=800&q=80",
                    totalPages = 112,
                    currentPage = 112,
                    category = BookShelfCategory.COMPLETED,
                    rating = 5,
                    reviewNote = "Searing allegory on corruption of power and political rhetoric.",
                    favoriteQuote = "All animals are equal, but some animals are more equal than others."
                ),
                UserBook(
                    id = "b6",
                    title = "Beowulf: A New Translation",
                    author = "Seamus Heaney",
                    coverUrl = "https://images.unsplash.com/photo-1461360370896-922624d12aa1?auto=format&fit=crop&w=800&q=80",
                    totalPages = 224,
                    currentPage = 168,
                    category = BookShelfCategory.READING,
                    rating = 5,
                    reviewNote = "Rhythmic, powerful translation of Old English epic poetry.",
                    favoriteQuote = "Behavior that's admired is the path to power among people everywhere."
                ),
                UserBook(
                    id = "b7",
                    title = "Nineteen Eighty-Four (1984)",
                    author = "George Orwell",
                    coverUrl = "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?auto=format&fit=crop&w=800&q=80",
                    totalPages = 328,
                    currentPage = 198,
                    category = BookShelfCategory.READING,
                    rating = 5,
                    reviewNote = "Chilling exploration of surveillance, Newspeak, and historical rewrite.",
                    favoriteQuote = "Freedom is the freedom to say that two plus two make four."
                ),
                UserBook(
                    id = "b8",
                    title = "Pride and Prejudice",
                    author = "Jane Austen",
                    coverUrl = "https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=800&q=80",
                    totalPages = 432,
                    currentPage = 432,
                    category = BookShelfCategory.COMPLETED,
                    rating = 5,
                    reviewNote = "Witty masterpiece on society, first impressions, and independence.",
                    favoriteQuote = "It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife."
                )
            )
        )
    }

    // Goal Stats
    val yearlyReadCount = userBooks.count { it.category == BookShelfCategory.COMPLETED }
    val yearlyGoal = 12
    val readingStreakDays = 14

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("bookworm_back_btn")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = WarmAmber.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = WarmAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Townsquare Bookworm",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = WarmAmber
                                ) {
                                    Text(
                                        text = "PLUS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF261800),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Personal Book Tracker • Literature Club",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = { isAddBookOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("add_book_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Book", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Annual Reading Goal & Streak Bar
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("READING STREAK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarmAmber, letterSpacing = 1.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$readingStreakDays Consec. Days Read", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text("25 minutes average daily session", fontSize = 11.sp, color = DarkTextSecondary)
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NeonCyan.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("$yearlyReadCount / $yearlyGoal", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                                    Text("2026 Goal", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Section 2: Book Shelf Tabs
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(BookShelfCategory.entries) { cat ->
                            val isSelected = selectedShelf == cat
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) WarmAmber else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WarmAmber else DarkBorder),
                                modifier = Modifier.clickable { selectedShelf = cat }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(cat.emoji, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cat.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF261800) else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 3: Filtered Books List
                val shelfBooks = userBooks.filter {
                    if (selectedShelf == BookShelfCategory.QUOTES) {
                        it.favoriteQuote != null
                    } else {
                        it.category == selectedShelf
                    }
                }

                if (shelfBooks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No books in ${selectedShelf.label} shelf yet.", color = DarkTextSecondary, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(shelfBooks, key = { it.id }) { book ->
                        BookTrackerCard(
                            book = book,
                            onUpdatePage = { newPage ->
                                userBooks = userBooks.map {
                                    if (it.id == book.id) {
                                        val updated = it.copy(currentPage = newPage)
                                        if (newPage >= it.totalPages) updated.copy(category = BookShelfCategory.COMPLETED)
                                        else updated
                                    } else it
                                }
                            },
                            onUpdateCategory = { newCat ->
                                userBooks = userBooks.map { if (it.id == book.id) it.copy(category = newCat) else it }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Add Book Dialog
        if (isAddBookOpen) {
            AddBookDialog(
                onDismiss = { isAddBookOpen = false },
                onAddBook = { title, author, pages, category, coverUrl ->
                    val newBook = UserBook(
                        id = "book_${System.currentTimeMillis()}",
                        title = title,
                        author = author,
                        coverUrl = coverUrl.ifBlank { "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80" },
                        totalPages = pages,
                        currentPage = 0,
                        category = category
                    )
                    userBooks = listOf(newBook) + userBooks
                    isAddBookOpen = false
                }
            )
        }
    }
}

@Composable
private fun BookTrackerCard(
    book: UserBook,
    onUpdatePage: (Int) -> Unit,
    onUpdateCategory: (BookShelfCategory) -> Unit
) {
    val progressFraction = if (book.totalPages > 0) book.currentPage.toFloat() / book.totalPages else 0f
    val percentInt = (progressFraction * 100).toInt()

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth().testTag("book_card_${book.id}")
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(70.dp)
                    .height(105.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkBorder)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "by ${book.author}",
                    fontSize = 11.sp,
                    color = DarkTextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Page ${book.currentPage} of ${book.totalPages}",
                        fontSize = 11.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$percentInt%",
                        fontSize = 11.sp,
                        color = WarmAmber,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progressFraction.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NeonCyan,
                    trackColor = DarkSurfaceElevated
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Page stepper controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = { if (book.currentPage > 0) onUpdatePage(book.currentPage - 10) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("-10", fontSize = 10.sp, color = Color.White)
                        }

                        Button(
                            onClick = { if (book.currentPage < book.totalPages) onUpdatePage(book.currentPage + 10) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("+10 pgs", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WarmAmber.copy(alpha = 0.15f),
                        modifier = Modifier.clickable {
                            val nextCat = when (book.category) {
                                BookShelfCategory.READING -> BookShelfCategory.COMPLETED
                                BookShelfCategory.COMPLETED -> BookShelfCategory.WANT_TO_READ
                                else -> BookShelfCategory.READING
                            }
                            onUpdateCategory(nextCat)
                        }
                    ) {
                        Text(
                            text = book.category.label,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddBookDialog(
    onDismiss: () -> Unit,
    onAddBook: (title: String, author: String, pages: Int, category: BookShelfCategory, coverUrl: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var pagesText by remember { mutableStateOf("250") }
    var category by remember { mutableStateOf(BookShelfCategory.READING) }
    var coverUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Book to Library", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pagesText,
                    onValueChange = { pagesText = it },
                    label = { Text("Total Pages") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddBook(title, author, pagesText.toIntOrNull() ?: 200, category, coverUrl)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
            ) {
                Text("Add to Shelf", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DarkTextSecondary) }
        }
    )
}
