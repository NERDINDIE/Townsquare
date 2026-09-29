package com.example.ui.plus.marketplace

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.plus.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareMarketplaceApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var listings by remember { mutableStateOf(TownsquarePlusSeed.generateInitialMarketplaceListings()) }
    var selectedCategory by remember { mutableStateOf(MarketCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Active item detail
    var selectedListing by remember { mutableStateOf<MarketplaceListing?>(null) }
    
    // Sell an Item dialog state
    var isSellItemOpen by remember { mutableStateOf(false) }

    // Cart state
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var isCartOpen by remember { mutableStateOf(false) }
    var isOrderCompletedDialog by remember { mutableStateOf(false) }

    // Make offer dialog state
    var isOfferDialogOpen by remember { mutableStateOf(false) }
    var offerAmountText by remember { mutableStateOf("") }
    var offerConfirmedMessage by remember { mutableStateOf<String?>(null) }

    fun addToCart(listing: MarketplaceListing) {
        val existing = cartItems.find { it.listing.id == listing.id }
        cartItems = if (existing != null) {
            cartItems.map { if (it.listing.id == listing.id) it.copy(quantity = it.quantity + 1) else it }
        } else {
            cartItems + CartItem(listing, 1)
        }
    }

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
                        IconButton(onClick = onBack, modifier = Modifier.testTag("market_back_button")) {
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
                                    imageVector = Icons.Default.Storefront,
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
                                    text = "Townsquare Market",
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
                                text = "Buy & Sell Online • Community Kiosks",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextSecondary
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // "Sell Item" button
                        Button(
                            onClick = { isSellItemOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("market_sell_item_button")
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sell", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Cart Button with badge
                        IconButton(onClick = { isCartOpen = true }, modifier = Modifier.testTag("market_cart_button")) {
                            BadgedBox(badge = {
                                if (cartItems.isNotEmpty()) {
                                    val totalQty = cartItems.sumOf { it.quantity }
                                    Badge(containerColor = WarmAmber) { Text("$totalQty") }
                                }
                            }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // Search Bar
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = DarkTextMuted)
                            }
                        }
                    },
                    placeholder = { Text("Search vintage, tech, furniture, books...", color = DarkTextMuted, fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("market_search_input")
                )
            }

            // Category Chips Carousel
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MarketCategory.entries) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) WarmAmber else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WarmAmber else DarkBorder),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = cat.iconEmoji, fontSize = 12.sp)
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

            // Filtered Items Grid
            val filteredListings = listings.filter { item ->
                val matchesCategory = selectedCategory == MarketCategory.ALL || item.category == selectedCategory
                val matchesSearch = searchQuery.isEmpty() ||
                        item.title.contains(searchQuery, ignoreCase = true) ||
                        item.description.contains(searchQuery, ignoreCase = true) ||
                        item.sellerName.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }

            if (filteredListings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No listings match your search.", color = DarkTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(onClick = { isSellItemOpen = true }) {
                            Text("Be the first to list an item in this category!", color = NeonCyan)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredListings, key = { it.id }) { listing ->
                        MarketplaceItemCard(
                            listing = listing,
                            onItemClick = { selectedListing = listing },
                            onToggleSave = {
                                listings = listings.map {
                                    if (it.id == listing.id) it.copy(isSaved = !it.isSaved) else it
                                }
                            }
                        )
                    }
                }
            }
        }

        // Product Detail BottomSheet or Dialog
        if (selectedListing != null) {
            val listing = selectedListing!!
            AlertDialog(
                onDismissRequest = { selectedListing = null },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = listing.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(onClick = { selectedListing = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = DarkTextSecondary)
                        }
                    }
                },
                text = {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            AsyncImage(
                                model = listing.imageUrl,
                                contentDescription = listing.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkBorder)
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$${String.format("%.2f", listing.price)}",
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                        color = WarmAmber
                                    )
                                    if (listing.originalPrice != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "$${String.format("%.2f", listing.originalPrice)}",
                                            style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough),
                                            color = DarkTextMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonCyan.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = listing.condition.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MintTeal,
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = listing.sellerName.firstOrNull()?.uppercase() ?: "S",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF003544)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = listing.sellerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text(text = "⭐ ${listing.sellerRating} • ${listing.sellerLocation}", fontSize = 11.sp, color = DarkTextSecondary)
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "ITEM DESCRIPTION",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = DarkTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = listing.description,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = Color.White
                            )
                        }
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                offerAmountText = String.format("%.0f", listing.price * 0.9)
                                isOfferDialogOpen = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Make Offer", color = WarmAmber, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                addToCart(listing)
                                selectedListing = null
                                isCartOpen = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("detail_add_to_cart_btn")
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                },
                dismissButton = {}
            )
        }

        // Sell an Item Modal Dialog
        if (isSellItemOpen) {
            SellItemDialog(
                onDismiss = { isSellItemOpen = false },
                onPublish = { title, price, category, condition, desc, imgUrl ->
                    val newListing = MarketplaceListing(
                        title = title,
                        price = price,
                        category = category,
                        condition = condition,
                        description = desc,
                        sellerName = "You (Local Resident)",
                        sellerRating = 5.0,
                        sellerLocation = "Civic Center • 0.1 miles",
                        imageUrl = imgUrl.ifBlank {
                            "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?auto=format&fit=crop&w=800&q=80"
                        },
                        isSaved = false
                    )
                    listings = listOf(newListing) + listings
                    isSellItemOpen = false
                }
            )
        }

        // Make Offer Dialog
        if (isOfferDialogOpen && selectedListing != null) {
            AlertDialog(
                onDismissRequest = { isOfferDialogOpen = false },
                title = { Text("Make Offer to Seller", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("List price: $${selectedListing!!.price}. Enter your proposed offer below:", fontSize = 13.sp, color = DarkTextSecondary)
                        OutlinedTextField(
                            value = offerAmountText,
                            onValueChange = { offerAmountText = it },
                            label = { Text("Offer Amount ($)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isOfferDialogOpen = false
                            offerConfirmedMessage = "Your offer of $$offerAmountText has been transmitted to ${selectedListing!!.sellerName}!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800))
                    ) {
                        Text("Send Offer", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isOfferDialogOpen = false }) {
                        Text("Cancel", color = DarkTextSecondary)
                    }
                }
            )
        }

        // Offer Confirmed Notice
        if (offerConfirmedMessage != null) {
            AlertDialog(
                onDismissRequest = { offerConfirmedMessage = null },
                title = { Text("Offer Transmitted! 🤝", color = Color.White, fontWeight = FontWeight.Bold) },
                text = { Text(offerConfirmedMessage!!, color = Color.White) },
                confirmButton = {
                    Button(
                        onClick = { offerConfirmedMessage = null },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Cart & Checkout BottomSheet Dialog
        if (isCartOpen) {
            CartCheckoutDialog(
                cartItems = cartItems,
                onDismiss = { isCartOpen = false },
                onRemoveItem = { listingId ->
                    cartItems = cartItems.filterNot { it.listing.id == listingId }
                },
                onUpdateQuantity = { listingId, delta ->
                    cartItems = cartItems.mapNotNull {
                        if (it.listing.id == listingId) {
                            val newQty = it.quantity + delta
                            if (newQty > 0) it.copy(quantity = newQty) else null
                        } else it
                    }
                },
                onCheckout = {
                    cartItems = emptyList()
                    isCartOpen = false
                    isOrderCompletedDialog = true
                }
            )
        }

        // Order Completed Celebration Dialog
        if (isOrderCompletedDialog) {
            AlertDialog(
                onDismissRequest = { isOrderCompletedDialog = false },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "🎉", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Order Confirmed!", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Thank you for supporting our local Townsquare merchants! Your items will be prepared for community locker pickup or local bike delivery.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "RECEIPT #TS-${(1000..9999).random()}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NeonCyan)
                                Text(text = "Delivery Method: 2-Hour Civic Courier", fontSize = 11.sp, color = DarkTextSecondary)
                                Text(text = "Status: Transmitted to Sellers", fontSize = 11.sp, color = Color(0xFF30D158))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { isOrderCompletedDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Marketplace", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

// -------------------------------------------------------------
// ITEM CARD COMPONENT
// -------------------------------------------------------------
@Composable
private fun MarketplaceItemCard(
    listing: MarketplaceListing,
    onItemClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .testTag("market_card_${listing.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = listing.imageUrl,
                    contentDescription = listing.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Favorite Button
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (listing.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Save",
                                tint = if (listing.isSaved) CoralRed else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Category pill
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = listing.category.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", listing.price)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = WarmAmber
                    )
                    Text(
                        text = listing.condition.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMuted,
                        fontSize = 9.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = listing.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = listing.sellerLocation,
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SELL ITEM DIALOG
// -------------------------------------------------------------
@Composable
private fun SellItemDialog(
    onDismiss: () -> Unit,
    onPublish: (title: String, price: Double, category: MarketCategory, condition: ItemCondition, desc: String, imgUrl: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(MarketCategory.TECH) }
    var condition by remember { mutableStateOf(ItemCondition.LIKE_NEW) }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Store, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("List Item for Sale", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Item Title") },
                        placeholder = { Text("e.g. Vintage AM/FM Radio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price ($)") },
                        placeholder = { Text("45.00") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("CATEGORY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(MarketCategory.entries.filter { it != MarketCategory.ALL }) { cat ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (category == cat) NeonCyan else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (category == cat) NeonCyan else DarkBorder),
                                modifier = Modifier.clickable { category = cat }
                            ) {
                                Text(
                                    text = "${cat.iconEmoji} ${cat.label}",
                                    fontSize = 11.sp,
                                    fontWeight = if (category == cat) FontWeight.Bold else FontWeight.Normal,
                                    color = if (category == cat) Color(0xFF003544) else Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text("CONDITION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarmAmber)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(ItemCondition.entries) { cond ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (condition == cond) WarmAmber else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (condition == cond) WarmAmber else DarkBorder),
                                modifier = Modifier.clickable { condition = cond }
                            ) {
                                Text(
                                    text = cond.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (condition == cond) FontWeight.Bold else FontWeight.Normal,
                                    color = if (condition == cond) Color(0xFF261800) else Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Item Description") },
                        placeholder = { Text("Describe condition, history, dimensions...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Photo URL (Optional)") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 20.0
                    if (title.isNotBlank()) {
                        onPublish(title, price, category, condition, description, imageUrl)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                modifier = Modifier.testTag("publish_listing_button")
            ) {
                Text("Publish to Market", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DarkTextSecondary)
            }
        }
    )
}

// -------------------------------------------------------------
// CART & CHECKOUT DIALOG
// -------------------------------------------------------------
@Composable
private fun CartCheckoutDialog(
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onUpdateQuantity: (String, Int) -> Unit,
    onCheckout: () -> Unit
) {
    val subtotal = cartItems.sumOf { it.listing.price * it.quantity }
    val tax = subtotal * 0.08

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Your Cart (${cartItems.size})", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                    Text("Your cart is currently empty.", color = DarkTextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cartItems) { item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.listing.imageUrl,
                                    contentDescription = item.listing.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.listing.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                    Text(text = "$${String.format("%.2f", item.listing.price)} each", fontSize = 11.sp, color = WarmAmber)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onUpdateQuantity(item.listing.id, -1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("-", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    Text(text = "${item.quantity}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    IconButton(
                                        onClick = { onUpdateQuantity(item.listing.id, 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("+", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = DarkTextSecondary, fontSize = 13.sp)
                            Text("$${String.format("%.2f", subtotal)}", color = Color.White, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Civic Delivery", color = DarkTextSecondary, fontSize = 13.sp)
                            Text("FREE (Local Kiosk)", color = Color(0xFF30D158), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimated Tax (8%)", color = DarkTextSecondary, fontSize = 13.sp)
                            Text("$${String.format("%.2f", tax)}", color = Color.White, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TOTAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("$${String.format("%.2f", subtotal + tax)}", color = WarmAmber, fontWeight = FontWeight.Black, fontSize = 17.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = onCheckout,
                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                    modifier = Modifier.testTag("checkout_order_button")
                ) {
                    Text("Place Order", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = DarkTextSecondary)
            }
        }
    )
}
