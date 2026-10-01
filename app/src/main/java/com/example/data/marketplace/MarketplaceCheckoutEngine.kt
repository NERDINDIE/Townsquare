package com.example.data.marketplace

import com.example.ui.plus.model.CartItem
import kotlinx.coroutines.delay
import java.util.UUID

enum class ShippingMethod(
    val title: String,
    val cost: Double,
    val estimatedArrival: String,
    val emoji: String,
    val description: String
) {
    STANDARD_CANAL("Eco Canal Barge & Courier", 3.50, "Tomorrow Afternoon", "⛵", "Low-emissions zero carbon waterway transit"),
    EXPRESS_DISPATCH("District Electric Van Courier", 7.00, "Today within 2 hours", "⚡", "Priority direct courier delivery to door"),
    KIOSK_LOCKER("Townsquare Smart Kiosk Pickup", 0.00, "Ready in 30 minutes", "📦", "Free 24/7 pickup at Pier 14 or Clocktower Square")
}

enum class MarketplacePaymentMethod(val title: String, val fee: Double, val iconEmoji: String, val detail: String) {
    CIVIC_LEDGER_PAY("Townsquare Civic Ledger Pay", 0.0, "🛡️", "Instant zero-fee biometric debit from Citizen Balance"),
    CREDIT_CARD("Visa / Mastercard Credit Card", 0.0, "💳", "Secure encrypted tokenization (•••• 4242)"),
    ARTISAN_COOP_CREDITS("Artisan Guild Store Credits", 0.0, "🪙", "Community cooperative reward balance ($150 available)"),
    CASH_ON_DELIVERY("Cash / Physical Currency on Handover", 1.50, "💵", "Pay vendor directly at delivery or kiosk")
}

data class ShippingAddress(
    val recipientName: String = "Alexander Vance",
    val streetAddress: String = "442 Willow Canal Promenade, Apt 3B",
    val district: String = "District 4 - Old Quarter",
    val postalCode: String = "TS-10442",
    val deliveryNotes: String = "Leave inside secure lobby lockbox or ring buzzer 3B."
)

data class MarketplaceOrder(
    val orderId: String = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}",
    val items: List<CartItem>,
    val subtotal: Double,
    val shippingMethod: ShippingMethod,
    val shippingFee: Double,
    val taxAmount: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val address: ShippingAddress,
    val paymentMethod: MarketplacePaymentMethod,
    val timestamp: String = "Just now",
    var status: String = "Confirmed • Preparing Dispatch",
    val trackingCode: String = "TRK-TS-${(100000..999999).random()}"
)

object MarketplaceCheckoutEngine {
    private val ordersList = mutableListOf<MarketplaceOrder>()

    fun getOrders(): List<MarketplaceOrder> = ordersList.toList()

    suspend fun processCheckout(
        items: List<CartItem>,
        address: ShippingAddress,
        shippingMethod: ShippingMethod,
        paymentMethod: MarketplacePaymentMethod,
        promoCode: String? = null
    ): MarketplaceOrder {
        // Simulate payment processing delay
        delay(1400L)

        val subtotal = items.sumOf { it.listing.price * it.quantity }
        val discount = if (promoCode?.uppercase() == "TOWNSQUARE10") subtotal * 0.10 else 0.0
        val tax = (subtotal - discount) * 0.08
        val shippingFee = shippingMethod.cost
        val total = (subtotal - discount + tax + shippingFee + paymentMethod.fee).coerceAtLeast(0.0)

        val order = MarketplaceOrder(
            items = items,
            subtotal = subtotal,
            shippingMethod = shippingMethod,
            shippingFee = shippingFee,
            taxAmount = tax,
            discountAmount = discount,
            totalAmount = total,
            address = address,
            paymentMethod = paymentMethod
        )

        ordersList.add(0, order)
        return order
    }
}
