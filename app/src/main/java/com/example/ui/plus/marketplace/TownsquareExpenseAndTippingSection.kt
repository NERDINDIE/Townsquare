package com.example.ui.plus.marketplace

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.UUID
import kotlin.math.ceil

data class MarketplaceExpense(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val merchant: String,
    val amount: Double,
    val category: String, // "Groceries", "Food & Dining", "Marketplace Goods", "Artisan Crafts", "Transit"
    val date: String,
    val categoryEmoji: String = "🛍️"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareExpenseAndTippingSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var subTab by remember { mutableIntStateOf(0) } // 0 = Expense Tracker, 1 = Tipping Calculator

    // Expense Tracker State
    val monthlyBudget = 600.00
    var expenses by remember {
        mutableStateOf(
            listOf(
                MarketplaceExpense(title = "Sourdough Pizza Ingredients", merchant = "Old Quarter Grocery", amount = 15.50, category = "Groceries", date = "Today", categoryEmoji = "🍕"),
                MarketplaceExpense(title = "Broadsheet Annual Subscription", merchant = "Townsquare Press Desk", amount = 49.99, category = "Marketplace Goods", date = "Yesterday", categoryEmoji = "📰"),
                MarketplaceExpense(title = "Canal Trout Chowder Lunch", merchant = "Riverside Diner", amount = 28.00, category = "Food & Dining", date = "Sep 28", categoryEmoji = "🍲"),
                MarketplaceExpense(title = "Handmade Ceramic Espresso Cup", merchant = "Artisan Guild Kiosk", amount = 22.00, category = "Artisan Crafts", date = "Sep 26", categoryEmoji = "☕"),
                MarketplaceExpense(title = "Highway 9 Diner Breakfast Skillet", merchant = "Chrome Star Diner", amount = 18.50, category = "Food & Dining", date = "Sep 24", categoryEmoji = "🍳")
            )
        )
    }

    var isAddExpenseOpen by remember { mutableStateOf(false) }
    var newExpenseTitle by remember { mutableStateOf("") }
    var newExpenseMerchant by remember { mutableStateOf("") }
    var newExpenseAmount by remember { mutableStateOf("") }
    var newExpenseCategory by remember { mutableStateOf("Groceries") }

    // Tipping Calculator State
    var billAmountInput by remember { mutableStateOf("45.00") }
    var selectedTipPercent by remember { mutableIntStateOf(18) }
    var numberOfPeople by remember { mutableIntStateOf(2) }
    var isRoundUpActive by remember { mutableStateOf(false) }

    val rawBill = billAmountInput.toDoubleOrNull() ?: 0.0
    val rawTip = rawBill * (selectedTipPercent / 100.0)
    val rawTotal = rawBill + rawTip
    val finalTotal = if (isRoundUpActive) ceil(rawTotal) else rawTotal
    val actualTip = finalTotal - rawBill
    val perPersonShare = if (numberOfPeople > 0) finalTotal / numberOfPeople else finalTotal

    val totalSpent = expenses.sumOf { it.amount }
    val remainingBudget = monthlyBudget - totalSpent

    Column(modifier = modifier.fillMaxSize().background(DarkBg)) {
        // Sub-Tab Switcher
        TabRow(
            selectedTabIndex = subTab,
            containerColor = DarkSurface,
            contentColor = NeonCyan
        ) {
            Tab(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp)) },
                text = { Text("📊 Expense Tracker", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_expense_tracker")
            )
            Tab(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                icon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp)) },
                text = { Text("🧮 Tip & Split Calculator", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_tipping_calculator")
            )
        }

        if (subTab == 0) {
            // -------------------------------------------------------------
            // 1. EXPENSE TRACKER VIEW
            // -------------------------------------------------------------
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Monthly Budget Overview Card
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth().testTag("expense_budget_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("MONTHLY CIVIC BUDGET", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                                    Text("$${String.format("%.2f", totalSpent)} / $${String.format("%.2f", monthlyBudget)}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = Color.White)
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (remainingBudget > 0) MintTeal.copy(alpha = 0.2f) else CoralRed.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "$${String.format("%.2f", remainingBudget)} left",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (remainingBudget > 0) MintTeal else CoralRed,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Linear Progress Bar
                            val progress = (totalSpent / monthlyBudget).coerceIn(0.0, 1.0).toFloat()
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (progress > 0.85f) CoralRed else NeonCyan,
                                trackColor = DarkBorder
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick Log Expense Button
                            Button(
                                onClick = { isAddExpenseOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("log_expense_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log New Marketplace Expense", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Recent Logged Transactions Header
                item {
                    Text("RECENT TRANSACTIONS & LOGS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarmAmber)
                }

                // Expense List Items
                items(expenses, key = { it.id }) { expense ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = NeonCyan.copy(alpha = 0.15f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(expense.categoryEmoji, fontSize = 16.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(expense.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    Text("${expense.merchant} • ${expense.category} • ${expense.date}", fontSize = 11.sp, color = DarkTextSecondary)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$${String.format("%.2f", expense.amount)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = WarmAmber
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { expenses = expenses.filterNot { it.id == expense.id } },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // -------------------------------------------------------------
            // 2. TIPPING & BILL SPLIT CALCULATOR VIEW
            // -------------------------------------------------------------
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calculation Summary Card
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0F261E),
                        border = BorderStroke(1.dp, MintTeal.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("tipping_result_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PER PERSON SHARE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MintTeal, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${String.format("%.2f", perPersonShare)}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MintTeal.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Bill Total", fontSize = 10.sp, color = DarkTextSecondary)
                                    Text("$${String.format("%.2f", rawBill)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Tip Amount ($selectedTipPercent%)", fontSize = 10.sp, color = DarkTextSecondary)
                                    Text("$${String.format("%.2f", actualTip)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MintTeal)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Grand Total", fontSize = 10.sp, color = DarkTextSecondary)
                                    Text("$${String.format("%.2f", finalTotal)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                                }
                            }
                        }
                    }
                }

                // Bill Input Card
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurface,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("BILL AMOUNT ($)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = billAmountInput,
                                onValueChange = { billAmountInput = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Tip Percentage Chips
                            Text("SELECT TIP PERCENTAGE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarmAmber)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(10, 15, 18, 20, 25).forEach { pct ->
                                    val isSel = selectedTipPercent == pct
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSel) WarmAmber else DarkSurfaceVariant,
                                        border = BorderStroke(1.dp, if (isSel) WarmAmber else DarkBorder),
                                        modifier = Modifier.weight(1f).clickable { selectedTipPercent = pct }
                                    ) {
                                        Text(
                                            text = "$pct%",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color(0xFF261800) else Color.White,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Split Between Diners
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("SPLIT BETWEEN", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                    Text("$numberOfPeople Person${if (numberOfPeople > 1) "s" else ""}", fontSize = 12.sp, color = DarkTextSecondary)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (numberOfPeople > 1) numberOfPeople -= 1 },
                                        modifier = Modifier.size(36.dp).background(DarkSurfaceVariant, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("$numberOfPeople", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    IconButton(
                                        onClick = { numberOfPeople += 1 },
                                        modifier = Modifier.size(36.dp).background(NeonCyan, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color(0xFF003544))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Round Up Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Round up to whole dollar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Extra cents added to tip for civic artisan", fontSize = 10.sp, color = DarkTextSecondary)
                                }
                                Switch(
                                    checked = isRoundUpActive,
                                    onCheckedChange = { isRoundUpActive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MintTeal)
                                )
                            }
                        }
                    }
                }

                // Log Calculated Meal to Expense Tracker Button
                item {
                    Button(
                        onClick = {
                            val newExp = MarketplaceExpense(
                                title = "Dining & Tip ($numberOfPeople Person${if (numberOfPeople > 1) "s" else ""})",
                                merchant = "Local Restaurant / Cafe",
                                amount = finalTotal,
                                category = "Food & Dining",
                                date = "Today",
                                categoryEmoji = "🍽️"
                            )
                            expenses = listOf(newExp) + expenses
                            Toast.makeText(context, "🎉 Logged $${String.format("%.2f", finalTotal)} to Expense Tracker!", Toast.LENGTH_SHORT).show()
                            subTab = 0
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintTeal, contentColor = Color(0xFF003544)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_tip_to_expenses_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Meal to Expense Tracker", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Add Custom Expense Dialog
    if (isAddExpenseOpen) {
        AlertDialog(
            onDismissRequest = { isAddExpenseOpen = false },
            title = {
                Text("Log Marketplace Expense", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newExpenseTitle,
                        onValueChange = { newExpenseTitle = it },
                        label = { Text("Item / Purchase Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newExpenseMerchant,
                        onValueChange = { newExpenseMerchant = it },
                        label = { Text("Merchant / Store Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newExpenseAmount,
                        onValueChange = { newExpenseAmount = it },
                        label = { Text("Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedAmt = newExpenseAmount.toDoubleOrNull() ?: 0.0
                        if (newExpenseTitle.isNotBlank() && parsedAmt > 0) {
                            val newExp = MarketplaceExpense(
                                title = newExpenseTitle,
                                merchant = newExpenseMerchant.ifBlank { "Marketplace Seller" },
                                amount = parsedAmt,
                                category = newExpenseCategory,
                                date = "Today"
                            )
                            expenses = listOf(newExp) + expenses
                            isAddExpenseOpen = false
                            newExpenseTitle = ""
                            newExpenseMerchant = ""
                            newExpenseAmount = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Add Expense", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddExpenseOpen = false }) {
                    Text("Cancel", color = DarkTextSecondary)
                }
            }
        )
    }
}
