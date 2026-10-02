package com.example.data

/**
 * Converted from src/lib/townsquares.ts
 */
data class Townsquare(
    val name: String,
    val slug: String,
    val description: String,
    val image: String,
    val dataAiHint: String,
    val historicalHandles: List<String>? = null,
    val nativeWordmark: String? = null
)

data class CountryGroup(
    val countryName: String,
    val squares: List<Townsquare>
)

val townsquares = listOf(
    Townsquare("Aethelgard", "aethelgard", "A world of swords, sorcery, and summoned heroes.", "https://placehold.co/400x400.png", "fantasy castle landscape", listOf("isekai-hero-kaito", "demon-lord-valerius", "alchemist-lina"), "Æthelgard"),
    Townsquare("Willingdon", "willingdon", "A quiet English town neighboring a rather revolutionary farm.", "https://placehold.co/400x400.png", "english countryside village", listOf("squealer", "napoleon", "snowball", "mr-jones", "mr-pilkington", "mr-frederick", "mr-whymper")),
    Townsquare("Istanbul", "istanbul", "A bridge between continents, where history meets modernity.", "https://placehold.co/400x400.png", "istanbul cityscape bosphorus", listOf("ahmed-riza-bey", "amelie-dubois", "hafiz-efendi", "zuhtu-pasazade", "vicomte-de-valmont", "hasan-yilmaz", "elif-aydin", "ayse-hanim", "captain-miller", "halide-edib"), "Kasaba Meydanı"),
    Townsquare("Moscow", "moscow", "A city of grand history and political power.", "https://placehold.co/400x400.png", "moscow kremlin", listOf("tsar-nicholas-ii", "vladimir-lenin", "russian-peasant", "pyotr-tchaikovsky"), "Городская площадь"),
    Townsquare("St. Petersburg", "st-petersburg", "Russia's window to Europe, a city of palaces and revolutions.", "https://placehold.co/400x400.png", "st petersburg winter palace", listOf("pierre-bezukhov", "natasha-rostova", "andrei-bolkonsky", "tsar-nicholas-ii", "vladimir-lenin", "russian-peasant"), "Городская площадь"),
    Townsquare("Tokyo", "tokyo", "A dazzling metropolis where tradition and future collide.", "https://placehold.co/400x400.png", "tokyo street crossing", listOf("kenji-tanaka", "yuki-sato", "haruto-ito", "kenji-t-showa", "yumi-s-showa", "haruto-i-showa"), "タウンスクエア"),
    Townsquare("Berlin", "berlin", "A city reborn, bearing the scars and stories of history.", "https://placehold.co/400x400.png", "berlin brandenburg gate", listOf("klaus-richter", "anne-frank", "eleanor-roosevelt", "winston-churchill", "hasan-yilmaz", "elif-aydin", "ingrid-schmidt"), "Stadtplatz"),
    Townsquare("Copenhagen", "copenhagen", "A city of Vikings, sagas, and fairy tales.", "https://placehold.co/400x400.png", "copenhagen harbor houses", listOf("hrothgar", "beowulf", "grendel", "grendels_mom", "unferth"), "Torvet"),
    Townsquare("Silicon Valley", "silicon-valley", "The digital frontier of innovation and technology.", "https://placehold.co/400x400.png", "futuristic cityscape"),
    Townsquare("Kyoto", "kyoto", "A serene space where tradition meets tranquility.", "https://placehold.co/400x400.png", "japanese temple garden", listOf("yuki-sato", "yumi-s-showa", "haruto-i-showa"), "タウンスクエア"),
    Townsquare("Paris", "paris", "The heart of art, culture, and intellectual conversations.", "https://placehold.co/400x400.png", "paris cafe scene", listOf("ahmed-riza-bey", "amelie-dubois", "dario-moreno", "vicomte-de-valmont"), "Place de la Ville"),
    Townsquare("Cairo", "cairo", "A bustling marketplace of ideas, history, and trade.", "https://placehold.co/400x400.png", "egyptian market bazaar", listOf("khaled-am", "fatima-z"), "ميدان المدينة"),
    Townsquare("Rio de Janeiro", "rio-de-janeiro", "A vibrant celebration of music, life, and community.", "https://placehold.co/400x400.png", "carnival festival", listOf("marco-r", "isabela-c"), "Praça da Cidade"),
    Townsquare("Ankara", "ankara", "The heart of a republic, a city of history and governance.", "https://placehold.co/400x400.png", "ankara cityscape mausoleum", listOf("mustafa-kemal", "ali-usta", "asker-mehmet", "zeynep-hanim", "gazeteci-orhan", "bulent-ecevit"), "Kasaba Meydanı"),
    Townsquare("İzmir", "izmir", "Pearl of the Aegean, a city of sun, sea, and history.", "https://placehold.co/400x400.png", "izmir clock tower", listOf("eleni-pappas", "gen-hacianestis", "dario-moreno"), "Kasaba Meydanı"),
    Townsquare("Çanakkale", "canakkale", "Guardian of the Dardanelles, a city of legends and sacrifice.", "https://placehold.co/400x400.png", "gallipoli peninsula", listOf("onbasi-halil"), "Kasaba Meydanı"),
    Townsquare("Louisiana", "louisiana", "A melting pot of cultures, music, and cuisine.", "https://placehold.co/400x400.png", "new orleans street", listOf("lou-devereaux")),
    Townsquare("Austin", "austin", "The live music capital of the world.", "https://placehold.co/400x400.png", "live music concert", listOf("clyde-b", "jed-jones", "ethnomusicologist-finch")),
    Townsquare("Nashville", "nashville", "The heart of country music.", "https://placehold.co/400x400.png", "country music guitar", listOf("helen-parker", "patty-oconnor", "fiddlin-jeb", "ethnomusicologist-finch")),
    Townsquare("Broadway", "broadway", "The grand stage of American theater.", "https://placehold.co/400x400.png", "theater stage curtains", listOf("leo-rothman")),
    Townsquare("Hollywood", "hollywood", "The dream factory of the film industry.", "https://placehold.co/400x400.png", "hollywood sign", listOf("eleanor-may")),
    Townsquare("Rome", "rome", "The eternal city of history and empire.", "https://placehold.co/400x400.png", "roman colosseum", listOf("giacomo-matteotti", "antonio-gramsci", "niccolo-paganini", "padre-lorenzo"), "Piazza della Città"),
    Townsquare("Cremona", "cremona", "A city of music, violins, and rising political fervor.", "https://placehold.co/400x400.png", "italian city square", listOf("roberto-farinacci", "antonio-stradivari", "niccolo-paganini"), "Piazza del Comune"),
    Townsquare("Soho", "soho", "A vibrant hub of fashion, art, and nightlife.", "https://placehold.co/400x400.png", "soho street london"),
    Townsquare("New Delhi", "new-delhi", "A sprawling city of ancient history and modern dynamism.", "https://placehold.co/400x400.png", "india gate monument", listOf("asha-s", "vikram-s"), "टाउन स्क्वायर"),
    Townsquare("New York City", "new-york-city", "The city that never sleeps, a global financial and cultural center.", "https://placehold.co/400x400.png", "new york skyline", listOf("frank-connolly")),
    Townsquare("Washington DC", "washington-dc", "The political heart of the United States.", "https://placehold.co/400x400.png", "us capitol building", listOf("walter-cronkite")),
    Townsquare("Vienna", "vienna", "A city of imperial palaces, classical music, and coffee houses.", "https://placehold.co/400x400.png", "vienna opera house", nativeWordmark = "Stadtplatz"),
    Townsquare("Zurich", "zurich", "A global center for banking and finance, nestled by a pristine lake.", "https://placehold.co/400x400.png", "zurich city lake", listOf("tim-b", "nicola-p", "jurgen-s"), "Stadtplatz"),
    Townsquare("London", "london", "A historic capital grappling with the dawn of a new media age.", "https://placehold.co/400x400.png", "london street vintage", listOf("arthur-p", "eleanor-v", "sid-cooper")),
    Townsquare("Nicosia/Lefkoşa", "nicosia", "The world's last divided capital, a city of two stories.", "https://placehold.co/400x400.png", "nicosia city street", listOf("mehmet-aydin", "sgt-price-uk"), "Kasaba Meydanı"),
    Townsquare("Famagusta/Mağusa", "famagusta", "A port city of deep history and haunting beauty.", "https://placehold.co/400x400.png", "famagusta ghost town", listOf("eleni-georgiou"), "Kasaba Meydanı"),
    Townsquare("Hatay", "hatay", "A land of ancient heritage and a pivotal moment in modern history.", "https://placehold.co/400x400.png", "hatay city view", listOf("tayfur-sokmen", "adile-halide", "jean-gauthier", "asker-mehmet", "gazeteci-orhan"), "Kasaba Meydanı"),
    Townsquare("Salzburg", "salzburg", "The birthplace of Mozart and a stage for the world's music.", "https://placehold.co/400x400.png", "salzburg austria skyline", listOf("w-a-mozart"), "Stadtplatz"),
    Townsquare("Bonn", "bonn", "A historic German city on the Rhine, birthplace of Beethoven.", "https://placehold.co/400x400.png", "bonn germany rhine river", listOf("ludwig-v-beethoven"), "Stadtplatz"),
    Townsquare("La Mancha", "la-mancha", "A sun-drenched plain of windmills and chivalrous dreams.", "https://placehold.co/400x400.png", "spain windmills landscape", listOf("don-quixote-mancha", "sancho-panza"), "Plaza del Pueblo"),
    Townsquare("Sierra Morena", "sierra-morena", "A rugged mountain range, a refuge for penitents and madmen.", "https://placehold.co/400x400.png", "spain mountain range", listOf("don-quixote-mancha", "sancho-panza"), "Plaza del Pueblo"),
    Townsquare("El Toboso", "el-toboso", "A humble village, home to the peerless lady of a knight's heart.", "https://placehold.co/400x400.png", "spanish village whitewashed", listOf("don-quixote-mancha", "sancho-panza"), "Plaza del Pueblo"),
    Townsquare("Barcelona", "barcelona", "A vibrant coastal city of printing presses and enchanted galleys.", "https://placehold.co/400x400.png", "barcelona gothic quarter", listOf("don-quixote-mancha", "sancho-panza"), "Plaça de la Vila")
)

val townsquaresByCountry = listOf(
    CountryGroup("Türkiye", townsquares.filter { it.slug in listOf("istanbul", "ankara", "izmir", "canakkale", "hatay") }),
    CountryGroup("KKTC / Northern Cyprus", townsquares.filter { it.slug in listOf("nicosia", "famagusta") }),
    CountryGroup("United States", townsquares.filter { it.slug in listOf("new-york-city", "washington-dc", "hollywood", "broadway", "louisiana", "austin", "nashville", "silicon-valley") }),
    CountryGroup("United Kingdom", townsquares.filter { it.slug in listOf("soho", "london", "willingdon") }),
    CountryGroup("Germany", townsquares.filter { it.slug in listOf("berlin", "bonn") }),
    CountryGroup("Russia", townsquares.filter { it.slug in listOf("moscow", "st-petersburg") }),
    CountryGroup("Japan", townsquares.filter { it.slug in listOf("tokyo", "kyoto") }),
    CountryGroup("Denmark", townsquares.filter { it.slug in listOf("copenhagen") }),
    CountryGroup("France", townsquares.filter { it.slug in listOf("paris") }),
    CountryGroup("Switzerland", townsquares.filter { it.slug in listOf("zurich") }),
    CountryGroup("Egypt", townsquares.filter { it.slug in listOf("cairo") }),
    CountryGroup("Brazil", townsquares.filter { it.slug in listOf("rio-de-janeiro") }),
    CountryGroup("Italy", townsquares.filter { it.slug in listOf("rome", "cremona") }),
    CountryGroup("India", townsquares.filter { it.slug in listOf("new-delhi") }),
    CountryGroup("Austria", townsquares.filter { it.slug in listOf("vienna", "salzburg") }),
    CountryGroup("Spain (Fictional)", townsquares.filter { it.slug in listOf("la-mancha", "sierra-morena", "el-toboso", "barcelona") }),
    CountryGroup("Fictional", townsquares.filter { it.slug in listOf("aethelgard", "willingdon", "la-mancha", "sierra-morena", "el-toboso", "barcelona") })
)
