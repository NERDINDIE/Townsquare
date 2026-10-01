package com.example.data.repository

import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType

object MediaDiscoveryChannels {

    /**
     * The 9 new media channels requested for the Discovery page.
     */
    fun getNewMediaChannels(): List<MediaChannelEntity> = listOf(
        // 1. Silver Screen / cinema, theatres, and movies
        MediaChannelEntity(
            id = "channel_silver_screen",
            name = "🎬 Silver Screen",
            description = "Cinema, grand theatres, 35mm repertory retrospectives, indie festivals, and director marquee premieres.",
            category = "Cinema & Movies",
            bannerColorHex = 0xFFFF0055, // Vivid Cinema Ruby
            isFollowed = true,
            followersCount = 38400,
            morningBriefHighlight = "Midnight 70mm archival restoration of 1958 classic 'Canal Sunset' sells out all weekend screenings at the Grand Rivoli Theatre.",
            iconEmoji = "🎬"
        ),

        // 2. The Scoop / Gossip, celebrities
        MediaChannelEntity(
            id = "channel_the_scoop",
            name = "✨ The Scoop",
            description = "High-society gossip, celebrity sightings, film festival red carpets, backstage secrets, and VIP gala chronicles.",
            category = "Gossip & Celebrities",
            bannerColorHex = 0xFFFF3399, // Vibrant Magenta Pink
            isFollowed = true,
            followersCount = 54200,
            morningBriefHighlight = "Award-winning lead duo spotted rehearsing period dialogue at the Old Pier Espresso Bar, drawing eager onlookers.",
            iconEmoji = "✨"
        ),

        // 3. The Business Beat / Business in general
        MediaChannelEntity(
            id = "channel_business_beat",
            name = "💼 The Business Beat",
            description = "Commercial ventures, regional trade chambers, enterprise logistics, startup incubators, and executive strategy.",
            category = "Business & Industry",
            bannerColorHex = 0xFF00B4D8, // Corporate Steel Blue
            isFollowed = true,
            followersCount = 42100,
            morningBriefHighlight = "Townsquare Artisan Export Cooperative posts 34% surplus, announcing new zero-interest micro-grants for local workshops.",
            iconEmoji = "💼"
        ),

        // 4. Stonks / Live stocks, crypto, and investments
        MediaChannelEntity(
            id = "channel_stonks",
            name = "🚀 Stonks",
            description = "Live market tickers, crypto protocols, decentralized finance, algorithmic funds, and retail investor community pulses.",
            category = "Markets, Crypto & Investing",
            bannerColorHex = 0xFF00F5D4, // Neon Mint Green
            isFollowed = true,
            followersCount = 67300,
            morningBriefHighlight = "Municipal Solar Bond token surges +18.4% as town microgrids report record battery storage efficiency.",
            iconEmoji = "🚀"
        ),

        // 5. The Community Post / Local news in a broadsheet form
        MediaChannelEntity(
            id = "channel_community_post",
            name = "📰 The Community Post",
            description = "Hyperlocal news in authentic broadsheet form: town council votes, school reports, public hearings, and civic milestones.",
            category = "Local Broadsheet News",
            bannerColorHex = 0xFFE0A96D, // Broadsheet Sepia Parchment
            isFollowed = true,
            followersCount = 49800,
            morningBriefHighlight = "District Council unanimously approves converting Old Railway Pier into a permanent public park and farmers' market.",
            iconEmoji = "📰"
        ),

        // 6. The Curator / Collecting and antiques
        MediaChannelEntity(
            id = "channel_the_curator",
            name = "🏺 The Curator",
            description = "Antiquarian rarities, horology, estate auctions, vintage numismatics, rare curios, and archival heirloom conservation.",
            category = "Collecting & Antiques",
            bannerColorHex = 0xFFC77DFF, // Imperial Amethyst
            isFollowed = true,
            followersCount = 28900,
            morningBriefHighlight = "Intact 18th-century nautical astrolabe discovered in cellar rafters of the Old Customs House authenticated by Guild appraisers.",
            iconEmoji = "🏺"
        ),

        // 7. Grapevine / Winery, alcohol, and bar culture
        MediaChannelEntity(
            id = "channel_grapevine",
            name = "🍷 Grapevine",
            description = "Artisanal vineyards, natural wines, craft distilleries, hidden speakeasies, barrel room tastings, and mixology guides.",
            category = "Wine, Spirits & Bar Culture",
            bannerColorHex = 0xFF9E0059, // Rich Burgundy
            isFollowed = true,
            followersCount = 37600,
            morningBriefHighlight = "Sunken Valley Cellars unveils its rare 10-year solera amber vermouth, infused with 24 alpine herbs and citrus peel.",
            iconEmoji = "🍷"
        ),

        // 8. The Urbanist / City culture and attractions
        MediaChannelEntity(
            id = "channel_the_urbanist",
            name = "🏙️ The Urbanist",
            description = "Metropolitan design, pedestrianized plazas, architecture walking tours, night markets, and city living culture.",
            category = "City Culture & Urbanism",
            bannerColorHex = 0xFF3A86FF, // Electric Cobalt Blue
            isFollowed = true,
            followersCount = 44500,
            morningBriefHighlight = "Heritage Canal Tramway completes historic route restoration, offering silent electric night tours with illuminated stops.",
            iconEmoji = "🏙️"
        ),

        // 9. Tyres / Cars and other types of vehicles
        MediaChannelEntity(
            id = "channel_tyres",
            name = "🏎️ Tyres",
            description = "Automotive passion: classic sports cars, electric restomods, touring rallies, custom motorbikes, and track telemetry.",
            category = "Automotive & Mobility",
            bannerColorHex = 0xFFFF5400, // Racing Tangerine
            isFollowed = true,
            followersCount = 51200,
            morningBriefHighlight = "Annual Autumn Hillclimb features 60 vintage roadsters and retro electric prototypes taking to the winding ridge highway.",
            iconEmoji = "🏎️"
        )
    )

    /**
     * Generates rich initial story dispatches for the 9 new channels.
     */
    fun getNewChannelsMediaItems(now: Long = System.currentTimeMillis()): List<MediaItemEntity> = listOf(
        // Silver Screen
        MediaItemEntity(
            type = MediaType.NEWSPAPER_MAGAZINE.name,
            title = "The Golden Age of 70mm Repertory Cinema Returns to the Grand Rivoli",
            subtitle = "Why celluloid projection and historic movie houses are capturing a new generation of filmgoers.",
            authorName = "Clara De Laurentiis",
            authorHandle = "@claracinema",
            channelId = "channel_silver_screen",
            channelName = "🎬 Silver Screen",
            bodyText = """The smell of warm buttery popcorn and carbon arc lamps fills the lobby of the 1928 Grand Rivoli Theatre. While modern streaming platforms offer endless menus of compressed video, our city’s independent cinemas are experiencing a historic renaissance.

Head projectionist Matteo Vance carefully threads a pristine 70mm print of the 1958 nautical mystery 'Canal Sunset' into the dual dual-gauge projector. 'You don't just watch film on 70mm,' Vance explains, adjusting the lens focus. 'You breathe it in. The grain, the rich color depth, the physical shutter clicking twenty-four times every second.'

This weekend kicks off the annual Autumn Repertory Festival, showcasing 40 restored classics, experimental midnight shorts, and Q&A sessions with contemporary cinematographers.""",
            readTimeMinutes = 5,
            likesCount = 142,
            timestamp = now - 1800000L,
            tags = "#cinema #movies #theatre #film #70mm #classic"
        ),
        MediaItemEntity(
            type = MediaType.PODCAST_EPISODE.name,
            title = "Behind the Lens: Director Nicolas K. on Shooting in Historic Disticts",
            subtitle = "A 22-minute conversation on lighting cobblestones, anamorphic lenses, and location scouting.",
            authorName = "Silver Screen Audio",
            authorHandle = "@silverscreen_fm",
            channelId = "channel_silver_screen",
            channelName = "🎬 Silver Screen",
            bodyText = "Director Nicolas discusses his award-winning period drama filmed entirely on location in District 4.",
            durationSeconds = 1320,
            mediaUrl = "https://audio.townsquare.local/silver_screen_ep12.mp3",
            timestamp = now - 3600000L,
            likesCount = 98,
            tags = "#podcast #film #cinema #interview"
        ),

        // The Scoop
        MediaItemEntity(
            type = MediaType.SOCIAL_POST.name,
            title = "Midnight Sighting: A-Listers Gather for Secret Canal Reading",
            subtitle = "Inside the velvet-curtained reading room where next year's biggest mystery drama took shape.",
            authorName = "The Velvet Quill",
            authorHandle = "@thevelvetquill",
            channelId = "channel_the_scoop",
            channelName = "✨ The Scoop",
            bodyText = """Tucked behind the spice merchants on North Wharf, the Lantern Club’s private room had all the blinds drawn past 1:00 AM last night.

Sources confirm that award-winning actress Elena Rostova and director Daniel Mercer spent four hours table-reading a rumored adaptation of the Clocktower Heist. Witnesses report celebratory champagne toasts and handwritten script annotations exchanged until dawn.

Expect an official studio announcement ahead of the Autumn Film Gala!""",
            readTimeMinutes = 3,
            likesCount = 285,
            timestamp = now - 2400000L,
            tags = "#gossip #celebrities #thescoop #vip #backstage"
        ),

        // The Business Beat
        MediaItemEntity(
            type = MediaType.NEWSPAPER_MAGAZINE.name,
            title = "Townsquare Industrial Corridor: The Cooperative Manufacturing Boom",
            subtitle = "How shared robotics hubs and clean electric supply chains are driving record local margins.",
            authorName = "Marcus Sterling",
            authorHandle = "@sterling_business",
            channelId = "channel_business_beat",
            channelName = "💼 The Business Beat",
            bodyText = """In the sprawling brick warehouses along the East Canal, a new breed of enterprise is outperforming conventional global supply chains.

The Townsquare Makers Guild, comprised of 85 small-to-medium manufacturing and design businesses, announced an all-time record 34% quarterly operating surplus. By centralizing solar battery microgrids, precision 5-axis CNC machining centers, and localized water-transit logistics, members have cut freight costs by over half.

'We are proving that decentralized, cooperative production is faster, cheaper, and vastly more resilient,' notes Guild President Sarah Lin.""",
            readTimeMinutes = 6,
            likesCount = 173,
            timestamp = now - 4200000L,
            tags = "#business #economy #manufacturing #startups #commerce"
        ),

        // Stonks
        MediaItemEntity(
            type = MediaType.SOCIAL_POST.name,
            title = "⚡ Green Energy Tokenized Bonds Hit New Highs as Grid Yields Expand",
            subtitle = "Decentralized civic debt instruments outpace treasury benchmarks with 7.8% verified yields.",
            authorName = "Stonks Desk Analyst",
            authorHandle = "@stonks_daily",
            channelId = "channel_stonks",
            channelName = "🚀 Stonks",
            bodyText = """Market Wrap:
- TS-SOLAR (Townsquare Solar Microgrid Token): +14.2% today, trading at $104.80.
- CIVIC-ETH Liquidity Pool: TVL reaches $42M with annual staking yields holding steady at 6.4%.
- Municipal Commodity Basket: Timber and recycled copper futures gain momentum following infrastructure funding vote.

Key Takeaway: Investors are rapidly rotating capital into tangible civic yield protocols backed by real-world physical assets.""",
            readTimeMinutes = 2,
            likesCount = 310,
            timestamp = now - 1200000L,
            tags = "#stonks #crypto #investing #stocks #finance #defi"
        ),

        // The Community Post
        MediaItemEntity(
            type = MediaType.NEWSPAPER_MAGAZINE.name,
            title = "Town Council Approves Waterfront Transformation: 10-Acre Promenade Planned",
            subtitle = "BROADSHEET SPECIAL: Public docks, shaded timber gazebos, and free community reading pavilions.",
            authorName = "Townsquare Editorial Board",
            authorHandle = "@community_post",
            channelId = "channel_community_post",
            channelName = "📰 The Community Post",
            bodyText = """DISTRICT FOUR SPECIAL DISPATCH — In a unanimous 9-0 vote last evening, the Townsquare Municipal Council ratified the Waterfront Promenade Master Plan.

The landmark resolution designates the former coal and railway marshalling yard for complete public reclamation. Key features include:
1. Public Timber Boardwalk: 1.8 miles of illuminated walking paths along the tidal basin.
2. Free Municipal Newspaper & Book Pavilions: Weatherproof kiosks stocked daily with broadsheet editions and public literature.
3. Native Estuary Salt Marshes: Natural flood-mitigation wetlands preserving local heron and kingfisher nesting grounds.

Groundbreaking is scheduled for early spring, with the first phase open to the public by midsummer.""",
            readTimeMinutes = 4,
            likesCount = 264,
            timestamp = now - 900000L,
            tags = "#localnews #broadsheet #community #towncouncil #civic"
        ),

        // The Curator
        MediaItemEntity(
            type = MediaType.NEWSPAPER_MAGAZINE.name,
            title = "Restoring an 1884 Marine Chronometer: The Art of Clockwork Conservation",
            subtitle = "Inside the horological atelier bringing centuries-old navigational instruments back to life.",
            authorName = "Evelyn Thorne",
            authorHandle = "@curator_thorne",
            channelId = "channel_the_curator",
            channelName = "🏺 The Curator",
            bodyText = """Under the magnifying loupe of master restorer Henrik Lindt, balance wheels crafted in the Victorian era beat with immaculate precision once more.

The two-day marine chronometer, stamped with the mark of the Royal Naval Observatory, had spent decades gathering dust in an attic off Pier 14. Henrik spent three weeks dismantling its 240 components, degreasing hardened whale-oil lubricants, and re-polishing the steel pivots by hand on boxwood laps.

'When you wind an antique clock, you are participating in a conversation across centuries,' Lindt smiles. 'These machines were built to outlive civilizations.'""",
            readTimeMinutes = 5,
            likesCount = 188,
            timestamp = now - 5400000L,
            tags = "#antiques #collecting #horology #clocks #curator #history"
        ),

        // Grapevine
        MediaItemEntity(
            type = MediaType.NEWSPAPER_MAGAZINE.name,
            title = "A Night Tour of the Valley's Subterranean Barrel Cellars",
            subtitle = "Native yeasts, amphora aging, and the revival of dry amber heritage wines.",
            authorName = "Sommelier Jean-Paul",
            authorHandle = "@grapevine_jp",
            channelId = "channel_grapevine",
            channelName = "🍷 Grapevine",
            bodyText = """Twenty feet beneath the limestone terraces of Sunken Valley, the air turns cool, damp, and fragrant with cedar and fermenting grape skins.

Vintner Helene Roy leads us past rows of clay amphorae buried directly into the earthen floor. 'We don't use temperature control coils or synthetic fining agents,' Roy explains, pouring a glass of 2024 Skin-Contact Roussanne. 'The clay breathes, the earth stabilizes the temperature, and the wild hillside yeasts do all the work.'

The result is a wine with electric acidity, orange blossoms, and a deep flinty minerality that mirrors the surrounding cliffs.""",
            readTimeMinutes = 4,
            likesCount = 205,
            timestamp = now - 3000000L,
            tags = "#wine #barculture #grapevine #winery #sommelier #spirits"
        ),

        // The Urbanist
        MediaItemEntity(
            type = MediaType.NEWSPAPER_MAGAZINE.name,
            title = "The 15-Minute Promenade: How Pedestrian Superblocks Reclaimed Our Streets",
            subtitle = "Urban architects celebrate three years of car-free town squares and flourishing canal culture.",
            authorName = "Kiran Patel, AICP",
            authorHandle = "@urbanist_patel",
            channelId = "channel_the_urbanist",
            channelName = "🏙️ The Urbanist",
            bodyText = """Three years ago, the Old Town Core was choked with delivery vans and idling traffic. Today, children play chess on granite tables outside open-air bakery patios while quiet electric cargo trikes glide effortlessly across cobblestones.

Urbanist studies reveal a 45% increase in small retail revenues, zero pedestrian collisions, and an 80% decrease in street-level noise pollution since bollards restricted private automobile transit.

'Streets are not merely conduits for metal boxes,' says urban designer Maya Vance. 'Streets are the collective living rooms of our society.'""",
            readTimeMinutes = 5,
            likesCount = 241,
            timestamp = now - 3600000L,
            tags = "#urbanist #city #architecture #publicspace #urbanplanning"
        ),

        // Tyres
        MediaItemEntity(
            type = MediaType.NEWSPAPER_MAGAZINE.name,
            title = "The Autumn Hillclimb: 60 Classic Roadsters Take on the Coastal Ridge",
            subtitle = "From howling Alfa twin-cams to custom electric Porsche restomods, the pass was pure poetry.",
            authorName = "Leo 'Rev' Rossi",
            authorHandle = "@tyres_rossi",
            channelId = "channel_tyres",
            channelName = "🏎️ Tyres",
            bodyText = """At 6:30 AM, the morning mist was sliced open by the sharp bark of open velocity stacks and the scent of castor bean oil.

The Coastal Ridge Hillclimb is a 14-mile serpentine test of driver nerve, chassis balance, and braking endurance. Among this year's standout entries: a 1965 Jaguar E-Type Low Drag Coupe running a hand-tuned straight-six, alongside a whisper-silent carbon-bodied electric restomod that devoured hairpins with 600 instantaneous horsepower.

'It doesn't matter what powers the machine,' laughs veteran racer Giorgio Conti. 'It’s about how the steering wheel talks to your hands when you apex through the eucalyptus grove.'""",
            readTimeMinutes = 5,
            likesCount = 299,
            timestamp = now - 2100000L,
            tags = "#cars #automotive #tyres #rally #motorsport #roadster"
        )
    )
}
