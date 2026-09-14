package com.example.data.model

enum class PartnerMediumType(val label: String, val badgeColorHex: Long) {
    PRINT_BROADSHEET("Print Broadsheet", 0xFFE5A93C),
    COMMUNITY_RADIO("Community Radio", 0xFF00E5FF),
    INVESTIGATIVE_WIRE("Investigative Wire", 0xFFFF5252),
    INDEPENDENT_ZINE("Independent Zine", 0xFF69F0AE),
    DIGITAL_GAZETTE("Digital Gazette", 0xFFB388FF)
}

data class PartnerPublication(
    val id: String,
    val name: String,
    val tagline: String,
    val mastheadLead: String,
    val medium: PartnerMediumType,
    val region: String,
    val circulation: String,
    val frequency: String,
    val editorialCharter: String,
    val sampleArticleHeadline: String,
    val sampleExcerpt: String,
    val establishedYear: Int,
    val isVerifiedPartner: Boolean = true
)

data class PartnerApplication(
    val id: String,
    val publicationName: String,
    val applicantName: String,
    val contactEmail: String,
    val mediumType: String,
    val region: String,
    val circulation: String,
    val pitchAndCharter: String,
    val sampleUrlOrRss: String,
    val submittedAt: String,
    val status: String = "UNDER_EDITORIAL_REVIEW"
)

object PartnerSyndicateRepository {
    val initialPartnerPublications = listOf(
        PartnerPublication(
            id = "partner_harbor_gazette",
            name = "The Harbor & Maritime Gazette",
            tagline = "The authoritative daily register of coastal commerce, tides & deepwater shipping",
            mastheadLead = "Arthur Pendelton, Publisher • Est. 1894",
            medium = PartnerMediumType.PRINT_BROADSHEET,
            region = "North Headland & Deep Harbor District",
            circulation = "34,500 Broadsheet Readers",
            frequency = "Morning Broadsheet & Hourly Tide Wire",
            editorialCharter = "Unvarnished reporting on maritime labour, coastal ecosystem protection, port infrastructure, and seafaring community heritage.",
            sampleArticleHeadline = "Deep Harbor Pier Reconstruction Completes Three Weeks Ahead of Winter Gale Season",
            sampleExcerpt = "Heavy cedar pilings and reinforced stone seawalls guarantee twenty-year storm surge resilience for the municipal trawler fleet.",
            establishedYear = 1894
        ),
        PartnerPublication(
            id = "partner_metro_herald",
            name = "Metropolitan Civic Herald",
            tagline = "Independent watchdog desk scrutinizing municipal budgets and public transit contracts",
            mastheadLead = "Dr. Elena Vasquez, Editor-in-Chief",
            medium = PartnerMediumType.INVESTIGATIVE_WIRE,
            region = "Greater Metropolitan Area",
            circulation = "78,000 Weekly Subscribers",
            frequency = "Daily Rolling Wire & Saturday Analysis",
            editorialCharter = "Non-partisan civic investigative journalism. We track every public dime, review city council proceedings, and hold elected boards accountable.",
            sampleArticleHeadline = "Audit Uncovers $4.2M Municipal Energy Surplus Diverted to School Weatherization Funds",
            sampleExcerpt = "A quiet consensus vote among district commissioners ensures seventeen public elementary academies receive dual-glazed thermal insulation.",
            establishedYear = 1921
        ),
        PartnerPublication(
            id = "partner_eco_chronicle",
            name = "The Chronicle of Ecology & Science",
            tagline = "Field research, watershed observations, and civic biodiversity documentation",
            mastheadLead = "Robin S. Thorne & Sylvan Science Collective",
            medium = PartnerMediumType.DIGITAL_GAZETTE,
            region = "Regional River Basin & Headwaters",
            circulation = "42,000 Field Naturalists",
            frequency = "Bi-Weekly Illustrated Gazette",
            editorialCharter = "Connecting urban dwellers with regional ecology through meticulous field notes, avian census telemetry, and community soil chemistry tests.",
            sampleArticleHeadline = "Native River Otters Re-Establish Breeding Dens in Re-Wilded Industrial Canal Shallows",
            sampleExcerpt = "Biological water testing confirms invertebrate bio-mass has rebounded to pre-industrial benchmarks along the two-mile restoration bank.",
            establishedYear = 1982
        ),
        PartnerPublication(
            id = "partner_west_end_review",
            name = "West End Literary Review",
            tagline = "Essays, print typography, poetic dispatches, and local cultural criticism",
            mastheadLead = "Clara Beauchamp, Managing Editor",
            medium = PartnerMediumType.INDEPENDENT_ZINE,
            region = "Old Arts Quarter & University Ridge",
            circulation = "12,800 Print & Zine Circulation",
            frequency = "Monthly Risograph Edition",
            editorialCharter = "Championing handset letterpress craft, avant-garde regional fiction, and philosophical inquiries into contemporary urban life.",
            sampleArticleHeadline = "On the Fragile Geometry of Streetlamp Shadows: A Nocturnal Walking Essay",
            sampleExcerpt = "To walk the cobblestone alleyways past midnight is to witness the city's architectural dialogue between gaslamp nostalgia and electric lucidity.",
            establishedYear = 2004
        ),
        PartnerPublication(
            id = "partner_civic_airwaves",
            name = "Civic Wire FM Radio Syndicate",
            tagline = "Broadcast cooperative airing live town hall debates, oral histories & acoustic folk",
            mastheadLead = "Marcus Sterling, Broadcast Director",
            medium = PartnerMediumType.COMMUNITY_RADIO,
            region = "Broadcast Radius 45 Miles (98.5 MHz FM)",
            circulation = "26,000 Daily Listeners",
            frequency = "24/7 Over-The-Air & SCA Subcarrier",
            editorialCharter = "Free-to-air community transmission dedicated to civic deliberation, live audio theatre, and uncompressed acoustic music.",
            sampleArticleHeadline = "Live Audio Archive: 1964 Old City Market Vendor Oral History Tapes Fully Digitized",
            sampleExcerpt = "Eighty-two magnetic tape reels featuring voices of fishmongers, florists, and cobblers are now permanently preserved in the civic sound archive.",
            establishedYear = 1976
        )
    )
}
