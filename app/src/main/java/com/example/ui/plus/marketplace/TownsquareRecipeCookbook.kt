package com.example.ui.plus.marketplace

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.plus.model.ItemCondition
import com.example.ui.plus.model.MarketCategory
import com.example.ui.plus.model.MarketplaceListing
import com.example.ui.theme.*
import java.util.UUID

data class RecipeIngredient(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: String,
    val estimatedPrice: Double,
    var isInPantry: Boolean = false,
    var isNeedToBuy: Boolean = true
)

data class CookbookRecipe(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String, // "Artisan Baking", "Seafood & Broths", "Pasta & Hearth", "Rustic Breakfast"
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String, // "Easy", "Medium", "Master Chef"
    val description: String,
    val imageUrl: String,
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val chefNotes: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCookbookSection(
    onAddIngredientsToCart: (List<MarketplaceListing>) -> Unit,
    modifier: Modifier = Modifier
) {
    var recipes by remember {
        mutableStateOf(
            listOf(
                CookbookRecipe(
                    id = "rec_1",
                    title = "Artisanal Wood-Fired Margherita Pizza",
                    category = "Pasta & Hearth",
                    prepTimeMinutes = 25,
                    cookTimeMinutes = 12,
                    servings = 3,
                    difficulty = "Medium",
                    description = "Traditional slow-fermented sourdough crust topped with San Marzano tomatoes, fresh pulled buffalo mozzarella, sea salt, and aromatic basil leaves.",
                    imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=800&q=80",
                    ingredients = listOf(
                        RecipeIngredient(name = "Organic 00 Sourdough Flour", quantity = "500g", estimatedPrice = 4.50, isInPantry = false, isNeedToBuy = true),
                        RecipeIngredient(name = "San Marzano Whole Peeled Tomatoes", quantity = "1 can (400g)", estimatedPrice = 3.20, isInPantry = true, isNeedToBuy = false),
                        RecipeIngredient(name = "Fresh Buffalo Mozzarella Ball", quantity = "250g", estimatedPrice = 5.80, isInPantry = false, isNeedToBuy = true),
                        RecipeIngredient(name = "Fresh Garden Sweet Basil", quantity = "1 bunch", estimatedPrice = 2.00, isInPantry = false, isNeedToBuy = true),
                        RecipeIngredient(name = "Extra Virgin First Cold Press Olive Oil", quantity = "2 tbsp", estimatedPrice = 8.50, isInPantry = true, isNeedToBuy = false)
                    ),
                    instructions = listOf(
                        "Mix flour, water, sourdough starter, and sea salt. Autolyse for 45 minutes, then perform 4 coil folds over 2 hours.",
                        "Cold-proof dough in the refrigerator for 24 hours for optimal airy crust blisters.",
                        "Stretch dough by hand from center outward, leaving an airy 1-inch cornicione edge.",
                        "Crush San Marzano tomatoes with sea salt, spread lightly over base, tear mozzarella chunks, and bake at max oven heat (500°F) on a pizza steel for 8-10 minutes.",
                        "Top with fresh basil leaves and a drizzle of olive oil before slicing."
                    ),
                    chefNotes = "Pro tip: Don't overload the center with sauce to avoid soggy crust."
                ),
                CookbookRecipe(
                    id = "rec_2",
                    title = "Harbor Canal Trout Chowder with Wild Herbs",
                    category = "Seafood & Broths",
                    prepTimeMinutes = 20,
                    cookTimeMinutes = 35,
                    servings = 4,
                    difficulty = "Easy",
                    description = "Hearty coastal chowder made with smoked freshwater canal trout, golden Yukon potatoes, sweet leeks, and heavy cream infused with fresh thyme.",
                    imageUrl = "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=800&q=80",
                    ingredients = listOf(
                        RecipeIngredient(name = "Smoked Freshwater Canal Trout Fillet", quantity = "400g", estimatedPrice = 11.50, isInPantry = false, isNeedToBuy = true),
                        RecipeIngredient(name = "Golden Yukon Butter Potatoes", quantity = "3 large", estimatedPrice = 3.00, isInPantry = true, isNeedToBuy = false),
                        RecipeIngredient(name = "Organic Sweet Leeks & Shallots", quantity = "2 stalks", estimatedPrice = 2.50, isInPantry = false, isNeedToBuy = true),
                        RecipeIngredient(name = "Farm Heavy Cream", quantity = "250ml", estimatedPrice = 3.50, isInPantry = false, isNeedToBuy = true),
                        RecipeIngredient(name = "Fresh Thyme & Bay Leaves", quantity = "1 bundle", estimatedPrice = 2.00, isInPantry = true, isNeedToBuy = false),
                        RecipeIngredient(name = "Crusty Broadsheet Sourdough Baguette", quantity = "1 loaf", estimatedPrice = 4.00, isInPantry = false, isNeedToBuy = true)
                    ),
                    instructions = listOf(
                        "Saute diced leeks and shallots in farm butter until soft and fragrant.",
                        "Add diced Yukon potatoes, fresh thyme sprigs, and vegetable stock. Simmer for 15 minutes until fork-tender.",
                        "Flake the smoked trout into bite-sized chunks and gently fold into the pot.",
                        "Pour in heavy cream, season with sea salt and cracked black pepper, and warm on low for 5 minutes.",
                        "Serve steaming hot with thick slices of warm sourdough bread."
                    ),
                    chefNotes = "Pair with a crisp glass of regional dry cider from Old Quarter Orchards."
                ),
                CookbookRecipe(
                    id = "rec_3",
                    title = "Cobblestone Cardamom & Cinnamon Buns",
                    category = "Rustic Breakfast",
                    prepTimeMinutes = 30,
                    cookTimeMinutes = 20,
                    servings = 8,
                    difficulty = "Medium",
                    description = "Swedish-style braided cardamom morning buns with caramelized brown butter cinnamon sugar and crunchy pearl sugar crystals.",
                    imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800&q=80",
                    ingredients = listOf(
                        RecipeIngredient(name = "High-Protein Bread Flour", quantity = "500g", estimatedPrice = 3.80, isInPantry = true, isNeedToBuy = false),
                        RecipeIngredient(name = "Freshly Ground Green Cardamom Seeds", quantity = "2 tbsp", estimatedPrice = 4.00, isInPantry = false, isNeedToBuy = true),
                        RecipeIngredient(name = "Unsalted Grass-Fed Farm Butter", quantity = "150g", estimatedPrice = 4.50, isInPantry = false, isNeedToBuy = true),
                        RecipeIngredient(name = "Dark Muscovado Brown Sugar", quantity = "100g", estimatedPrice = 2.80, isInPantry = false, isNeedToBuy = true),
                        RecipeIngredient(name = "Pearl Sugar Crystals", quantity = "50g", estimatedPrice = 2.20, isInPantry = false, isNeedToBuy = true)
                    ),
                    instructions = listOf(
                        "Knead milk, active yeast, flour, and freshly ground cardamom until smooth and elastic.",
                        "Roll dough into a large rectangle, spread softened butter mixed with brown sugar and cinnamon.",
                        "Fold dough in thirds like a letter, slice into strips, twist each strip twice, and tie into traditional knot buns.",
                        "Proof for 45 minutes, brush with egg wash, sprinkle generously with pearl sugar, and bake at 400°F for 15-18 minutes.",
                        "Brush with simple vanilla syrup right out of the oven for a glossy finish."
                    ),
                    chefNotes = "Always grind whole green cardamom pods fresh in a mortar for the most intoxicating aroma."
                )
            )
        )
    }

    var selectedRecipe by remember { mutableStateOf(recipes.first()) }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var isAddRecipeOpen by remember { mutableStateOf(false) }

    // New Recipe Dialog Form State
    var newTitle by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Pasta & Hearth") }
    var newPrepTime by remember { mutableStateOf("20") }
    var newCookTime by remember { mutableStateOf("25") }
    var newServings by remember { mutableStateOf("4") }
    var newDesc by remember { mutableStateOf("") }
    var newIngredientsText by remember { mutableStateOf("Fresh Pasta • 400g • $4.00\nCherry Tomatoes • 250g • $2.50\nGarlic Cloves • 4 pcs • $1.00\nOlive Oil • 2 tbsp • $3.00") }
    var newInstructionsText by remember { mutableStateOf("Boil salted water and cook pasta al dente.\nSaute garlic and cherry tomatoes until blistered.\nToss pasta with sauce and garnish with fresh herbs.") }

    val categories = listOf("ALL", "Pasta & Hearth", "Seafood & Broths", "Rustic Breakfast", "Artisan Baking")

    val filteredRecipes = remember(recipes, selectedCategoryFilter) {
        if (selectedCategoryFilter == "ALL") recipes
        else recipes.filter { it.category == selectedCategoryFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .testTag("recipe_cookbook_hero")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1E1A0F), Color(0xFF131F28), Color(0xFF0F172A))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WarmAmber
                            ) {
                                Text(
                                    text = "📖 RECIPE COOKBOOK & PANTRY LIST",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF261800),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Button(
                                onClick = { isAddRecipeOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("add_custom_recipe_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Recipe", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Cook Local • Shop Fresh Ingredients",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Browse heritage civic recipes, check off what is already in your pantry, note what you need to buy, and send missing ingredients straight to your Marketplace cart.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextSecondary
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) NeonCyan else DarkBorder,
                            selectedBorderColor = NeonCyan,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
        }

        // Recipe Selector Cards Carousel
        item {
            Text(
                text = "FEATURED DISHES (${filteredRecipes.size})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredRecipes) { recipe ->
                    val isSelected = selectedRecipe.id == recipe.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF142436) else DarkSurface,
                        border = BorderStroke(1.5.dp, if (isSelected) NeonCyan else DarkBorder),
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { selectedRecipe = recipe }
                            .testTag("recipe_card_${recipe.id}")
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                            ) {
                                AsyncImage(
                                    model = recipe.imageUrl,
                                    contentDescription = recipe.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.75f),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                    ) {
                                    Text(
                                        text = "⏱️ ${recipe.prepTimeMinutes + recipe.cookTimeMinutes} min",
                                        fontSize = 10.sp,
                                        color = WarmAmber,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = recipe.title,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    text = recipe.category,
                                    fontSize = 10.sp,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Recipe Details & Shopping Checklist
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedRecipe.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${selectedRecipe.category} • Serves ${selectedRecipe.servings} • Difficulty: ${selectedRecipe.difficulty}",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = selectedRecipe.description,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = DarkTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Ingredients & Buy Checklist Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛒 INGREDIENTS & GROCERY LIST",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = NeonCyan
                        )
                        val neededCount = selectedRecipe.ingredients.count { it.isNeedToBuy }
                        Text(
                            text = "$neededCount items to buy",
                            fontSize = 11.sp,
                            color = if (neededCount > 0) WarmAmber else Color(0xFF30D158),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Ingredients Items
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedRecipe.ingredients.forEachIndexed { index, ingredient ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (ingredient.isNeedToBuy) Color(0xFF1E1712) else Color(0xFF0F1A24),
                                border = BorderStroke(1.dp, if (ingredient.isNeedToBuy) WarmAmber.copy(alpha = 0.4f) else DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Checkbox(
                                            checked = ingredient.isInPantry,
                                            onCheckedChange = { isChecked ->
                                                val updatedList = selectedRecipe.ingredients.toMutableList()
                                                updatedList[index] = ingredient.copy(
                                                    isInPantry = isChecked,
                                                    isNeedToBuy = !isChecked
                                                )
                                                selectedRecipe = selectedRecipe.copy(ingredients = updatedList)
                                                recipes = recipes.map { if (it.id == selectedRecipe.id) selectedRecipe else it }
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFF30D158),
                                                uncheckedColor = Color.Gray
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = ingredient.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (ingredient.isInPantry) Color.Gray else Color.White,
                                                textDecoration = if (ingredient.isInPantry) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            Text(
                                                text = "${ingredient.quantity} • Est. $${String.format("%.2f", ingredient.estimatedPrice)}",
                                                fontSize = 11.sp,
                                                color = if (ingredient.isNeedToBuy) WarmAmber else DarkTextSecondary
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (ingredient.isNeedToBuy) WarmAmber.copy(alpha = 0.2f) else Color(0xFF30D158).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (ingredient.isNeedToBuy) "NEED TO BUY" else "IN PANTRY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (ingredient.isNeedToBuy) WarmAmber else Color(0xFF30D158),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Add Missing Items to Cart Button
                    val missingIngredients = selectedRecipe.ingredients.filter { it.isNeedToBuy }
                    val totalMissingCost = missingIngredients.sumOf { it.estimatedPrice }

                    Button(
                        onClick = {
                            val cartListings = missingIngredients.map { ing ->
                                MarketplaceListing(
                                    id = "grocery_${ing.id}",
                                    title = "🛒 ${ing.name} (${ing.quantity})",
                                    price = ing.estimatedPrice,
                                    category = MarketCategory.FOOD,
                                    condition = ItemCondition.BRAND_NEW,
                                    description = "Fresh ingredient for ${selectedRecipe.title} sourced from local Market Hall vendor.",
                                    sellerName = "Old Quarter Grocery Co-Op",
                                    sellerRating = 4.95,
                                    sellerLocation = "Market Hall Stall 4",
                                    imageUrl = selectedRecipe.imageUrl
                                )
                            }
                            onAddIngredientsToCart(cartListings)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_ingredients_to_cart_btn"),
                        enabled = missingIngredients.isNotEmpty()
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (missingIngredients.isNotEmpty()) "Add ${missingIngredients.size} Missing Ingredients to Cart ($${String.format("%.2f", totalMissingCost)})" else "All Ingredients in Pantry! 🎉",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Cooking Instructions
                    Text(
                        text = "👩‍🍳 STEP-BY-STEP PREPARATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedRecipe.instructions.forEachIndexed { stepNum, stepText ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    shape = CircleShape,
                                    color = NeonCyan.copy(alpha = 0.15f),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "${stepNum + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stepText,
                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (selectedRecipe.chefNotes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF131F2A),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "💡", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedRecipe.chefNotes,
                                    fontSize = 11.sp,
                                    color = NeonCyan,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // New Custom Recipe Modal Dialog
    if (isAddRecipeOpen) {
        AlertDialog(
            onDismissRequest = { isAddRecipeOpen = false },
            title = { Text("Add Recipe & Grocery List 🍳", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Recipe Title") },
                        placeholder = { Text("e.g. Garlic Butter Clam Linguine") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newPrepTime,
                            onValueChange = { newPrepTime = it },
                            label = { Text("Prep (min)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = newCookTime,
                            onValueChange = { newCookTime = it },
                            label = { Text("Cook (min)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Description & Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = newIngredientsText,
                        onValueChange = { newIngredientsText = it },
                        label = { Text("Ingredients (Name • Qty • \$Price)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            val parsedIngredients = newIngredientsText.lines().filter { it.isNotBlank() }.map { line ->
                                val parts = line.split("•").map { it.trim() }
                                val name = parts.getOrNull(0) ?: line
                                val qty = parts.getOrNull(1) ?: "1 unit"
                                val priceStr = parts.getOrNull(2)?.replace("$", "")?.trim()
                                val price = priceStr?.toDoubleOrNull() ?: 2.50
                                RecipeIngredient(name = name, quantity = qty, estimatedPrice = price, isInPantry = false, isNeedToBuy = true)
                            }

                            val customRecipe = CookbookRecipe(
                                title = newTitle,
                                category = newCategory,
                                prepTimeMinutes = newPrepTime.toIntOrNull() ?: 15,
                                cookTimeMinutes = newCookTime.toIntOrNull() ?: 20,
                                servings = newServings.toIntOrNull() ?: 2,
                                difficulty = "Home Cook",
                                description = newDesc.ifBlank { "Home recipe created in Townsquare Marketplace cookbook." },
                                imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=800&q=80",
                                ingredients = parsedIngredients.ifEmpty {
                                    listOf(RecipeIngredient(name = "Main Grocery Item", quantity = "1 portion", estimatedPrice = 4.00, isInPantry = false, isNeedToBuy = true))
                                },
                                instructions = newInstructionsText.lines().filter { it.isNotBlank() },
                                chefNotes = "Created on ${java.time.LocalDate.now()}"
                            )

                            recipes = listOf(customRecipe) + recipes
                            selectedRecipe = customRecipe
                            isAddRecipeOpen = false
                            newTitle = ""
                            newDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Save to Cookbook", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddRecipeOpen = false }) {
                    Text("Cancel", color = DarkTextSecondary)
                }
            }
        )
    }
}
