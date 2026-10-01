package com.example.ui.plus.marketplace

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.marketplace.*
import com.example.ui.plus.model.CartItem
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MarketplaceCheckoutSheet(
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onOrderPlaced: (MarketplaceOrder) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var checkoutStep by remember { mutableIntStateOf(0) } // 0: Address, 1: Shipping, 2: Payment, 3: Review & Place

    var recipientName by remember { mutableStateOf("Alexander Vance") }
    var streetAddress by remember { mutableStateOf("442 Willow Canal Promenade, Apt 3B") }
    var district by remember { mutableStateOf("District 4 - Old Quarter") }
    var postalCode by remember { mutableStateOf("TS-10442") }
    var deliveryNotes by remember { mutableStateOf("Leave in lockbox if absent") }

    var selectedShipping by remember { mutableStateOf(ShippingMethod.STANDARD_CANAL) }
    var selectedPayment by remember { mutableStateOf(MarketplacePaymentMethod.CIVIC_LEDGER_PAY) }
    var promoCodeInput by remember { mutableStateOf("TOWNSQUARE10") }
    var promoApplied by remember { mutableStateOf(true) }

    var isProcessing by remember { mutableStateOf(false) }
    var confirmedOrder by remember { mutableStateOf<MarketplaceOrder?>(null) }

    val rawSubtotal = cartItems.sumOf { it.listing.price * it.quantity }
    val discount = if (promoApplied) rawSubtotal * 0.10 else 0.0
    val tax = (rawSubtotal - discount) * 0.08
    val shippingCost = selectedShipping.cost
    val finalTotal = rawSubtotal - discount + tax + shippingCost + selectedPayment.fee

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(20.dp),
            color = DarkBg,
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Marketplace Checkout",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${cartItems.sumOf { it.quantity }} item(s) • Total: $${String.format("%.2f", finalTotal)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmAmber
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DarkTextMuted)
                    }
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Progress Step Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val steps = listOf("Address", "Shipping", "Payment", "Review")
                    steps.forEachIndexed { index, stepName ->
                        val isCurrent = checkoutStep == index
                        val isDone = checkoutStep > index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                isDone -> Color(0xFF30D158).copy(alpha = 0.2f)
                                isCurrent -> NeonCyan.copy(alpha = 0.2f)
                                else -> DarkSurfaceVariant
                            },
                            border = BorderStroke(1.dp, if (isCurrent) NeonCyan else Color.Transparent),
                            modifier = Modifier.clickable { if (checkoutStep < 4) checkoutStep = index }
                        ) {
                            Text(
                                text = "${index + 1}. $stepName",
                                fontSize = 10.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) NeonCyan else if (isDone) Color(0xFF30D158) else DarkTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step Content
                when (checkoutStep) {
                    0 -> {
                        // STEP 0: DELIVERY ADDRESS
                        Text(text = "1. DELIVERY DESTINATION & RECIPIENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = recipientName,
                            onValueChange = { recipientName = it },
                            label = { Text("Recipient Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = streetAddress,
                            onValueChange = { streetAddress = it },
                            label = { Text("Street Address / Canal Pier") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = district,
                                onValueChange = { district = it },
                                label = { Text("District") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = postalCode,
                                onValueChange = { postalCode = it },
                                label = { Text("Postal Code") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deliveryNotes,
                            onValueChange = { deliveryNotes = it },
                            label = { Text("Delivery Instructions / Gate Code") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { checkoutStep = 1 },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Continue to Shipping Method", fontWeight = FontWeight.Bold)
                        }
                    }

                    1 -> {
                        // STEP 1: SHIPPING METHOD
                        Text(text = "2. SELECT SHIPPING & COURIER SPEED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        ShippingMethod.entries.forEach { method ->
                            val isSel = selectedShipping == method
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) NeonCyan.copy(alpha = 0.1f) else DarkSurface,
                                border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedShipping = method }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Text(text = method.emoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = method.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                            Text(text = "${method.description} • Est: ${method.estimatedArrival}", fontSize = 10.sp, color = DarkTextSecondary)
                                        }
                                    }
                                    Text(
                                        text = if (method.cost == 0.0) "FREE" else "$${String.format("%.2f", method.cost)}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = if (method.cost == 0.0) Color(0xFF30D158) else WarmAmber
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { checkoutStep = 0 },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Back")
                            }
                            Button(
                                onClick = { checkoutStep = 2 },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(2f).height(48.dp)
                            ) {
                                Text("Continue to Payment", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    2 -> {
                        // STEP 2: PAYMENT METHOD & PROMO
                        Text(text = "3. SELECT PAYMENT PROTOCOL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        MarketplacePaymentMethod.entries.forEach { method ->
                            val isSel = selectedPayment == method
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) NeonCyan.copy(alpha = 0.1f) else DarkSurface,
                                border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedPayment = method }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = method.iconEmoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = method.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                        Text(text = method.detail, fontSize = 10.sp, color = DarkTextSecondary)
                                    }
                                    RadioButton(selected = isSel, onClick = { selectedPayment = method })
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Promo code box
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = promoCodeInput,
                                onValueChange = { promoCodeInput = it },
                                label = { Text("Promo Code (e.g. TOWNSQUARE10)") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    promoApplied = promoCodeInput.uppercase() == "TOWNSQUARE10"
                                    Toast.makeText(context, if (promoApplied) "10% Townsquare promo applied!" else "Invalid code", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                                modifier = Modifier.height(54.dp)
                            ) {
                                Text("Apply")
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { checkoutStep = 1 },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Back")
                            }
                            Button(
                                onClick = { checkoutStep = 3 },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(2f).height(48.dp)
                            ) {
                                Text("Review Order", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    3 -> {
                        // STEP 3: ORDER REVIEW & CONFIRM
                        Text(text = "4. ORDER REVIEW & DISPATCH CONFIRMATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = "Items in Order (${cartItems.size})", fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                cartItems.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "${item.quantity}x ${item.listing.title}", fontSize = 12.sp, color = DarkTextSecondary, modifier = Modifier.weight(1f))
                                        Text(text = "$${String.format("%.2f", item.listing.price * item.quantity)}", fontSize = 12.sp, color = Color.White)
                                    }
                                }

                                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Subtotal:", fontSize = 11.sp, color = DarkTextSecondary)
                                    Text(text = "$${String.format("%.2f", rawSubtotal)}", fontSize = 11.sp, color = Color.White)
                                }
                                if (discount > 0) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Promo Discount (10%):", fontSize = 11.sp, color = Color(0xFF30D158))
                                        Text(text = "-$${String.format("%.2f", discount)}", fontSize = 11.sp, color = Color(0xFF30D158))
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Shipping (${selectedShipping.title}):", fontSize = 11.sp, color = DarkTextSecondary)
                                    Text(text = "$${String.format("%.2f", shippingCost)}", fontSize = 11.sp, color = Color.White)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Municipal Sales Tax (8%):", fontSize = 11.sp, color = DarkTextSecondary)
                                    Text(text = "$${String.format("%.2f", tax)}", fontSize = 11.sp, color = Color.White)
                                }

                                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Grand Total:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = WarmAmber)
                                    Text(text = "$${String.format("%.2f", finalTotal)}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = WarmAmber)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Shipping Address summary
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Deliver To: $recipientName", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                Text(text = "$streetAddress, $district, $postalCode", fontSize = 11.sp, color = DarkTextSecondary)
                                Text(text = "Payment: ${selectedPayment.title}", fontSize = 11.sp, color = NeonCyan)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    isProcessing = true
                                    val order = MarketplaceCheckoutEngine.processCheckout(
                                        items = cartItems,
                                        address = ShippingAddress(recipientName, streetAddress, district, postalCode, deliveryNotes),
                                        shippingMethod = selectedShipping,
                                        paymentMethod = selectedPayment,
                                        promoCode = if (promoApplied) promoCodeInput else null
                                    )
                                    isProcessing = false
                                    confirmedOrder = order
                                    checkoutStep = 4
                                    onOrderPlaced(order)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30D158), contentColor = Color(0xFF003544)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("place_order_button"),
                            enabled = !isProcessing
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(color = Color(0xFF003544), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Authorizing Civic Payment...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Authorize & Place Order ($${String.format("%.2f", finalTotal)})", fontWeight = FontWeight.Black, fontSize = 15.sp)
                            }
                        }
                    }

                    4 -> {
                        // STEP 4: ORDER CONFIRMATION & LIVE TRACKING RECEIPT
                        val order = confirmedOrder
                        if (order != null) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF0A2216),
                                border = BorderStroke(2.dp, Color(0xFF30D158)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "🎉", fontSize = 42.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Order Placed & Confirmed!", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = Color.White)
                                    Text(text = "Townsquare District Merchant Syndicate", fontSize = 11.sp, color = Color(0xFF30D158))

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = DarkSurface,
                                        border = BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(text = "Order ID:", fontSize = 11.sp, color = DarkTextSecondary)
                                                Text(text = order.orderId, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = NeonCyan)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(text = "Tracking Code:", fontSize = 11.sp, color = DarkTextSecondary)
                                                Text(text = order.trackingCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = WarmAmber)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(text = "Total Charged:", fontSize = 11.sp, color = DarkTextSecondary)
                                                Text(text = "$${String.format("%.2f", order.totalAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(text = "Estimated Arrival:", fontSize = 11.sp, color = DarkTextSecondary)
                                                Text(text = order.shippingMethod.estimatedArrival, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF30D158))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = onDismiss,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30D158), contentColor = Color(0xFF003544)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text("Done", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
