package com.example.data

/**
 * Converted from src/lib/epaper.ts
 */
data class Edition(
    val id: String,
    val date: String,
    val coverImage: String
)

data class Publication(
    val id: String,
    val name: String,
    val slug: String,
    val logoText: String,
    val editions: List<Edition>
)

val publications = listOf(
    Publication(
        id = "pub1",
        name = "Townsquare Daily",
        slug = "townsquare-daily",
        logoText = "TOWNSQUARE+DAILY",
        editions = listOf(
            Edition("ed1-1", "Saturday, August 16, 2025", "https://placehold.co/800x1067.png?text=Townsquare+Daily\\nCover&font=roboto"),
            Edition("ed1-2", "Friday, August 15, 2025", "https://placehold.co/800x1067.png?text=Townsquare+Daily\\nCover&font=roboto"),
            Edition("ed1-3", "Thursday, August 14, 2025", "https://placehold.co/800x1067.png?text=Townsquare+Daily\\nCover&font=roboto"),
            Edition("ed1-4", "Wednesday, August 13, 2025", "https://placehold.co/800x1067.png?text=Townsquare+Daily\\nCover&font=roboto"),
            Edition("ed1-5", "Tuesday, August 12, 2025", "https://placehold.co/800x1067.png?text=Townsquare+Daily\\nCover&font=roboto")
        )
    ),
    Publication(
        id = "pub2",
        name = "Townsquare Weekly",
        slug = "townsquare-weekly",
        logoText = "TOWNSQUARE+WEEKLY",
        editions = listOf(
            Edition("ed2-1", "Sunday, August 17, 2025", "https://placehold.co/800x1067.png?text=Townsquare+Weekly\\nCover&font=roboto"),
            Edition("ed2-2", "Sunday, August 10, 2025", "https://placehold.co/800x1067.png?text=Townsquare+Weekly\\nCover&font=roboto")
        )
    ),
    Publication(
        id = "pub6",
        name = "Supermarket Weekly",
        slug = "supermarket-flyer",
        logoText = "SUPERMARKET+WEEKLY",
        editions = listOf(
            Edition("ed6-1", "Week of August 14, 2025", "https://placehold.co/800x1067.png?text=Supermarket+Weekly\\nFlyer&font=roboto")
        )
    ),
    Publication(
        id = "pub4",
        name = "The Business Beat",
        slug = "the-business-beat",
        logoText = "BUSINESS+BEAT",
        editions = listOf(
            Edition("ed4-1", "August 15, 2025", "https://placehold.co/800x1067.png?text=Business+Beat\\nCover&font=playfair"),
            Edition("ed4-2", "August 8, 2025", "https://placehold.co/800x1067.png?text=Business+Beat\\nCover&font=playfair")
        )
    ),
    Publication(
        id = "pub5",
        name = "The Urbanist",
        slug = "the-urbanist",
        logoText = "THE+URBANIST",
        editions = listOf(
            Edition("ed5-1", "Summer 2025", "https://placehold.co/800x1067.png?text=The+Urbanist\\nCover&font=playfair"),
            Edition("ed5-2", "Spring 2025", "https://placehold.co/800x1067.png?text=The+Urbanist\\nCover&font=playfair")
        )
    ),
    Publication(
        id = "pub3",
        name = "Magazines",
        slug = "magazines",
        logoText = "TOWNSQUARE+MAGAZINES",
        editions = listOf(
            Edition("ed3-1", "August 2025", "https://placehold.co/800x1067.png?text=Fashion+Mag\\nCover&font=playfair"),
            Edition("ed3-2", "August 2025", "https://placehold.co/800x1067.png?text=Tech+Weekly\\nCover&font=roboto"),
            Edition("ed3-3", "July 2025", "https://placehold.co/800x1067.png?text=Travel+Monthly\\nCover&font=playfair")
        )
    )
)
