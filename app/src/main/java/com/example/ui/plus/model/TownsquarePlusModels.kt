package com.example.ui.plus.model

import java.util.UUID

// ==========================================
// 1. TOWNSQUARE PHONE DATA MODELS
// ==========================================

enum class CallType {
    INCOMING, OUTGOING, MISSED
}

data class ContactItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val roleOrOrg: String,
    val phoneNumber: String,
    val avatarLetter: String,
    val avatarColorHex: Long = 0xFF00D2FF,
    val isFavorite: Boolean = false,
    val email: String? = null
)

data class CallLogItem(
    val id: String = UUID.randomUUID().toString(),
    val contactName: String,
    val phoneNumber: String,
    val callType: CallType,
    val timestamp: String,
    val durationText: String? = null
)

data class VoicemailItem(
    val id: String = UUID.randomUUID().toString(),
    val callerName: String,
    val phoneNumber: String,
    val timestamp: String,
    val durationSeconds: Int,
    val transcript: String,
    val isRead: Boolean = false
)

// ==========================================
// 2. TOWNSQUARE MAILBOX DATA MODELS
// ==========================================

enum class MailFolder {
    INBOX, STARRED, SENT, DRAFTS, ARCHIVE, TRASH
}

data class MailAttachment(
    val name: String,
    val sizeText: String,
    val type: String // "pdf", "image", "audio", "doc"
)

data class EmailItem(
    val id: String = UUID.randomUUID().toString(),
    val senderName: String,
    val senderEmail: String,
    val recipientEmail: String = "editor@townsquare.media",
    val subject: String,
    val snippet: String,
    val body: String,
    val timestamp: String,
    val folder: MailFolder = MailFolder.INBOX,
    val isUnread: Boolean = true,
    val isStarred: Boolean = false,
    val hasAttachments: Boolean = false,
    val attachments: List<MailAttachment> = emptyList(),
    val avatarColorHex: Long = 0xFF1E88E5
)

// ==========================================
// 3. MAPS & TRAVEL MAGAZINE DATA MODELS
// ==========================================

enum class SpotCategory(val title: String, val emoji: String) {
    HISTORIC("Heritage & Sights", "🏛️"),
    CAFES("Cafes & Roasters", "☕"),
    CULTURE("Museums & Art", "🎨"),
    NATURE("Parks & Walks", "🌲"),
    MARKET("Local Markets", "🛍️"),
    PRESS("Newsstands & Books", "📰")
}

data class MapLocationSpot(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: SpotCategory,
    val description: String,
    val address: String,
    val openingHours: String,
    val rating: Double,
    val reviewsCount: Int,
    val latitudeOffset: Float, // for custom interactive vector map positioning
    val longitudeOffset: Float,
    val photoUrl: String,
    val audioGuideDuration: String? = null,
    val isCuratedPick: Boolean = false,
    val tags: List<String> = emptyList()
)

data class TravelMagazineArticle(
    val id: String = UUID.randomUUID().toString(),
    val issueTitle: String,
    val editionNumber: String,
    val title: String,
    val subtitle: String,
    val author: String,
    val readTimeMinutes: Int,
    val heroImageUrl: String,
    val contentParagraphs: List<String>,
    val highlights: List<String>,
    val recommendedStopNames: List<String>
)

data class ItineraryStop(
    val id: String = UUID.randomUUID().toString(),
    val timeSlot: String,
    val spotName: String,
    val note: String,
    val isCompleted: Boolean = false
)

// ==========================================
// 4. TOWNSQUARE MARKETPLACE DATA MODELS
// ==========================================

enum class MarketCategory(val label: String, val iconEmoji: String) {
    ALL("All Items", "✨"),
    TECH("Electronics & Audio", "📻"),
    VINTAGE("Antiques & Oddities", "🕰️"),
    BOOKS("Press & Literature", "📚"),
    HOME("Studio & Living", "🪴"),
    FASHION("Apparel & Vintage", "🧥"),
    ARTISANAL("Handcrafted & Local", "🏺")
}

enum class ItemCondition(val label: String) {
    BRAND_NEW("Brand New"),
    LIKE_NEW("Like New / Mint"),
    EXCELLENT("Good Vintage"),
    RESTORED("Carefully Restored")
}

data class MarketplaceListing(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val price: Double,
    val originalPrice: Double? = null,
    val category: MarketCategory,
    val condition: ItemCondition,
    val description: String,
    val sellerName: String,
    val sellerRating: Double,
    val sellerLocation: String,
    val imageUrl: String,
    val isSaved: Boolean = false,
    val datePosted: String = "Today",
    val inStock: Boolean = true
)

data class CartItem(
    val listing: MarketplaceListing,
    val quantity: Int = 1
)

// ==========================================
// SEED GENERATOR
// ==========================================

object TownsquarePlusSeed {

    fun generateInitialContacts(): List<ContactItem> = listOf(
        ContactItem(
            name = "Mayor's Civic Office",
            roleOrOrg = "Municipal Affairs Desk",
            phoneNumber = "+1 (555) 019-2831",
            avatarLetter = "M",
            avatarColorHex = 0xFFFF9F1C,
            isFavorite = true,
            email = "civic.desk@townsquare.gov"
        ),
        ContactItem(
            name = "Townsquare Newsroom",
            roleOrOrg = "Breaking Editorial Wire",
            phoneNumber = "+1 (555) 439-0021",
            avatarLetter = "T",
            avatarColorHex = 0xFF00D2FF,
            isFavorite = true,
            email = "newsdesk@townsquare.media"
        ),
        ContactItem(
            name = "Harbor Weather Station",
            roleOrOrg = "Maritime & Meteorological Watch",
            phoneNumber = "+1 (555) 882-9410",
            avatarLetter = "H",
            avatarColorHex = 0xFF2EC4B6,
            isFavorite = false,
            email = "forecast@harborweather.org"
        ),
        ContactItem(
            name = "Elena Rostova",
            roleOrOrg = "Chief Investigative Reporter",
            phoneNumber = "+1 (555) 723-1194",
            avatarLetter = "E",
            avatarColorHex = 0xFF9D4EDD,
            isFavorite = true,
            email = "e.rostova@townsquare.press"
        ),
        ContactItem(
            name = "Downtown Transit Authority",
            roleOrOrg = "Ferry & Tram Operations",
            phoneNumber = "+1 (555) 304-8819",
            avatarLetter = "D",
            avatarColorHex = 0xFF1E88E5,
            isFavorite = false,
            email = "dispatch@metrotransit.org"
        ),
        ContactItem(
            name = "Old Quarter Artisan Guild",
            roleOrOrg = "Craftsmen & Market Co-op",
            phoneNumber = "+1 (555) 612-4402",
            avatarLetter = "A",
            avatarColorHex = 0xFFE71D36,
            isFavorite = false,
            email = "guild@oldquarter.market"
        )
    )

    fun generateInitialCallLogs(): List<CallLogItem> = listOf(
        CallLogItem(
            contactName = "Townsquare Newsroom",
            phoneNumber = "+1 (555) 439-0021",
            callType = CallType.INCOMING,
            timestamp = "Today, 11:42 AM",
            durationText = "04:18"
        ),
        CallLogItem(
            contactName = "Elena Rostova",
            phoneNumber = "+1 (555) 723-1194",
            callType = CallType.OUTGOING,
            timestamp = "Today, 09:15 AM",
            durationText = "12:05"
        ),
        CallLogItem(
            contactName = "Harbor Weather Station",
            phoneNumber = "+1 (555) 882-9410",
            callType = CallType.MISSED,
            timestamp = "Yesterday, 06:30 PM",
            durationText = null
        ),
        CallLogItem(
            contactName = "Mayor's Civic Office",
            phoneNumber = "+1 (555) 019-2831",
            callType = CallType.OUTGOING,
            timestamp = "Yesterday, 02:11 PM",
            durationText = "08:44"
        )
    )

    fun generateInitialVoicemails(): List<VoicemailItem> = listOf(
        VoicemailItem(
            callerName = "Elena Rostova",
            phoneNumber = "+1 (555) 723-1194",
            timestamp = "Today, 08:30 AM",
            durationSeconds = 48,
            transcript = "Hey, I just verified the shipping manifests down at Pier 4. The historical archive documents for the clocktower were intact after all. Call me back before noon editorial meeting!",
            isRead = false
        ),
        VoicemailItem(
            callerName = "Harbor Weather Station",
            phoneNumber = "+1 (555) 882-9410",
            timestamp = "Yesterday, 06:31 PM",
            durationSeconds = 32,
            transcript = "Harbor barometer has dropped 6 millibars in the last hour. Offshore squall line expected near the breakwater around 9 PM. Maritime advisory issued.",
            isRead = true
        )
    )

    fun generateInitialEmails(): List<EmailItem> = listOf(
        EmailItem(
            senderName = "Mayor's Press Office",
            senderEmail = "press@townsquare.gov",
            subject = "Embargoed Press Release: Downtown Waterfront Promenade Opening",
            snippet = "The civic council unanimously approved the autumn grand opening of the public promenade with open-air kiosks...",
            body = """Dear Editorial Board,

We are delighted to share an embargoed advance copy of the upcoming mayoral announcement regarding the completion of the Pier 14 Promenade Renewal project.

Key Milestones:
• 1.8 miles of pedestrian-first waterfront walkways
• 24 solar-powered community newsstand kiosks
• Free public Wi-Fi & live civic bulletin touchscreens

The official ribbon-cutting ceremony will take place this Thursday at 10:00 AM. We invite Townsquare TV and Radio correspondents for exclusive live broadcasting access.

Warm regards,
Office of Civic Communications""",
            timestamp = "10:15 AM",
            folder = MailFolder.INBOX,
            isUnread = true,
            isStarred = true,
            hasAttachments = true,
            attachments = listOf(
                MailAttachment("Promenade_Design_Brief.pdf", "2.4 MB", "pdf"),
                MailAttachment("Schedule_RibbonCutting.docx", "380 KB", "doc")
            ),
            avatarColorHex = 0xFFFF9F1C
        ),
        EmailItem(
            senderName = "Artisan Market Syndicate",
            senderEmail = "syndicate@oldquarter.market",
            subject = "Weekly Vendor Ledger & Sunday Flea Highlights",
            snippet = "Check out the newly registered collectors selling vintage radios, hand-bound journals, and rare print editions...",
            body = """Hello Townsquare Community,

This Sunday's Open Air Kiosk will host over 40 independent sellers, including restored 1960s transistor radios, antique copper printing presses, and locally roasted single-origin espresso beans.

A complete vendor list has been indexed into Townsquare Marketplace for online ordering and pickup.

Best,
Marketplace Curators""",
            timestamp = "Yesterday",
            folder = MailFolder.INBOX,
            isUnread = false,
            isStarred = false,
            hasAttachments = false,
            avatarColorHex = 0xFF2EC4B6
        ),
        EmailItem(
            senderName = "Townsquare Broadcast Syndicate",
            senderEmail = "tech@townsquare.media",
            subject = "Scheduled Maintenance: Relay Transmitter 7-B",
            snippet = "Broadcast engineers will calibrate the low-frequency radio relay between 02:00 and 03:30 AM tonight...",
            body = """All stations,

Please be aware of a routine 30-minute calibration on transmitter 7-B overnight. Digital streams and mobile caching will remain active through backup relays.

Townsquare Operations Team""",
            timestamp = "Sep 26",
            folder = MailFolder.INBOX,
            isUnread = false,
            isStarred = false,
            hasAttachments = false,
            avatarColorHex = 0xFF00D2FF
        ),
        EmailItem(
            senderName = "Elena Rostova",
            senderEmail = "e.rostova@townsquare.press",
            subject = "DRAFT: Investigative Dispatch on Maritime Trade Rerouting",
            snippet = "Here is my final draft before sending it to layout. Please check the statistical breakdown in section 3...",
            body = """Hey Team,

Attaching the investigative story outline. Please inspect the interview notes and let me know if we need another fact-check pass with port authorities.

Thanks,
Elena""",
            timestamp = "Sep 25",
            folder = MailFolder.DRAFTS,
            isUnread = false,
            isStarred = true,
            hasAttachments = true,
            attachments = listOf(
                MailAttachment("Maritime_Trade_RoughDraft.pdf", "4.1 MB", "pdf")
            ),
            avatarColorHex = 0xFF9D4EDD
        )
    )

    fun generateInitialMapSpots(): List<MapLocationSpot> = listOf(
        MapLocationSpot(
            name = "Grand Clocktower & Archives",
            category = SpotCategory.HISTORIC,
            description = "Centuries-old stone tower housing the municipal library and historic print presses. Panoramic observation deck at 120ft.",
            address = "1 Civic Square, Old Town",
            openingHours = "08:00 AM - 07:00 PM",
            rating = 4.9,
            reviewsCount = 1420,
            latitudeOffset = 0.35f,
            longitudeOffset = 0.45f,
            photoUrl = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=800&q=80",
            audioGuideDuration = "6 min narrative",
            isCuratedPick = true,
            tags = listOf("Historic", "Scenic Views", "Architecture")
        ),
        MapLocationSpot(
            name = "Lantern Lane Espresso & Roasters",
            category = SpotCategory.CAFES,
            description = "Cozy courtyard roastery serving pour-overs alongside fresh morning croissants and daily print editions of Townsquare Daily.",
            address = "42 Cobblestone Alley",
            openingHours = "06:30 AM - 06:00 PM",
            rating = 4.8,
            reviewsCount = 890,
            latitudeOffset = 0.22f,
            longitudeOffset = 0.62f,
            photoUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=800&q=80",
            audioGuideDuration = "3 min profile",
            isCuratedPick = true,
            tags = listOf("Specialty Coffee", "Quiet Reading", "Courtyard")
        ),
        MapLocationSpot(
            name = "Harbor Maritime Pavilion & Pier",
            category = SpotCategory.CULTURE,
            description = "Interactive maritime museum featuring historic fishing schooners, vintage navigation instruments, and open sea boardwalk.",
            address = "Pier 14, Waterfront Boulevard",
            openingHours = "09:00 AM - 08:30 PM",
            rating = 4.7,
            reviewsCount = 1105,
            latitudeOffset = 0.65f,
            longitudeOffset = 0.28f,
            photoUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
            audioGuideDuration = "8 min documentary",
            isCuratedPick = false,
            tags = listOf("Boardwalk", "Sea Views", "Exhibits")
        ),
        MapLocationSpot(
            name = "Foundry Antiquarian Books & Kiosk",
            category = SpotCategory.PRESS,
            description = "Three-floor treasure trove of vintage literature, rare periodicals, and an authentic 19th-century letterpress workshop.",
            address = "18 Ironworks Street",
            openingHours = "10:00 AM - 09:00 PM",
            rating = 4.9,
            reviewsCount = 630,
            latitudeOffset = 0.48f,
            longitudeOffset = 0.75f,
            photoUrl = "https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=800&q=80",
            audioGuideDuration = "5 min tour",
            isCuratedPick = true,
            tags = listOf("Rare Books", "Letterpress", "Curator Pick")
        ),
        MapLocationSpot(
            name = "Canal Promenade Botanical Garden",
            category = SpotCategory.NATURE,
            description = "Lush green oasis with canal-side benches, shaded willow paths, and seasonal wildflower pavilions.",
            address = "Riverside Way & Willow Path",
            openingHours = "Sunrise to Sunset",
            rating = 4.8,
            reviewsCount = 740,
            latitudeOffset = 0.72f,
            longitudeOffset = 0.58f,
            photoUrl = "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?auto=format&fit=crop&w=800&q=80",
            audioGuideDuration = "4 min meditation",
            isCuratedPick = false,
            tags = listOf("Greenery", "Walking", "Peaceful")
        ),
        MapLocationSpot(
            name = "Market Hall & Guild Arcade",
            category = SpotCategory.MARKET,
            description = "Vibrant indoor bazaar packed with fresh farm produce, handmade ceramics, vintage vinyl records, and street food.",
            address = "7 Market Arcade Plaza",
            openingHours = "07:00 AM - 06:00 PM",
            rating = 4.6,
            reviewsCount = 2200,
            latitudeOffset = 0.38f,
            longitudeOffset = 0.30f,
            photoUrl = "https://images.unsplash.com/photo-1533900298318-6b8da08a523e?auto=format&fit=crop&w=800&q=80",
            audioGuideDuration = "5 min street story",
            isCuratedPick = true,
            tags = listOf("Street Food", "Ceramics", "Local Culture")
        )
    )

    fun generateTravelMagazineIssues(): List<TravelMagazineArticle> = listOf(
        TravelMagazineArticle(
            issueTitle = "Townsquare Wanderer • Autumn Chronicle",
            editionNumber = "Vol. 14 / Issue 3",
            title = "A Day Among the Cobblestone Spires",
            subtitle = "Wandering through the forgotten printing presses, hidden canal walkways, and secret courtyards of the Old Quarter.",
            author = "Claire Fontaine, Senior Travel Correspondent",
            readTimeMinutes = 7,
            heroImageUrl = "https://images.unsplash.com/photo-1499856871958-5b9627545d1a?auto=format&fit=crop&w=800&q=80",
            contentParagraphs = listOf(
                "There is a particular amber warmth to the autumn morning light as it fractures through the stained glass of the Grand Clocktower. Before the tram bells begin their cadence, the only audible presence is the gentle rustle of newsprint at the courtyard kiosks.",
                "Venturing two blocks east onto Cobblestone Alley reveals Lantern Lane Espresso, where local writers sit with fountain pens and steaming porcelain demitasses. The smell of fresh cardamom pastries mingles with roasting Ethiopian Yirgacheffe beans.",
                "Further south along the old stone canal, the Waterfront Promenade unfolds. Here, fishermen tie up their wooden boats alongside maritime stalls selling salted cod and fresh oysters. It is a world where time seems to expand, generous to those who travel without urgency."
            ),
            highlights = listOf(
                "Best Morning Espresso: Lantern Lane (Table 4 by the ivy wall)",
                "Golden Hour Viewpoint: Clocktower South Balcony at 5:45 PM",
                "Secret Quiet Spot: Willow nook beside the canal lock gate"
            ),
            recommendedStopNames = listOf(
                "Grand Clocktower & Archives",
                "Lantern Lane Espresso & Roasters",
                "Harbor Maritime Pavilion & Pier"
            )
        ),
        TravelMagazineArticle(
            issueTitle = "The Cultural Courier • Weekend Explorer",
            editionNumber = "Vol. 14 / Issue 4",
            title = "The Artisan's Blueprint: Books, Ceramics & Sound",
            subtitle = "How our historic guild arcade survived the digital turn to become the creative heart of the city.",
            author = "Julian Vance, Cultural Essayist",
            readTimeMinutes = 5,
            heroImageUrl = "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?auto=format&fit=crop&w=800&q=80",
            contentParagraphs = listOf(
                "Step inside Foundry Antiquarian Books, and you are immediately welcomed by the smell of aged vellum and linseed ink. In the back room, a restored 1888 Heidelberg platen press still stamps custom broadsheet covers for the weekly literary edition.",
                "Across the square, the Market Hall is an energetic sensory explosion: copper kettles whistling over open charcoal braziers, antique watchmakers adjusting balance wheels with brass tweezers, and buskers fingerpicking delta blues on resonator guitars."
            ),
            highlights = listOf(
                "Must-Visit Workshop: Letterpress demonstration every Saturday at 2 PM",
                "Signature Dish: Smoked canal trout flatbread at Market Arcade Stall 12",
                "Audio Experience: Listen to the harbor foghorn symphony at twilight"
            ),
            recommendedStopNames = listOf(
                "Foundry Antiquarian Books & Kiosk",
                "Market Hall & Guild Arcade",
                "Canal Promenade Botanical Garden"
            )
        )
    )

    fun generateInitialMarketplaceListings(): List<MarketplaceListing> = listOf(
        MarketplaceListing(
            title = "Vintage 1974 Braun Transistor AM/FM Radio",
            price = 145.00,
            originalPrice = 195.00,
            category = MarketCategory.TECH,
            condition = ItemCondition.RESTORED,
            description = "Meticulously restored Dieter Rams era radio. Warm tube-like tone, balanced analog tuning knob with weighted flywheel, headphone jack, and auxiliary 3.5mm input installed.",
            sellerName = "Analog Sound Lab",
            sellerRating = 4.95,
            sellerLocation = "Old Quarter • 1.2 miles away",
            imageUrl = "https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=800&q=80",
            isSaved = true
        ),
        MarketplaceListing(
            title = "Handcrafted Full-Grain Leather Field Journal",
            price = 38.50,
            originalPrice = 45.00,
            category = MarketCategory.BOOKS,
            condition = ItemCondition.BRAND_NEW,
            description = "Hand-stitched vegetable-tanned Italian leather cover with 240 pages of 120gsm archival fountain pen friendly paper. Includes brass bookmark and pen loop.",
            sellerName = "Guild Bookbinders",
            sellerRating = 5.0,
            sellerLocation = "Artisan District • 0.8 miles away",
            imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80",
            isSaved = false
        ),
        MarketplaceListing(
            title = "Mid-Century Brass Desk Banker's Lamp",
            price = 85.00,
            originalPrice = 110.00,
            category = MarketCategory.HOME,
            condition = ItemCondition.LIKE_NEW,
            description = "Solid polished brass with emerald green glass shade. Original pull-chain mechanism, re-wired for modern LED bulbs with warm amber Edison bulb included.",
            sellerName = "Vintage Haven",
            sellerRating = 4.88,
            sellerLocation = "Downtown Plaza • 2.1 miles away",
            imageUrl = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=800&q=80",
            isSaved = false
        ),
        MarketplaceListing(
            title = "1968 Olympus Pen EE-3 Half-Frame Camera",
            price = 160.00,
            originalPrice = null,
            category = MarketCategory.TECH,
            condition = ItemCondition.LIKE_NEW,
            description = "Pristine working condition with sharp 28mm f/3.5 D.Zuiko lens. Light meter fully responsive, new light seals installed. Shoots 72 frames on standard 36-exposure 35mm film!",
            sellerName = "Harbor Film Collective",
            sellerRating = 4.92,
            sellerLocation = "Pier 14 • 1.5 miles away",
            imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80",
            isSaved = true
        ),
        MarketplaceListing(
            title = "Hand-thrown Ceramic Coffee Dripper & Mug Set",
            price = 48.00,
            originalPrice = 60.00,
            category = MarketCategory.ARTISANAL,
            condition = ItemCondition.BRAND_NEW,
            description = "Stoneware dripper with textured speckled matte glaze. Accommodates standard V60 filters. Dishwasher and microwave safe.",
            sellerName = "Pottery on the Canal",
            sellerRating = 4.97,
            sellerLocation = "Canal Basin • 0.5 miles away",
            imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=800&q=80",
            isSaved = false
        ),
        MarketplaceListing(
            title = "Vintage Wool Tweed Newsboy Cap (Size M)",
            price = 32.00,
            originalPrice = null,
            category = MarketCategory.FASHION,
            condition = ItemCondition.EXCELLENT,
            description = "Classic herringbone 100% British wool newsboy flat cap. Silk lining, very comfortable and warm for autumn morning walks.",
            sellerName = "Gentleman's Wardrobe Co.",
            sellerRating = 4.85,
            sellerLocation = "Civic Arcade • 1.9 miles away",
            imageUrl = "https://images.unsplash.com/photo-1529720317453-c8da503f2051?auto=format&fit=crop&w=800&q=80",
            isSaved = false
        )
    )
}
