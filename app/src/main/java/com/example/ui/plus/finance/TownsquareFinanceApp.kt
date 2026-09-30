package com.example.ui.plus.finance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class CurrencyRate(
    val code: String,
    val name: String,
    val flagEmoji: String,
    val rateToUsd: Double,
    val changePercent24h: Double,
    val high24h: Double,
    val low24h: Double
)

data class MarketIndex(
    val name: String,
    val symbol: String,
    val valueFormatted: String,
    val changePoints: String,
    val changePercent: Double,
    val isUp: Boolean
)

data class FinancialNewsItem(
    val title: String,
    val source: String,
    val timeAgo: String,
    val category: String,
    val snippet: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareFinanceApp(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0=FX & Converter, 1=Indices, 2=Wire News, 3=Wallet

    // Live FX Rates State
    var fxRates by remember {
        mutableStateOf(
            listOf(
                CurrencyRate("EUR", "Euro", "🇪🇺", 0.9215, +0.42, 0.9240, 0.9180),
                CurrencyRate("GBP", "British Pound", "🇬🇧", 0.7850, +0.18, 0.7890, 0.7820),
                CurrencyRate("JPY", "Japanese Yen", "🇯🇵", 154.25, -0.65, 155.10, 153.80),
                CurrencyRate("CHF", "Swiss Franc", "🇨🇭", 0.8840, +0.25, 0.8870, 0.8810),
                CurrencyRate("CAD", "Canadian Dollar", "🇨🇦", 1.3580, -0.12, 1.3610, 1.3540),
                CurrencyRate("AUD", "Australian Dollar", "🇦🇺", 1.5220, +0.31, 1.5280, 1.5180),
                CurrencyRate("CNY", "Chinese Yuan", "🇨🇳", 7.2340, -0.08, 7.2450, 7.2210),
                CurrencyRate("INR", "Indian Rupee", "🇮🇳", 83.45, +0.05, 83.60, 83.30),
                CurrencyRate("SGD", "Singapore Dollar", "🇸🇬", 1.3420, +0.15, 1.3450, 1.3390),
                CurrencyRate("BRL", "Brazilian Real", "🇧🇷", 5.1250, -0.45, 5.1600, 5.0900)
            )
        )
    }

    // Live FX Price Fluctuation Simulation
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(4000L)
            fxRates = fxRates.map { rate ->
                val delta = ((-3..3).random() * 0.0008)
                val newRate = (rate.rateToUsd + delta).coerceAtLeast(0.01)
                val newChange = rate.changePercent24h + ((-2..2).random() * 0.05)
                rate.copy(rateToUsd = newRate, changePercent24h = newChange)
            }
        }
    }

    // Currency Converter State
    var convertAmountText by remember { mutableStateOf("100") }
    var fromCurrencyCode by remember { mutableStateOf("USD") }
    var toCurrencyCode by remember { mutableStateOf("EUR") }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // App Bar
        Surface(
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder),
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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("finance_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF30D158).copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = null,
                                tint = Color(0xFF30D158),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Townsquare FX & Finance",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF30D158)
                            ) {
                                Text(
                                    text = "LIVE FX",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "24/5 Foreign Exchange Rates & Markets",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF132A1C),
                    border = BorderStroke(1.dp, Color(0xFF30D158).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(6.dp).background(Color(0xFF30D158), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("MARKETS OPEN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF30D158))
                    }
                }
            }
        }

        // Top Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = NeonCyan
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("💱 FX & Converter", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("📊 Indices", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("📰 Wire News", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("💳 Wallet", fontSize = 12.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> FxExchangeAndConverterTab(
                    fxRates = fxRates,
                    convertAmountText = convertAmountText,
                    fromCurrencyCode = fromCurrencyCode,
                    toCurrencyCode = toCurrencyCode,
                    onUpdateAmount = { convertAmountText = it },
                    onUpdateFrom = { fromCurrencyCode = it },
                    onUpdateTo = { toCurrencyCode = it },
                    onSwapCurrencies = {
                        val temp = fromCurrencyCode
                        fromCurrencyCode = toCurrencyCode
                        toCurrencyCode = temp
                    }
                )
                1 -> MarketIndicesTab()
                2 -> FinancialNewsTab()
                3 -> CivicWalletTab()
            }
        }
    }
}

@Composable
private fun FxExchangeAndConverterTab(
    fxRates: List<CurrencyRate>,
    convertAmountText: String,
    fromCurrencyCode: String,
    toCurrencyCode: String,
    onUpdateAmount: (String) -> Unit,
    onUpdateFrom: (String) -> Unit,
    onUpdateTo: (String) -> Unit,
    onSwapCurrencies: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // REAL-TIME FX CURRENCY CONVERTER CARD
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("fx_converter_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Real-Time FX Converter", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = NeonCyan.copy(alpha = 0.2f)) {
                            Text("LIVE FX CALC", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input Amount
                    OutlinedTextField(
                        value = convertAmountText,
                        onValueChange = onUpdateAmount,
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Currency Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // From Currency
                        CurrencyDropdownSelector(
                            label = "From",
                            selectedCode = fromCurrencyCode,
                            onSelectCode = onUpdateFrom,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = onSwapCurrencies,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Swap", tint = WarmAmber)
                        }

                        // To Currency
                        CurrencyDropdownSelector(
                            label = "To",
                            selectedCode = toCurrencyCode,
                            onSelectCode = onUpdateTo,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Conversion Calculation Result
                    val amount = convertAmountText.toDoubleOrNull() ?: 0.0
                    val fromRate = if (fromCurrencyCode == "USD") 1.0 else fxRates.find { it.code == fromCurrencyCode }?.rateToUsd ?: 1.0
                    val toRate = if (toCurrencyCode == "USD") 1.0 else fxRates.find { it.code == toCurrencyCode }?.rateToUsd ?: 1.0

                    // Convert: Amount in USD = amount / fromRate; Converted = amountInUsd * toRate
                    val amountInUsd = if (fromCurrencyCode == "USD") amount else (amount / fromRate)
                    val convertedTotal = if (toCurrencyCode == "USD") amountInUsd else (amountInUsd * toRate)

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF30D158)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("EQUIVALENT VALUE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text(
                                text = "${String.format("%.2f", convertedTotal)} $toCurrencyCode",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF30D158),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "1 $fromCurrencyCode = ${String.format("%.4f", (if (fromCurrencyCode == "USD") toRate else toRate / fromRate))} $toCurrencyCode",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }

        // LIVE FX EXCHANGE RATES BOARD
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE FOREIGN EXCHANGE (FX) BOARD",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = NeonCyan
                )
                Text("Updates Every 4s ⚡", fontSize = 10.sp, color = Color.Gray)
            }
        }

        items(fxRates) { rate ->
            val isPositive = rate.changePercent24h >= 0
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth().testTag("fx_rate_${rate.code}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(rate.flagEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("${rate.code} / USD", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text(rate.name, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format("%.4f", rate.rateToUsd),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isPositive) Color(0xFF30D158).copy(alpha = 0.2f) else Color(0xFFFF453A).copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = if (isPositive) Color(0xFF30D158) else Color(0xFFFF453A),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${if (isPositive) "+" else ""}${String.format("%.2f", rate.changePercent24h)}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPositive) Color(0xFF30D158) else Color(0xFFFF453A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyDropdownSelector(
    label: String,
    selectedCode: String,
    onSelectCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currencyOptions = listOf("USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "CNY", "INR", "SGD", "BRL")

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedCode,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select")
                }
            },
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkSurfaceElevated)
        ) {
            currencyOptions.forEach { code ->
                DropdownMenuItem(
                    text = { Text(code, color = Color.White, fontWeight = FontWeight.Bold) },
                    onClick = {
                        onSelectCode(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MarketIndicesTab() {
    val indices = listOf(
        MarketIndex("S&P 500 Index", "SPX", "5,432.10", "+28.40", +0.52, true),
        MarketIndex("NASDAQ Composite", "IXIC", "17,688.20", "+112.10", +0.64, true),
        MarketIndex("Dow Jones Industrial", "DJI", "38,910.50", "-42.15", -0.11, false),
        MarketIndex("FTSE 100", "UKX", "8,210.40", "+14.80", +0.18, true),
        MarketIndex("Nikkei 225", "N225", "38,450.00", "-180.20", -0.47, false),
        MarketIndex("Gold Spot ($/oz)", "XAU", "$2,342.80", "+12.50", +0.54, true),
        MarketIndex("Brent Crude Oil", "OIL", "$85.40", "-0.85", -0.98, false),
        MarketIndex("Bitcoin Index", "BTC/USD", "$67,450.00", "+1,240.00", +1.87, true)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "GLOBAL FINANCIAL INDICES & COMMODITIES",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = WarmAmber
            )
        }

        items(indices) { idx ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(idx.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(idx.symbol, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(idx.valueFormatted, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                        Text(
                            text = "${if (idx.isUp) "+" else ""}${idx.changePoints} (${if (idx.isUp) "+" else ""}${String.format("%.2f", idx.changePercent)}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (idx.isUp) Color(0xFF30D158) else Color(0xFFFF453A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialNewsTab() {
    val news = listOf(
        FinancialNewsItem("Central Banks Signal Balanced Policy Rate Path", "Townsquare Press Wire", "10m ago", "MONETARY POLICY", "Federal Reserve and ECB dispatches indicate steady interest rate hold amid easing inflation metrics."),
        FinancialNewsItem("Foreign Exchange Markets See Euro Rally on Trade Boost", "Civic Forex Desk", "25m ago", "CURRENCY WIRE", "EUR/USD advances past 0.9215 threshold following strong European industrial manufacturing numbers."),
        FinancialNewsItem("Global Commodity Futures Rise as Shipping Routes Stabilize", "Maritime Freight Journal", "1h ago", "COMMODITIES", "Crude oil and gold spot prices show steady demand across international trade corridors.")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "FINANCIAL NEWS & CENTRAL BANK DISPATCHES",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = NeonCyan
            )
        }

        items(news) { item ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(4.dp), color = NeonCyan.copy(alpha = 0.2f)) {
                            Text(item.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Text(item.timeAgo, fontSize = 10.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.snippet, fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Source: ${item.source}", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun CivicWalletTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, NeonCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CIVIC LEDGER BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("$4,285.50", fontSize = 28.sp, fontWeight = FontWeight.Black, color = NeonCyan, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Deposit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = {},
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, WarmAmber),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Transfer FX", color = WarmAmber, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Text("RECENT TRANSACTIONS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarmAmber)
        }

        val txs = listOf(
            Triple("Marketplace Purchase: Vintage Radio", "-$45.00", "Today at 09:12 AM"),
            Triple("FX Exchange: USD to EUR", "+€92.15", "Yesterday"),
            Triple("Broadsheet Print Subscription", "-$12.50", "Sep 28, 2026")
        )

        items(txs) { (title, amount, time) ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        Text(time, fontSize = 10.sp, color = Color.Gray)
                    }
                    Text(amount, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (amount.startsWith("+")) Color(0xFF30D158) else Color.White)
                }
            }
        }
    }
}
