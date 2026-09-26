package com.example.ui.sanctuary

import com.example.language.model.VerificationStatus

/**
 * Diurnal Time Cycle for atmospheric lighting.
 */
enum class DiurnalTimeOfDay(val title: String, val description: String) {
    MORNING_DAWN("Morning Dawn", "Soft morning light over papyrus reeds."),
    MIDDAY_SUN("Midday Sun", "Warm sunlight filtering through baobab leaves."),
    EVENING_GOLD("Evening Gold", "Golden sky and gentle hearth glow."),
    TWILIGHT_DUSK("Twilight Dusk", "Calm evening dusk along the shore.");

    fun next(): DiurnalTimeOfDay = when (this) {
        MORNING_DAWN -> MIDDAY_SUN
        MIDDAY_SUN -> EVENING_GOLD
        EVENING_GOLD -> TWILIGHT_DUSK
        TWILIGHT_DUSK -> MORNING_DAWN
    }
}

/**
 * Sanctuary Biome mapped to Room database state.
 */
enum class SanctuaryBiome(
    val id: String,
    val title: String,
    val description: String,
    val ambientAudioCue: String
) {
    RIVER_WETLANDS(
        id = "WETLANDS",
        title = "River & Canoe Landing",
        description = "River shore, papyrus reeds & dugout canoes.",
        ambientAudioCue = "audio_ambient_river_water"
    ),
    VILLAGE_LANDING(
        id = "VILLAGE",
        title = "Village Hearth & Home",
        description = "Clay homesteads, hearth fire & family stories.",
        ambientAudioCue = "audio_ambient_village_hearth"
    ),
    HILLS_AND_MEADOWS(
        id = "MEADOWS",
        title = "Wetland Marsh & Wildlife",
        description = "Reeds, crested cranes, hippos & river birds.",
        ambientAudioCue = "audio_ambient_meadow_wind"
    ),
    BAOBAB_FOREST(
        id = "FOREST",
        title = "Baobab Forest & Nature",
        description = "Ancient baobab canopy, wild fruits & soil.",
        ambientAudioCue = "audio_ambient_forest_birds"
    )
}

enum class StorybookThemeStyle {
    RIVERBANK_DAWN,
    VILLAGE_HEARTH,
    WETLAND_MARSH,
    BAOBAB_CANOPY,
    LAKESIDE_COUNTING,
    LAKESIDE_COLOURS,
    FISHERMAN_SONG
}

/**
 * Storybook Spread representing a single full-screen illustrated learning page.
 */
data class StorybookSpread(
    val bookId: String,
    val pageIndex: Int,
    val pageNumberInBook: Int,
    val totalPagesInBook: Int,
    val bookTitleLukenye: String,
    val bookTitleEnglish: String,
    val bookIconEmoji: String,
    val titleLukenye: String,
    val titleEnglish: String,
    val conceptDescription: String,
    val biome: SanctuaryBiome,
    val themeStyle: StorybookThemeStyle,
    val interactiveObjects: List<StorybookObject>,
    val narrativeLukenye: String = "",
    val narrativeEnglish: String = "",
    val proverbLukenye: String = "",
    val proverbEnglish: String = "",
    val culturalFact: String = ""
)

/**
 * Storybook representing one of the 18 Launch Books.
 */
data class Storybook(
    val id: String,
    val titleLukenye: String,
    val titleEnglish: String,
    val iconEmoji: String,
    val shortDescription: String,
    val spreads: List<StorybookSpread>
)

/**
 * Interactive object placed on a storybook page layout.
 */
data class StorybookObject(
    val id: String,
    val labelLukenye: String,
    val labelEnglish: String,
    val pronunciation: String,
    val illustrationEmoji: String,
    val xPercent: Float,
    val yPercent: Float,
    val audioAssetPath: String = "bakenye_sample",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED
)

/**
 * Builds the complete collection of 18 Launch Picture Books with full multi-page story spreads.
 */
fun getLaunchStorybooks(): List<Storybook> {
    var globalPageIndex = 0
    val booksList = mutableListOf<Storybook>()

    // ==========================================
    // 1. GREETINGS (Okulamusa) - 2 Spreads
    // ==========================================
    val b1Spreads = listOf(
        StorybookSpread(
            bookId = "greetings",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Okulamusa",
            bookTitleEnglish = "Greetings",
            bookIconEmoji = "👋🏽",
            titleLukenye = "Okulamusa ku Nnyanja",
            titleEnglish = "Morning Greetings on the Shore",
            conceptDescription = "Warm lakeside greetings and welcoming words as dawn breaks.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.RIVERBANK_DAWN,
            narrativeLukenye = "Enjuba evaayo ku nnyanja Kyoga, abantu babuuzaganya n'essanyu.",
            narrativeEnglish = "The sun rises over Lake Kyoga, and the people greet one another with joy.",
            proverbLukenye = "Okulamusa bwe bulamu.",
            proverbEnglish = "To greet another is to honor their life.",
            culturalFact = "Bakenyi elders teach children that every traveler met by the water receives a warm greeting.",
            interactiveObjects = listOf(
                StorybookObject("GREET_WASUZE_OTYA", "Wasuze otya?", "Good morning!", "Wah-soo-zeh oh-tyah?", "☀️", 0.16f, 0.28f),
                StorybookObject("GREET_OLI_OTYA", "Oli otya!", "Hello! / How are you!", "Oh-lee oh-tyah!", "🦦", 0.48f, 0.24f),
                StorybookObject("GREET_GYEBALAKO", "Gyebaleko!", "Well done on your work!", "Jyeh-bah-leh-koh!", "🛶", 0.76f, 0.28f),
                StorybookObject("GREET_BULUNGI", "Bulungi!", "I am well / All is peaceful!", "Boo-loon-jee!", "🕊️", 0.20f, 0.62f),
                StorybookObject("GREET_WEEBALE", "Weebale!", "Thank you!", "Weh-eh-bah-leh!", "🤝", 0.74f, 0.62f)
            )
        ),
        StorybookSpread(
            bookId = "greetings",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Okulamusa",
            bookTitleEnglish = "Greetings",
            bookIconEmoji = "👋🏽",
            titleLukenye = "Okusiibula n'Ekiro",
            titleEnglish = "Afternoon & Evening Farewells",
            conceptDescription = "Gentle afternoon check-ins and peaceful evening farewells.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            narrativeLukenye = "Akawungeezi katuuse, twebaza ab'enju era tusiibula n'emirembe.",
            narrativeEnglish = "As dusk arrives, we thank our family and say gentle goodbyes in peace.",
            proverbLukenye = "Akanyonyi akatono keebaza n'oluyimba.",
            proverbEnglish = "Even the smallest bird gives thanks with a song.",
            culturalFact = "In the evening, neighbors sitting around the hearth exchange 'Siiba bulungi' and 'Sula bulungi'.",
            interactiveObjects = listOf(
                StorybookObject("GREET_OSIIBYE_OTYA", "Osiibye otya?", "Good afternoon!", "Oh-see-byeh oh-tyah?", "🌅", 0.18f, 0.28f),
                StorybookObject("GREET_KAALE", "Kaale!", "You are welcome / Alright!", "Kah-ah-leh!", "🌿", 0.74f, 0.28f),
                StorybookObject("GREET_SIIBA_BULUNGI", "Siiba bulungi!", "Have a pleasant day!", "See-bah boo-loon-jee!", "🌾", 0.20f, 0.64f),
                StorybookObject("GREET_SULA_BULUNGI", "Sula bulungi!", "Sleep peacefully / Goodnight!", "Soo-lah boo-loon-jee!", "🌙", 0.75f, 0.64f),
                StorybookObject("GREET_TULAGABANA", "Tulagabana!", "See you soon!", "Too-lah-gah-bah-nah!", "✨", 0.48f, 0.46f)
            )
        )
    )
    booksList.add(Storybook("greetings", "Okulamusa", "Greetings", "👋🏽", "Warm lakeside welcomes & polite speech", b1Spreads))

    // ==========================================
    // 2. FAMILY (Amaaka) - 2 Spreads
    // ==========================================
    val b2Spreads = listOf(
        StorybookSpread(
            bookId = "family",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Amaaka",
            bookTitleEnglish = "Family",
            bookIconEmoji = "👨‍👩‍👧‍👦",
            titleLukenye = "Amaaka n'Ab'enju",
            titleEnglish = "Our Loving Family",
            conceptDescription = "Caring parents, infants and growing children.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            narrativeLukenye = "Abazadde bakuza abaana n'okwagala awamu mu nju y'eka.",
            narrativeEnglish = "Parents raise their children with endless love together in the family home.",
            proverbLukenye = "Omwana takula mu maka gamu ga bweru.",
            proverbEnglish = "It takes a whole loving clan to nurture a child.",
            culturalFact = "In Bakenyi culture, a child calls both maternal and paternal aunts 'Maama' as a sign of collective care.",
            interactiveObjects = listOf(
                StorybookObject("FAM_MAAMA", "Maama", "Mother", "Mah-ah-mah", "👩🏽", 0.18f, 0.30f),
                StorybookObject("FAM_TAATA", "Taata", "Father", "Tah-ah-tah", "👨🏽", 0.48f, 0.22f),
                StorybookObject("FAM_OMWANA", "Omwana", "Child", "Oh-mwah-nah", "🧒🏽", 0.76f, 0.30f),
                StorybookObject("FAM_MUTO", "Muto wange", "Baby / Little Sibling", "Moo-toh wahn-geh", "👶🏽", 0.20f, 0.64f),
                StorybookObject("FAM_MUKULU", "Mukulu wange", "Elder Brother/Sister", "Moo-koo-loo wahn-geh", "👦🏽", 0.76f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "family",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Amaaka",
            bookTitleEnglish = "Family",
            bookIconEmoji = "👨‍👩‍👧‍👦",
            titleLukenye = "Bajjajja n'Abaganda",
            titleEnglish = "Grandparents & Clan Kin",
            conceptDescription = "Honored grandparents, uncles, aunts and clan cousins.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            narrativeLukenye = "Bajjajja batuula n'abaana wansi w'omuti ne babasomera enfumo.",
            narrativeEnglish = "Grandparents sit with the children under the shade tree sharing wisdom tales.",
            proverbLukenye = "Amagezi gali mu bakulu.",
            proverbEnglish = "Wisdom flows deepest from the elders.",
            culturalFact = "JjaJja tells children bedtime folktales by the flickering fire to preserve clan lineage.",
            interactiveObjects = listOf(
                StorybookObject("FAM_JJAJJA_OMUKAZI", "JjaJja Omukazi", "Grandmother", "Jjah-Jjah Oh-moo-kah-zee", "👵🏽", 0.18f, 0.30f),
                StorybookObject("FAM_JJAJJA_OMUSAJJA", "JjaJja Omusajja", "Grandfather", "Jjah-Jjah Oh-moo-sahj-jah", "👴🏽", 0.76f, 0.30f),
                StorybookObject("FAM_SSENGA", "Ssenga", "Paternal Aunt", "Ssen-gah", "🧕🏽", 0.20f, 0.64f),
                StorybookObject("FAM_KOOJA", "Kooja", "Maternal Uncle", "Koh-oh-jah", "🧔🏽", 0.76f, 0.64f),
                StorybookObject("FAM_KIZIBWE", "Kizibwe", "Cousin / Playmate", "Kee-zeeb-weh", "👧🏽", 0.48f, 0.48f)
            )
        )
    )
    booksList.add(Storybook("family", "Amaaka", "Family", "👨‍👩‍👧‍👦", "Parents, grandparents & loving siblings", b2Spreads))

    // ==========================================
    // 3. HOME (Amaka n'Enju) - 2 Spreads
    // ==========================================
    val b3Spreads = listOf(
        StorybookSpread(
            bookId = "home",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Amaka n'Enju",
            bookTitleEnglish = "Home & Hearth",
            bookIconEmoji = "🏡",
            titleLukenye = "Omuliro n'Enju y'Eka",
            titleEnglish = "Inside the Family Homestead",
            conceptDescription = "Clay home, woven mats and the warm hearth.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            narrativeLukenye = "Enju y'ettaka eyiwe bulungi era ekuuma ebbugumu liri munda.",
            narrativeEnglish = "The clay homestead is smoothly plastered and keeps everyone warm and safe.",
            proverbLukenye = "Eka kitiibwa ky'omuntu.",
            proverbEnglish = "Home is a person's dignity and foundation.",
            culturalFact = "Homestead walls are crafted from sun-dried termite clay and thatched with dried elephant grass.",
            interactiveObjects = listOf(
                StorybookObject("HOME_ENJU", "Enju", "Clay Homestead", "En-joo", "🏡", 0.20f, 0.28f),
                StorybookObject("HOME_EKYOTO", "Ekyoto", "Fireside Hearth", "Eh-kyoh-toh", "🔥", 0.74f, 0.28f),
                StorybookObject("HOME_OMUKEEKA", "Omukeeka", "Woven Grass Mat", "Oh-moo-keh-eh-kah", "🧺", 0.46f, 0.48f),
                StorybookObject("HOME_ENSIIMU", "Ensiimu", "Clay Cooking Pot", "En-see-moo", "🍲", 0.20f, 0.66f),
                StorybookObject("HOME_EKITA", "Ekita", "Water Gourd", "Eh-kee-tah", "🍶", 0.74f, 0.66f)
            )
        ),
        StorybookSpread(
            bookId = "home",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Amaka n'Enju",
            bookTitleEnglish = "Home & Hearth",
            bookIconEmoji = "🏡",
            titleLukenye = "Olugya n'Ebintu by'Eka",
            titleEnglish = "The Courtyard & Handcrafted Tools",
            conceptDescription = "Clean-swept courtyard, wooden stools, grain stores and pestles.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            narrativeLukenye = "Olugya lwayeerwa bulungi n'olweyo, n'ebintu byonna bitegekeddwa.",
            narrativeEnglish = "The courtyard is swept clean with reeds, and tools are gathered neatly.",
            proverbLukenye = "Obuyonjo buzaala obulamu.",
            proverbEnglish = "Cleanliness brings vibrant health.",
            culturalFact = "Children take pride in sweeping the compound with handmade broom reeds every morning.",
            interactiveObjects = listOf(
                StorybookObject("HOME_OLWEYO", "Olweyo", "Grass Broom", "Oh-lweh-yoh", "🧹", 0.20f, 0.28f),
                StorybookObject("HOME_ENTEBE", "Entebe y'Omuti", "Carved Stool", "En-teh-beh", "🪑", 0.74f, 0.28f),
                StorybookObject("HOME_EKYEKYO", "Eseggwanga", "Granary Store", "Eh-segg-wahn-gah", "🛖", 0.48f, 0.46f),
                StorybookObject("HOME_OMUSEKUZZO", "Omusekuzzo", "Wooden Pestle", "Oh-moo-seh-koo-zzoh", "🪵", 0.20f, 0.64f),
                StorybookObject("HOME_EKISEKUZZE", "Ekinu", "Wooden Mortar", "Eh-kee-noo", "🥣", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("home", "Amaka n'Enju", "Home & Hearth", "🏡", "Warm homestead, mats & cooking pots", b3Spreads))

    // ==========================================
    // 4. VILLAGE (Ekyalo) - 2 Spreads
    // ==========================================
    val b4Spreads = listOf(
        StorybookSpread(
            bookId = "village",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ekyalo kya Bakenyi",
            bookTitleEnglish = "The Village",
            bookIconEmoji = "🏘️",
            titleLukenye = "Obulamu mu Kyalo",
            titleEnglish = "Life in the Lakeside Village",
            conceptDescription = "Meeting drums, footpaths, leaders and community grounds.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            narrativeLukenye = "Engoma evuga, abantu b'omu kyalo basisinkana mu mirembe.",
            narrativeEnglish = "The village drum beats, calling all neighbors together in harmony.",
            proverbLukenye = "Agali awamu ge galuma ennyama.",
            proverbEnglish = "Teeth that work together can chew any meat — in unity is strength.",
            culturalFact = "Village drums have unique rhythmic patterns for weddings, fish harvests, and council meetings.",
            interactiveObjects = listOf(
                StorybookObject("VIL_EKYALO", "Ekyalo", "Lakeside Village", "Eh-kyah-loh", "🏘️", 0.22f, 0.26f),
                StorybookObject("VIL_ENGOMA", "Engoma", "Village Drum", "En-goh-mah", "🪘", 0.74f, 0.26f),
                StorybookObject("VIL_OLUGUUDO", "Oluguudo", "Footpath", "Oh-loo-goo-doh", "🛤️", 0.20f, 0.62f),
                StorybookObject("VIL_OMUKUNGU", "Omukungu", "Village Leader", "Oh-moo-koon-goo", "👑", 0.48f, 0.46f),
                StorybookObject("VIL_EKISAANYA", "Ekisaanya", "Meeting Clearing", "Eh-kee-sah-ahn-yah", "🏕️", 0.75f, 0.62f)
            )
        ),
        StorybookSpread(
            bookId = "village",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ekyalo kya Bakenyi",
            bookTitleEnglish = "The Village",
            bookIconEmoji = "🏘️",
            titleLukenye = "Akatale k'Obakenyi",
            titleEnglish = "The Lakeside Floating Market",
            conceptDescription = "Trading fresh tilapia, baskets, sweet fruit and pottery by the docks.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.RIVERBANK_DAWN,
            narrativeLukenye = "Ku myalo, abavubi bagaba ensomba ate abalala ne baluka ebibbo.",
            narrativeEnglish = "At the docks, fishermen trade fresh catch while weavers craft colorful baskets.",
            proverbLukenye = "Ekitone ky'omukono kigabira obulamu.",
            proverbEnglish = "The skill of your hands feeds the household.",
            culturalFact = "Water markets have taken place along Lake Kyoga's canoe channels for hundreds of years.",
            interactiveObjects = listOf(
                StorybookObject("VIL_AKATALE", "Akatale", "Lakeside Market", "Ah-kah-tah-leh", "🧺", 0.20f, 0.28f),
                StorybookObject("VIL_EKIBBO", "Ekibbo", "Woven Reed Basket", "Eh-kee-bboh", "🧺", 0.74f, 0.28f),
                StorybookObject("VIL_OMWALO", "Omwalo", "Canoe Landing Dock", "Oh-mwah-loh", "🛶", 0.48f, 0.46f),
                StorybookObject("VIL_ENSIMBI", "Ensimbi z'Ekitalo", "Trade Beads / Coins", "En-seem-bee", "🪙", 0.20f, 0.64f),
                StorybookObject("VIL_OMUTUNZI", "Omutunzi", "Friendly Trader", "Oh-moo-toon-zee", "🧑🏽", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("village", "Ekyalo kya Bakenyi", "The Village", "🏘️", "Paths, gathering drums & friendly markets", b4Spreads))

    // ==========================================
    // 5. ANIMALS (Ebisolo) - 2 Spreads
    // ==========================================
    val b5Spreads = listOf(
        StorybookSpread(
            bookId = "animals",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ebisolo",
            bookTitleEnglish = "Animals",
            bookIconEmoji = "🦛",
            titleLukenye = "Ebisolo by'Amazzi",
            titleEnglish = "Water & Marsh Wildlife",
            conceptDescription = "Hippos, friendly otters, monitor lizards and bullfrogs.",
            biome = SanctuaryBiome.HILLS_AND_MEADOWS,
            themeStyle = StorybookThemeStyle.WETLAND_MARSH,
            narrativeLukenye = "Envyubu ewugira wansi w'amazzi, ate Kato ogonze asambala n'essanyu.",
            narrativeEnglish = "The gentle hippo swims underwater, while Kato the otter paddles happily.",
            proverbLukenye = "Amazzi amengi gamala ennyonta y'ensolo zonna.",
            proverbEnglish = "The great lake provides cool water for all living beings.",
            culturalFact = "The clawless river otter is revered by Bakenyi children as the clever guardian of the reeds.",
            interactiveObjects = listOf(
                StorybookObject("ANI_ENVYUBU", "Envyubu", "Hippopotamus", "En-vyoo-boo", "🦛", 0.20f, 0.28f),
                StorybookObject("ANI_OGONZE", "Ogonze", "River Otter (Kato)", "Oh-gohn-zeh", "🦦", 0.74f, 0.24f),
                StorybookObject("ANI_OMUBULI", "Omubuli", "Monitor Lizard", "Oh-moo-boo-lee", "🦎", 0.48f, 0.42f),
                StorybookObject("ANI_EKIKERE", "Ekikere", "Water Frog", "Eh-kee-keh-reh", "🐸", 0.20f, 0.64f),
                StorybookObject("ANI_ENKWALE", "Enkwale", "Marsh Quail", "En-kwah-leh", "🦃", 0.74f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "animals",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ebisolo",
            bookTitleEnglish = "Animals",
            bookIconEmoji = "🦛",
            titleLukenye = "Ebisolo by'Ettale",
            titleEnglish = "Meadow Grazers & Forest Creatures",
            conceptDescription = "Cattle, playful vervet monkeys, goats and shy bushbucks.",
            biome = SanctuaryBiome.HILLS_AND_MEADOWS,
            themeStyle = StorybookThemeStyle.WETLAND_MARSH,
            narrativeLukenye = "Ente zirya omuddo omulungi mu lusaalu lwa Kyoga.",
            narrativeEnglish = "The horned cattle graze on tender sweet grass across the Kyoga plains.",
            proverbLukenye = "Ente eyagala omwalo ye esooka.",
            proverbEnglish = "The thirsty cow reaches the sweet water first.",
            culturalFact = "Cattle bells carved from wood ring musically across the morning meadows.",
            interactiveObjects = listOf(
                StorybookObject("ANI_ENTE", "Ente", "Long-Horned Cow", "En-teh", "🐄", 0.18f, 0.28f),
                StorybookObject("ANI_NKIMA", "Nkima", "Vervet Monkey", "N-kee-mah", "🐒", 0.74f, 0.28f),
                StorybookObject("ANI_EMBUZI", "Embuzi", "Spotted Goat", "Em-boo-zee", "🐐", 0.48f, 0.46f),
                StorybookObject("ANI_ENGABO", "Engabi", "Bushbuck Antelope", "En-gah-bee", "🦌", 0.20f, 0.64f),
                StorybookObject("ANI_EMBWA", "Embwa", "Homestead Dog", "Em-bwah", "🐕", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("animals", "Ebisolo", "Animals", "🦛", "Hippos, otters, cows & wetland wildlife", b5Spreads))

    // ==========================================
    // 6. BIRDS (Enyonyi) - 2 Spreads
    // ==========================================
    val b6Spreads = listOf(
        StorybookSpread(
            bookId = "birds",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Enyonyi",
            bookTitleEnglish = "Birds",
            bookIconEmoji = "🦩",
            titleLukenye = "Enyonyi z'Ennyanja",
            titleEnglish = "Waterbirds of Lake Kyoga",
            conceptDescription = "Crested cranes, kingfishers and fish eagles.",
            biome = SanctuaryBiome.HILLS_AND_MEADOWS,
            themeStyle = StorybookThemeStyle.WETLAND_MARSH,
            narrativeLukenye = "Engwali ebuuka mu bwengula n'ebinaala n'essanyu eringi.",
            narrativeEnglish = "The crowned crane soars above the blue water, dancing with grace.",
            proverbLukenye = "Engwali tesiiba ku lusozi lw'enjala.",
            proverbEnglish = "The crane nests only where peace and abundance reign.",
            culturalFact = "The Grey Crowned Crane is protected with reverence by the Bakenyi people.",
            interactiveObjects = listOf(
                StorybookObject("BRD_ENGWALI", "Engwali", "Crested Crane", "En-gwah-lee", "🦩", 0.20f, 0.28f),
                StorybookObject("BRD_NALUBA", "Naluba", "Pied Kingfisher", "Nah-loo-bah", "🐦", 0.76f, 0.25f),
                StorybookObject("BRD_EMPUNGU", "Empungu", "Fish Eagle", "Em-poon-goo", "🦅", 0.48f, 0.18f),
                StorybookObject("BRD_ENYANGE", "Enyange", "White Egret", "Eh-nyahn-geh", "🦢", 0.20f, 0.64f),
                StorybookObject("BRD_ENKOBA", "Enkoba", "Cormorant", "En-koh-bah", "🦆", 0.75f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "birds",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Enyonyi",
            bookTitleEnglish = "Birds",
            bookIconEmoji = "🦩",
            titleLukenye = "Enyonyi z'Emiti n'Ettale",
            titleEnglish = "Songbirds & Tree Dwellers",
            conceptDescription = "Golden weaver birds, sunbirds, owls and turtle doves.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            narrativeLukenye = "Enyonyi ziyimba emyoyo emirungi n'ekisu kye bazimba mu luseke.",
            narrativeEnglish = "Weaver birds sing sweet melodies and weave round nests in the reeds.",
            proverbLukenye = "Akanyonyi akalungi kakwata abantu amatwi.",
            proverbEnglish = "A sweet-voiced bird draws the listening ears of all.",
            culturalFact = "Weaver nests hanging over riverbanks indicate strong, healthy reed beds.",
            interactiveObjects = listOf(
                StorybookObject("BRD_ENKOBE", "Enkobe", "Weaver Bird", "En-koh-beh", "🪺", 0.20f, 0.28f),
                StorybookObject("BRD_KASOOKO", "Kasooko", "Sunbird", "Kah-soh-oh-koh", "🦜", 0.76f, 0.28f),
                StorybookObject("BRD_EKIYIKI", "Ekiyiki", "Night Owl", "Eh-kee-yee-kee", "🦉", 0.48f, 0.46f),
                StorybookObject("BRD_ENJIBA", "Enjiba", "Woodland Dove", "En-jee-bah", "🕊️", 0.20f, 0.64f),
                StorybookObject("BRD_EBIBAAWO", "Ebibaawo", "Swallow", "Eh-bee-bah-ah-woh", "🦇", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("birds", "Enyonyi", "Birds", "🦩", "Crested cranes, eagles & singing weaver birds", b6Spreads))

    // ==========================================
    // 7. FISH & CANOES (Ensomba) - 2 Spreads
    // ==========================================
    val b7Spreads = listOf(
        StorybookSpread(
            bookId = "fish",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ensomba",
            bookTitleEnglish = "Fish & River",
            bookIconEmoji = "🐟",
            titleLukenye = "Ensomba z'Ennyanja",
            titleEnglish = "Fishes of Lake Kyoga",
            conceptDescription = "Tilapia, Nile perch, silver cyprinid and catfish.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.RIVERBANK_DAWN,
            narrativeLukenye = "Ngege n'Emputa ziwugira mu mazzi amayonjo ag'ennyanja.",
            narrativeEnglish = "Tilapia and Nile perch glide gracefully through the fresh, clear water.",
            proverbLukenye = "Ensomba tekwatibwa na matama magonvu.",
            proverbEnglish = "Patience and steady hands catch the finest fish.",
            culturalFact = "Bakenyi fishermen catch only mature fish, returning fingerlings to protect lake balance.",
            interactiveObjects = listOf(
                StorybookObject("FSH_NGEGE", "Ngege", "Tilapia Fish", "N-geh-geh", "🐟", 0.20f, 0.28f),
                StorybookObject("FSH_MPUTA", "Mputa", "Nile Perch", "M-poo-tah", "🐠", 0.74f, 0.28f),
                StorybookObject("FSH_MALE", "Male", "Mudfish / Catfish", "Mah-leh", "🐡", 0.48f, 0.48f),
                StorybookObject("FSH_MUKENE", "Mukene", "Silver Fish", "Moo-keh-neh", "✨", 0.20f, 0.66f),
                StorybookObject("FSH_KASULU", "Kasulu", "Elephant-Snout Fish", "Kah-soo-loo", "🐬", 0.75f, 0.66f)
            )
        ),
        StorybookSpread(
            bookId = "fish",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ensomba",
            bookTitleEnglish = "Fish & River",
            bookIconEmoji = "🐟",
            titleLukenye = "Eryato n'Ekigogo",
            titleEnglish = "The Dugout Canoe & Paddle",
            conceptDescription = "Hand-carved cedar canoes, polished paddles, weave nets and fish traps.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.RIVERBANK_DAWN,
            narrativeLukenye = "Omulunnyanja asikayo eryato lye n'enkasi eyalukibwa obulungi.",
            narrativeEnglish = "The canoe pilot glides outward with a smooth paddle carved from mahogany.",
            proverbLukenye = "Eryato lisigala, naye amazzi gagenda.",
            proverbEnglish = "The canoe endures while the river waters flow onward.",
            culturalFact = "Traditional Bakenyi canoes are hollowed from a single solid tree trunk with ancestral care.",
            interactiveObjects = listOf(
                StorybookObject("FSH_ERYATO", "Eryato", "Dugout Canoe", "Eh-ryah-toh", "🛶", 0.20f, 0.28f),
                StorybookObject("FSH_NKASI", "Nkasi", "Carved Paddle", "N-kah-see", "🪵", 0.75f, 0.28f),
                StorybookObject("FSH_EKITTIMBA", "Ekittimba", "Woven Fishing Net", "Eh-keet-teem-bah", "🕸️", 0.48f, 0.46f),
                StorybookObject("FSH_OMUGONO", "Omugono", "Reed Fish Trap", "Oh-moo-goh-noh", "🧺", 0.20f, 0.64f),
                StorybookObject("FSH_EKISIIBO", "Ekisiibo", "Canoe Mooring Anchor", "Eh-kee-see-boh", "⚓", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("fish", "Ensomba", "Fish & River", "🐟", "Tilapia, canoes, paddles & river life", b7Spreads))

    // ==========================================
    // 8. NATURE & EARTH (Obutonde) - 2 Spreads
    // ==========================================
    val b8Spreads = listOf(
        StorybookSpread(
            bookId = "nature",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Obutonde",
            bookTitleEnglish = "Nature & Earth",
            bookIconEmoji = "🌱",
            titleLukenye = "Obutonde bw'Ensi",
            titleEnglish = "The Living Natural World",
            conceptDescription = "Rich red soil, hills, shore stones and grass.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            narrativeLukenye = "Etaka ly'emyofu limera emiti n'ebimera eby'omugaso.",
            narrativeEnglish = "The rich red earth nourishes trees and healing herbs for all living beings.",
            proverbLukenye = "Ensi ekuuma oyo agikuuma.",
            proverbEnglish = "The earth protects the one who nurtures and protects it.",
            culturalFact = "Red clay soil is celebrated in songs as the fertile mother of harvest.",
            interactiveObjects = listOf(
                StorybookObject("NAT_ETAKA", "Etaka", "Rich Red Earth", "Eh-tah-kah", "🌱", 0.20f, 0.28f),
                StorybookObject("NAT_AMABAALE", "Amabaale", "River Stones", "Ah-mah-bah-ah-leh", "🪨", 0.74f, 0.28f),
                StorybookObject("NAT_OMUDDO", "Omuddo", "Shore Grass", "Oh-moo-ddoh", "🌾", 0.48f, 0.45f),
                StorybookObject("NAT_OLUSOZI", "Olusozi", "Lakeside Hill", "Oh-loo-soh-zee", "⛰️", 0.20f, 0.64f),
                StorybookObject("NAT_LUFU", "Lufu", "Morning Mist", "Loo-foo", "🌫️", 0.75f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "nature",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Obutonde",
            bookTitleEnglish = "Nature & Earth",
            bookIconEmoji = "🌱",
            titleLukenye = "Ennyanja n'Ebitundu",
            titleEnglish = "Islands & Sacred Shores",
            conceptDescription = "Floating papyrus islands, shore caves, sand banks and springs.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.RIVERBANK_DAWN,
            narrativeLukenye = "Ebizinga by'obutiise byewugisa n'empewo ku nnyanja.",
            narrativeEnglish = "Floating islands of papyrus drift calmly with the gentle lake breeze.",
            proverbLukenye = "Omugga ogutakulumba gwe gukulungula.",
            proverbEnglish = "Quiet waters carry the deepest spirit.",
            culturalFact = "Floating islands (Sudd) drift across Lake Kyoga, carrying birds and marsh life.",
            interactiveObjects = listOf(
                StorybookObject("NAT_EKIZINGA", "Ekizinga", "Floating Island", "Eh-kee-zeen-gah", "🏝️", 0.20f, 0.28f),
                StorybookObject("NAT_OMUGGA", "Omugga", "Freshwater Stream", "Oh-moo-ggah", "🌊", 0.74f, 0.28f),
                StorybookObject("NAT_OMUSENYI", "Omusenyi", "Golden Sand", "Oh-moo-seh-nyee", "🏖️", 0.48f, 0.46f),
                StorybookObject("NAT_EMPUKU", "Empuku", "Lakeside Cave", "Em-poo-koo", "🕳️", 0.20f, 0.64f),
                StorybookObject("NAT_OLUZZI", "Oluzzi", "Fresh Spring", "Oh-loo-zzee", "⛲", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("nature", "Obutonde", "Nature & Earth", "🌱", "Red soil, shore stones & morning mist", b8Spreads))

    // ==========================================
    // 9. TREES (Emiti) - 2 Spreads
    // ==========================================
    val b9Spreads = listOf(
        StorybookSpread(
            bookId = "trees",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Emiti",
            bookTitleEnglish = "Trees",
            bookIconEmoji = "🌳",
            titleLukenye = "Emiti gy'Obakenyi",
            titleEnglish = "Trees of the Bakenyi Lands",
            conceptDescription = "Ancient baobab canopy, palm trees and leaves.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            narrativeLukenye = "Omuti omunene gwa baobab gukuuma ebyama by'abakulu.",
            narrativeEnglish = "The mighty baobab tree shelters the deep stories of the clan elders.",
            proverbLukenye = "Omuti omugumu teguyisibwa mpewo.",
            proverbEnglish = "A deeply rooted tree stands fearless against the wind.",
            culturalFact = "Baobabs store thousands of liters of clean water within their fibrous trunks.",
            interactiveObjects = listOf(
                StorybookObject("TRE_OMUTI", "Omuti", "Baobab Tree", "Oh-moo-tee", "🌳", 0.22f, 0.28f),
                StorybookObject("TRE_ENKOMA", "Enkoma", "Wild Palm Tree", "En-koh-mah", "🌴", 0.74f, 0.26f),
                StorybookObject("TRE_AMALAGALA", "Amalagala", "Broad Green Leaves", "Ah-mah-lah-gah-lah", "🍃", 0.48f, 0.46f),
                StorybookObject("TRE_EMIZI", "Emizi", "Deep Tree Roots", "Eh-mee-zee", "🪵", 0.20f, 0.66f),
                StorybookObject("TRE_EKIKOLIGO", "Ekikoligo", "Tree Branch", "Eh-kee-koh-lee-goh", "🌿", 0.76f, 0.66f)
            )
        ),
        StorybookSpread(
            bookId = "trees",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Emiti",
            bookTitleEnglish = "Trees",
            bookIconEmoji = "🌳",
            titleLukenye = "Emiti gy'Eddagala",
            titleEnglish = "Medicinal & Barkcloth Trees",
            conceptDescription = "Fig barkcloth tree (Mutuba), neem leaves and sweet tamarind.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            narrativeLukenye = "Omutuba gutuwoomera olugoye era ne guwonya endwadde.",
            narrativeEnglish = "The Mutuba fig tree gifts soft barkcloth fabric and cooling shade.",
            proverbLukenye = "Akasaalaba akamala endwadde kanoonyezebwa.",
            proverbEnglish = "The herb that brings healing is sought with a grateful heart.",
            culturalFact = "Barkcloth beating from the Mutuba tree is an honored intangible world cultural heritage.",
            interactiveObjects = listOf(
                StorybookObject("TRE_OMUTUBA", "Omutuba", "Barkcloth Fig Tree", "Oh-moo-too-bah", "🎋", 0.20f, 0.28f),
                StorybookObject("TRE_OLUKUKU", "Olukuku", "Tree Bark", "Oh-loo-koo-koo", "🪵", 0.74f, 0.28f),
                StorybookObject("TRE_MUKO", "Muko", "Tamarind Tree", "Moo-koh", "🌲", 0.48f, 0.46f),
                StorybookObject("TRE_EKIMULI", "Ekimuli", "Sweet Flower", "Eh-kee-moo-lee", "🌺", 0.20f, 0.64f),
                StorybookObject("TRE_EMPEKE", "Empeke", "Forest Seed", "Em-peh-keh", "🌰", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("trees", "Emiti", "Trees", "🌳", "Mighty baobabs, palms & gentle green leaves", b9Spreads))

    // ==========================================
    // 10. FRUIT (Ebibala) - 2 Spreads
    // ==========================================
    val b10Spreads = listOf(
        StorybookSpread(
            bookId = "fruit",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ebibala",
            bookTitleEnglish = "Fruit",
            bookIconEmoji = "🥭",
            titleLukenye = "Ebibala Ebiwoomu",
            titleEnglish = "Sweet Orchard Fruit",
            conceptDescription = "Ripe mangoes, sweet bananas, papaya and jackfruit.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            narrativeLukenye = "Emiyembe myeru era giwooma nnyo eri abaana bonna.",
            narrativeEnglish = "Ripe mangoes turn golden sweet and bring big smiles to every child.",
            proverbLukenye = "Ekibala ekiwoomu kiva ku muti omulungi.",
            proverbEnglish = "Sweet, nourishing fruit grows from a healthy and protected tree.",
            culturalFact = "Mango harvesting seasons bring families together to dry and store sweet fruit slices.",
            interactiveObjects = listOf(
                StorybookObject("FRU_EMIYEMBE", "Emiyembe", "Sweet Mangoes", "Eh-mee-yem-beh", "🥭", 0.20f, 0.28f),
                StorybookObject("FRU_AMENVVU", "AmEnvvu", "Ripe Bananas", "Ah-men-vvoo", "🍌", 0.75f, 0.28f),
                StorybookObject("FRU_PPAAPAALI", "Ppaapaali", "Papaya", "Ppah-ah-pah-ah-lee", "🍈", 0.48f, 0.46f),
                StorybookObject("FRU_FENE", "Fene", "Jackfruit", "Feh-neh", "🥑", 0.20f, 0.64f),
                StorybookObject("FRU_ENNANAANSI", "Ennanaansi", "Pineapple", "En-nah-nah-ahn-see", "🍍", 0.75f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "fruit",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ebibala",
            bookTitleEnglish = "Fruit",
            bookIconEmoji = "🥭",
            titleLukenye = "Ebibala by'Omunsiko",
            titleEnglish = "Wild Wetland Berries & Melons",
            conceptDescription = "Wild passion fruits, yellow guavas, sugar cane and watermelons.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            narrativeLukenye = "Obutunda bwe munsiko bujjudde amazzi amalungi amatulegevu.",
            narrativeEnglish = "Wild wetland berries burst with cool, refreshing sweetness.",
            proverbLukenye = "Akalimi akawooma kasinga omuyembe.",
            proverbEnglish = "A sweet and kind tongue is sweeter than honey or ripe mango.",
            culturalFact = "Children gather sweet water reeds and wild passion fruit along canoe pathways.",
            interactiveObjects = listOf(
                StorybookObject("FRU_BUTUNDA", "Obutunda", "Passion Fruit", "Oh-boo-toon-dah", "🍇", 0.20f, 0.28f),
                StorybookObject("FRU_MAPEERA", "Amapeera", "Guava", "Ah-mah-peh-eh-rah", "🍐", 0.75f, 0.28f),
                StorybookObject("FRU_KIKOJJO", "Ekikojjo", "Sweet Sugar Cane", "Eh-kee-kohj-joh", "🎋", 0.48f, 0.46f),
                StorybookObject("FRU_WOTAMELONI", "Ewujju", "Watermelon", "Eh-wooj-joo", "🍉", 0.20f, 0.64f),
                StorybookObject("FRU_KUKUMISI", "Enkukumisi", "Wild Berries", "En-koo-koo-mee-see", "🫐", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("fruit", "Ebibala", "Fruit", "🥭", "Mangoes, bananas, sweet papayas & jackfruits", b10Spreads))

    // ==========================================
    // 11. FOOD (Emmere) - 2 Spreads
    // ==========================================
    val b11Spreads = listOf(
        StorybookSpread(
            bookId = "food",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Emmere",
            bookTitleEnglish = "Food",
            bookIconEmoji = "🍲",
            titleLukenye = "Emmere y'Eka",
            titleEnglish = "Delicious Village Food",
            conceptDescription = "Steamed matooke, smoked fish, cassava and maize.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            narrativeLukenye = "Maama atokosa matooke n'ensomba mu bibalagala.",
            narrativeEnglish = "Mother steams tender matooke plantains and smoked fish in banana leaves.",
            proverbLukenye = "Emmere eriirwa wamu ewooma.",
            proverbEnglish = "A meal shared together with family tastes the sweetest.",
            culturalFact = "Food is always steamed gently in fresh banana leaf packets to seal in natural aromas.",
            interactiveObjects = listOf(
                StorybookObject("FOD_MATOOKE", "Matooke", "Steamed Plantains", "Mah-toh-oh-keh", "🍲", 0.20f, 0.28f),
                StorybookObject("FOD_ENSOMBA", "Ensomba", "Smoked Fish", "En-sohm-bah", "🐟", 0.75f, 0.28f),
                StorybookObject("FOD_MUWOGO", "Muwogo", "Fresh Cassava", "Moo-woh-goh", "🍠", 0.48f, 0.46f),
                StorybookObject("FOD_KASOLI", "Kasoli", "Roasted Maize", "Kah-soh-lee", "🌽", 0.20f, 0.65f),
                StorybookObject("FOD_AMAZZI", "Amazzi", "Clean Water", "Ah-mah-zzee", "🍶", 0.75f, 0.65f)
            )
        ),
        StorybookSpread(
            bookId = "food",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Emmere",
            bookTitleEnglish = "Food",
            bookIconEmoji = "🍲",
            titleLukenye = "Enva n'Ebikoola",
            titleEnglish = "Groundnut Sauce & Greens",
            conceptDescription = "Rich groundnut paste, pumpkin leaves, yams and millet bread.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            narrativeLukenye = "Bafumba ebinyebwa ne kalo k'omugati ku kyoto.",
            narrativeEnglish = "They prepare savory peanut stew and warm millet bread over the fire.",
            proverbLukenye = "Akamwa kalyo tekawooma buwereere.",
            proverbEnglish = "A grateful heart appreciates every harvest provided.",
            culturalFact = "Groundnuts are pounded in wooden mortars into rich, creamy stew paste.",
            interactiveObjects = listOf(
                StorybookObject("FOD_EBINYEBWA", "Ebinyebwa", "Groundnut Stew", "Eh-bee-nyeh-bwah", "🥜", 0.20f, 0.28f),
                StorybookObject("FOD_KALO", "Akalo", "Millet Bread", "Ah-kah-loh", "🫓", 0.75f, 0.28f),
                StorybookObject("FOD_DOODO", "Doodo", "Steamed Greens", "Doh-oh-doh", "🥬", 0.48f, 0.46f),
                StorybookObject("FOD_EBIJUMBA", "Ebijumba", "Sweet Yams", "Eh-bee-joom-bah", "🥔", 0.20f, 0.64f),
                StorybookObject("FOD_OMUNYO", "Omunyo", "Lake Salt", "Oh-moo-nyoh", "🧂", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("food", "Emmere", "Food", "🍲", "Steamed matooke, roasted maize & cassava", b11Spreads))

    // ==========================================
    // 12. WATER (Amazzi) - 2 Spreads
    // ==========================================
    val b12Spreads = listOf(
        StorybookSpread(
            bookId = "water",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Amazzi",
            bookTitleEnglish = "Water & Lake",
            bookIconEmoji = "💧",
            titleLukenye = "Amazzi g'Ennyanja",
            titleEnglish = "Ripples of Lake Kyoga",
            conceptDescription = "Living river water, reeds, water lilies and waves.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.RIVERBANK_DAWN,
            narrativeLukenye = "Amazzi g'ennyanja gatebenkedde era geeyongera okuba amayonjo.",
            narrativeEnglish = "The waters of the lake rest tranquil and clear under the sky.",
            proverbLukenye = "Amazzi tegakulira lusozi.",
            proverbEnglish = "Water flows steadily without rushing uphill — maintain your peace.",
            culturalFact = "Bakenyi communities protect the shorelines from disturbance to keep the lake sparkling.",
            interactiveObjects = listOf(
                StorybookObject("WAT_AMAZZI", "Amazzi", "Living Water", "Ah-mah-zzee", "💧", 0.20f, 0.28f),
                StorybookObject("WAT_ENNYANJA", "Ennyanja", "Lake Kyoga", "En-nyahn-jah", "🌊", 0.75f, 0.28f),
                StorybookObject("WAT_KIYANJA", "Kiyanja", "Papyrus Reed", "Kee-yahn-jah", "🌾", 0.48f, 0.46f),
                StorybookObject("WAT_EKIMULI", "Ekimuli ky'Amazzi", "Water Lily", "Eh-kee-moo-lee ky'Ah-mah-zzee", "🪷", 0.20f, 0.64f),
                StorybookObject("WAT_EJJE", "Ejje", "Gentle Wave", "Ej-jeh", "〰️", 0.75f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "water",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Amazzi",
            bookTitleEnglish = "Water & Lake",
            bookIconEmoji = "💧",
            titleLukenye = "Omulubalama n'Emyalo",
            titleEnglish = "Channels & Shimmering Shores",
            conceptDescription = "Quiet lagoons, morning dew, canoe channels and clean springs.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.RIVERBANK_DAWN,
            narrativeLukenye = "Omume gukwata ku bimuli ebiwonvu ku makya.",
            narrativeEnglish = "Fresh morning dew settles like diamonds upon soft blossoms.",
            proverbLukenye = "Omume tegumala nnyonta naye guwonya ekiragala.",
            proverbEnglish = "Gentle dew drops may be small, but they keep the whole leaf alive.",
            culturalFact = "Natural wetlands act as Lake Kyoga's giant ecological freshwater filter.",
            interactiveObjects = listOf(
                StorybookObject("WAT_OMUME", "Omume", "Morning Dew", "Oh-moo-meh", "✨", 0.20f, 0.28f),
                StorybookObject("WAT_OLUKOKO", "Olukoko", "Canoe Channel", "Oh-loo-koh-koh", "🛶", 0.75f, 0.28f),
                StorybookObject("WAT_EKIWONVU", "Ekiwonvu", "Quiet Lagoon", "Eh-kee-wohn-voo", "🏞️", 0.48f, 0.46f),
                StorybookObject("WAT_ENTONNYO", "Entonnyo", "Water Droplet", "En-tohn-nyoh", "🫧", 0.20f, 0.64f),
                StorybookObject("WAT_ENSUWA", "Ensuwa y'Amazzi", "Clay Water Jar", "En-soo-wah", "🏺", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("water", "Amazzi", "Water & Lake", "💧", "Shimmering lake waves, reeds & water lilies", b12Spreads))

    // ==========================================
    // 13. WEATHER & SKY (Obudde) - 2 Spreads
    // ==========================================
    val b13Spreads = listOf(
        StorybookSpread(
            bookId = "weather",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Obudde",
            bookTitleEnglish = "Weather & Sky",
            bookIconEmoji = "☀️",
            titleLukenye = "Obudde bw'Omusana",
            titleEnglish = "Sunny Days & Rainbows",
            conceptDescription = "Golden sun, soft rain showers, rainbow and soft breeze.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.LAKESIDE_COLOURS,
            narrativeLukenye = "Musoke atadde langi z'eggulu zonna mu bbanga oluvanyuma lw'enkuba.",
            narrativeEnglish = "Musoke the rainbow paints bright celestial arches after gentle rainfall.",
            proverbLukenye = "Enkuba etonnya teziyiza njuba kuleeta musana.",
            proverbEnglish = "Even the heaviest shower gives way to warm sunshine.",
            culturalFact = "Musoke is honored in folklore as the peaceful spirit of the lake rainbow.",
            interactiveObjects = listOf(
                StorybookObject("WTH_ENJUBA", "Enjuba", "Golden Sun", "En-joo-bah", "☀️", 0.20f, 0.28f),
                StorybookObject("WTH_ENKUBA", "Enkuba", "Gentle Rain", "En-koo-bah", "🌧️", 0.75f, 0.28f),
                StorybookObject("WTH_MUSOKE", "Musoke", "Sky Rainbow", "Moo-soh-keh", "🌈", 0.48f, 0.46f),
                StorybookObject("WTH_EBIRE", "Ebire", "Soft Clouds", "Eh-bee-reh", "☁️", 0.20f, 0.64f),
                StorybookObject("WTH_EMPEWO", "Empewo", "Lake Breeze", "Em-peh-woh", "🍃", 0.75f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "weather",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Obudde",
            bookTitleEnglish = "Weather & Sky",
            bookIconEmoji = "☀️",
            titleLukenye = "Ekiro n'Emunyeenye",
            titleEnglish = "Night Sky & Moonbeams",
            conceptDescription = "Bright crescent moon, twinkling constellations, gentle thunder and night breeze.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.FISHERMAN_SONG,
            narrativeLukenye = "Omwezi n'emunyeenye byaka bulungi era bikuuma ekiro kyonna.",
            narrativeEnglish = "The silver moon and twinkling stars shine peacefully through the night.",
            proverbLukenye = "Omwezi tegulumizibwa embwa.",
            proverbEnglish = "The serene moon shines bright undeterred by barking below.",
            culturalFact = "Fishers use the Southern Cross constellation as their night canoe compass.",
            interactiveObjects = listOf(
                StorybookObject("WTH_OMWEZI", "Omwezi", "Silver Moon", "Oh-mweh-zee", "🌙", 0.20f, 0.28f),
                StorybookObject("WTH_EMUNYEENYE", "Emunyeenye", "Night Stars", "Eh-moon-yeh-en-yeh", "✨", 0.75f, 0.28f),
                StorybookObject("WTH_EKIRO", "Ekiro", "Peaceful Night", "Eh-kee-roh", "🌌", 0.48f, 0.46f),
                StorybookObject("WTH_KABUBBU", "Kabubbu", "Shooting Star", "Kah-boob-boo", "🌠", 0.20f, 0.64f),
                StorybookObject("WTH_LADDU", "Laddu", "Distant Lightning", "Lah-ddoo", "⚡", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("weather", "Obudde", "Weather & Sky", "☀️", "Golden sun, rainbows, soft rain & night stars", b13Spreads))

    // ==========================================
    // 14. COLOURS (Langi) - 2 Spreads
    // ==========================================
    val b14Spreads = listOf(
        StorybookSpread(
            bookId = "colours",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Langi",
            bookTitleEnglish = "Colours",
            bookIconEmoji = "🎨",
            titleLukenye = "Langi z'Eggulu",
            titleEnglish = "Primary Lakeside Palette",
            conceptDescription = "Red sunset, green reeds, blue lake and yellow sun.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.LAKESIDE_COLOURS,
            narrativeLukenye = "Ensi ejjudde langi nnyingi ezisaanyusa amaaso g'abaana.",
            narrativeEnglish = "The natural earth is full of glowing colors that delight the eyes.",
            proverbLukenye = "Langi zonna ze zikola ekitondekyo ekirungi.",
            proverbEnglish = "Every distinct color contributes to the magnificent picture.",
            culturalFact = "Natural cloth dyes are extracted from roots, berries and crushed hibiscus.",
            interactiveObjects = listOf(
                StorybookObject("COL_MYOFU", "Myofu (Red)", "Red Sunset", "Myoh-foo", "🔴 🌅", 0.20f, 0.28f),
                StorybookObject("COL_KIRAGALA", "Kiragala (Green)", "Green Leaf", "Kee-rah-gah-lah", "🟢 🍃", 0.75f, 0.28f),
                StorybookObject("COL_BULURU", "Buluru (Blue)", "Blue River", "Boo-loo-roo", "🔵 💧", 0.20f, 0.64f),
                StorybookObject("COL_KYENVO", "Kyenvo (Yellow)", "Yellow Sun", "Kyen-voh", "🟡 ☀️", 0.75f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "colours",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Langi",
            bookTitleEnglish = "Colours",
            bookIconEmoji = "🎨",
            titleLukenye = "Langi z'Ettaka n'Ekiro",
            titleEnglish = "Earth Tones & Whites",
            conceptDescription = "White egret feather, black fertile soil, brown clay and purple lotus.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.LAKESIDE_COLOURS,
            narrativeLukenye = "Enyange yeeru nnyo, ate ettaka ly'omuliro kiddugavu.",
            narrativeEnglish = "The white egret is pure snow-white, while the fertile soil is deep ebony.",
            proverbLukenye = "Obwerufu bwe bumu tebuva ku langi y'olususu.",
            proverbEnglish = "True beauty springs from kindness within the heart.",
            culturalFact = "White clay is traditionally used in ceremonies as a blessing of peace.",
            interactiveObjects = listOf(
                StorybookObject("COL_YERA", "Yeru (White)", "White Egret", "Yeh-roo", "⚪ 🦢", 0.20f, 0.28f),
                StorybookObject("COL_DUGAVU", "Ddugavu (Black)", "Ebony Earth", "Ddoo-gah-voo", "⚫ 🌱", 0.75f, 0.28f),
                StorybookObject("COL_KITAKA", "Kitaka (Brown)", "Brown Clay", "Kee-tah-kah", "🟤 🏺", 0.20f, 0.64f),
                StorybookObject("COL_KAKOBE", "Kakobe (Purple)", "Purple Lily", "Kah-koh-beh", "🟣 🪷", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("colours", "Langi", "Colours", "🎨", "Red, green, blue & bright yellow colours", b14Spreads))

    // ==========================================
    // 15. NUMBERS (Ebibalo) - 2 Spreads
    // ==========================================
    val b15Spreads = listOf(
        StorybookSpread(
            bookId = "numbers",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ebibalo",
            bookTitleEnglish = "Numbers",
            bookIconEmoji = "🔢",
            titleLukenye = "Ebibalo mu Bakenyi (1 - 5)",
            titleEnglish = "Counting by the Shore (1 - 5)",
            conceptDescription = "Numbers 1 through 5 with river items.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.LAKESIDE_COUNTING,
            narrativeLukenye = "Twaala obubalo bw'ensonga: emu, bbiri, ssatu, nnya, tano!",
            narrativeEnglish = "Let us count the treasures of the lake: one, two, three, four, five!",
            proverbLukenye = "Embalula emu teziyiza kusaana.",
            proverbEnglish = "One seed today grows into a whole field tomorrow.",
            culturalFact = "Children use smooth river pebbles to learn traditional count rhythms.",
            interactiveObjects = listOf(
                StorybookObject("NUM_1", "Emu (1)", "One Canoe", "Eh-moo", "1️⃣ 🛶", 0.18f, 0.28f),
                StorybookObject("NUM_2", "Bbiri (2)", "Two Fish", "Bbee-ree", "2️⃣ 🐟", 0.75f, 0.28f),
                StorybookObject("NUM_3", "Ssatu (3)", "Three Reeds", "Ssah-too", "3️⃣ 🌾", 0.18f, 0.62f),
                StorybookObject("NUM_4", "Nnya (4)", "Four Stones", "N-nyah", "4️⃣ 🪨", 0.75f, 0.62f),
                StorybookObject("NUM_5", "Tano (5)", "Five Birds", "Tah-noh", "5️⃣ 🦩", 0.48f, 0.45f)
            )
        ),
        StorybookSpread(
            bookId = "numbers",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ebibalo",
            bookTitleEnglish = "Numbers",
            bookIconEmoji = "🔢",
            titleLukenye = "Ebibalo ebinene (6 - 10)",
            titleEnglish = "Counting Together (6 - 10)",
            conceptDescription = "Numbers 6 through 10 with baobab seeds, shells and drums.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.LAKESIDE_COUNTING,
            narrativeLukenye = "Tugendekana n'ebibalo: mukaaga, musanvu, munaana, mwenda, n'ekkumi!",
            narrativeEnglish = "Let us count higher: six, seven, eight, nine, and ten!",
            proverbLukenye = "Engalo ekkumi ze zikola emirimo gyonna.",
            proverbEnglish = "Ten coordinated fingers accomplish great wonders.",
            culturalFact = "Ten claps of the drum mark the ceremonial opening of village council.",
            interactiveObjects = listOf(
                StorybookObject("NUM_6", "Mukaaga (6)", "Six Baobab Seeds", "Moo-kah-ah-gah", "6️⃣ 🌰", 0.18f, 0.28f),
                StorybookObject("NUM_7", "Musanvu (7)", "Seven Shells", "Moo-sahn-voo", "7️⃣ 🐚", 0.75f, 0.28f),
                StorybookObject("NUM_8", "Munaana (8)", "Eight Reeds", "Moo-nah-ah-nah", "8️⃣ 🌿", 0.18f, 0.62f),
                StorybookObject("NUM_9", "Mwenda (9)", "Nine Stars", "Mwen-dah", "9️⃣ ✨", 0.75f, 0.62f),
                StorybookObject("NUM_10", "Ekkumi (10)", "Ten Drums", "Ek-koo-mee", "🔟 🪘", 0.48f, 0.45f)
            )
        )
    )
    booksList.add(Storybook("numbers", "Ebibalo", "Numbers", "🔢", "Counting 1 to 10 with canoes, fish, seeds & drums", b15Spreads))

    // ==========================================
    // 16. SONGS & LULLABIES (Ennyimba) - 2 Spreads
    // ==========================================
    val b16Spreads = listOf(
        StorybookSpread(
            bookId = "songs",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ennyimba",
            bookTitleEnglish = "Songs & Lullabies",
            bookIconEmoji = "🎵",
            titleLukenye = "Ennyimba z'Abalunnyanja",
            titleEnglish = "Songs of the Canoe Fishermen",
            conceptDescription = "Paddle rhythms, heartbeat drums and voyage chants.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.FISHERMAN_SONG,
            narrativeLukenye = "Abalunnyanja bayimbira wamu n'enkasi: 'Hee-ya! Kyoga nnyanja yaffe!'",
            narrativeEnglish = "The canoe fishers row in chorus with rhythm: 'Hee-ya! Kyoga is our calm home!'",
            proverbLukenye = "Oluyimba lusitula obukoowu.",
            proverbEnglish = "A shared song lifts all tiredness from the paddlers' arms.",
            culturalFact = "Rowing songs keep every canoe stroke synchronized across long journeys.",
            interactiveObjects = listOf(
                StorybookObject("SNG_NKASI", "Nkasi", "Paddle Stroke Rhythm", "N-kah-see", "🛶 🪵", 0.20f, 0.32f),
                StorybookObject("SNG_ENGOMA", "Engoma", "Heritage Drum", "En-goh-mah", "🪘", 0.74f, 0.32f),
                StorybookObject("SNG_OLUYIMBA", "Oluyimba", "Lakeside Chant", "Oh-loo-yeem-bah", "🎵 🌊", 0.48f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "songs",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Ennyimba",
            bookTitleEnglish = "Songs & Lullabies",
            bookIconEmoji = "🎵",
            titleLukenye = "Oluyimba lw'Ekiro",
            titleEnglish = "Bedtime Lakeside Lullaby",
            conceptDescription = "Thumb piano (Kalimba), soothing flute and soft cradle lullaby.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.FISHERMAN_SONG,
            narrativeLukenye = "Maama ayimbira omwana n'eddoboozi ly'emirembe: 'Webake bulungi mwana wange.'",
            narrativeEnglish = "Mother sings her gentle lullaby: 'Rest peacefully in sweet slumber, my precious child.'",
            proverbLukenye = "Tulo tuwona obulamu.",
            proverbEnglish = "Peaceful sleep heals and renews the spirit.",
            culturalFact = "Soft thumb piano melodies mimic the soothing trickle of lake water.",
            interactiveObjects = listOf(
                StorybookObject("SNG_KALIMBA", "Kalimba", "African Thumb Piano", "Kah-leem-bah", "🎹 🌾", 0.20f, 0.30f),
                StorybookObject("SNG_ENDERE", "Endere", "Bamboo Flute", "En-deh-reh", "🪈", 0.74f, 0.30f),
                StorybookObject("SNG_TULO", "Otulo", "Gentle Slumber", "Oh-too-loh", "🌙 ✨", 0.48f, 0.62f)
            )
        )
    )
    booksList.add(Storybook("songs", "Ennyimba", "Songs & Lullabies", "🎵", "Canoe paddle rhythms & gentle lake lullabies", b16Spreads))

    // ==========================================
    // 17. DAILY LIFE (Obulamu) - 2 Spreads
    // ==========================================
    val b17Spreads = listOf(
        StorybookSpread(
            bookId = "dailylife",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Obulamu",
            bookTitleEnglish = "Daily Life",
            bookIconEmoji = "🌅",
            titleLukenye = "Obulamu bw'Buli Lunaku",
            titleEnglish = "Morning to Afternoon at Lake Kyoga",
            conceptDescription = "Waking at dawn, washing with cool stream water, paddling and playing.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            narrativeLukenye = "Abaana bazuukuka n'essanyu ne bakolagana n'abazadde baabwe.",
            narrativeEnglish = "Children wake up with bright joy and help their parents along the shore.",
            proverbLukenye = "Akakeeka akalungi katandika n'oluseke lumu.",
            proverbEnglish = "The most beautiful mat begins with a single woven reed.",
            culturalFact = "Morning greetings and washing face in the fresh breeze starts every good day.",
            interactiveObjects = listOf(
                StorybookObject("DAY_WAKE", "Okuzuukuka", "Waking at Dawn", "Oh-koo-zoo-koo-kah", "🌅", 0.20f, 0.28f),
                StorybookObject("DAY_WASH", "Okunaaba", "Washing by Stream", "Oh-koo-nah-ah-bah", "💧", 0.75f, 0.28f),
                StorybookObject("DAY_FISH", "Okuvuba", "Fishing in Canoe", "Oh-koo-voo-bah", "🛶", 0.20f, 0.64f),
                StorybookObject("DAY_PLAY", "Okuzannya", "Children Playing", "Oh-koo-zahn-nyah", "🧒🏽", 0.75f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "dailylife",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Obulamu",
            bookTitleEnglish = "Daily Life",
            bookIconEmoji = "🌅",
            titleLukenye = "Akawungeezi n'Okwebaka",
            titleEnglish = "Evening Stories & Bedtime Rest",
            conceptDescription = "Evening meal, firelight stories with elders and cozy sleep.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            narrativeLukenye = "Oluvanyuma lw'emmere y'akawungeezi, abaana beebaka mirembe.",
            narrativeEnglish = "After the warm evening feast, children sleep peacefully in their woven beds.",
            proverbLukenye = "Olunaku olulungi lwebazibwa n'ekiro ekitangaavu.",
            proverbEnglish = "A purposeful day is concluded with a tranquil, peaceful night.",
            culturalFact = "Telling folktales by the hearth is the traditional school of Bakenyi children.",
            interactiveObjects = listOf(
                StorybookObject("DAY_EAT", "Okulya", "Evening Feast", "Oh-koo-lyah", "🍲", 0.20f, 0.28f),
                StorybookObject("DAY_STORY", "Enfumo", "Elder Storytelling", "En-foo-moh", "📖", 0.75f, 0.28f),
                StorybookObject("DAY_REST", "Okwebaka", "Bedtime Sleep", "Oh-kweh-bah-kah", "🌙", 0.48f, 0.50f),
                StorybookObject("DAY_DREAM", "Ebirooto", "Sweet Dreams", "Eh-bee-roh-oh-toh", "✨", 0.20f, 0.68f),
                StorybookObject("DAY_PEACE", "Emirembe", "Serenity & Peace", "Eh-mee-rem-beh", "🕊️", 0.75f, 0.68f)
            )
        )
    )
    booksList.add(Storybook("dailylife", "Obulamu", "Daily Life", "🌅", "Waking, paddling, playing & bedtime rest", b17Spreads))

    // ==========================================
    // 18. COMMUNITY & ELDERS (Abantu) - 2 Spreads
    // ==========================================
    val b18Spreads = listOf(
        StorybookSpread(
            bookId = "community",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 1,
            totalPagesInBook = 2,
            bookTitleLukenye = "Abantu",
            bookTitleEnglish = "Community",
            bookIconEmoji = "🤝",
            titleLukenye = "Abantu n'Abakulu",
            titleEnglish = "Elders & Cherished Neighbors",
            conceptDescription = "Elder storytellers, canoe fishers, potters and friends.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            narrativeLukenye = "Abantu bakolera wamu n'omukwano mu kutuukiriza obulungi bw'ekyalo.",
            narrativeEnglish = "Neighbors work together with deep affection to uplift the entire village community.",
            proverbLukenye = "Omuntu ye muntu olw'abantu.",
            proverbEnglish = "Ubuntu: A person is truly human through their bond with others.",
            culturalFact = "Collective communal work (Bulungi Bwansi) builds shared bridges and canoe trails.",
            interactiveObjects = listOf(
                StorybookObject("COM_OMUKULU", "Omukulu", "Elder Storyteller", "Oh-moo-koo-loo", "👴🏽", 0.20f, 0.28f),
                StorybookObject("COM_OMULUNNYANJA", "Omulunnyanja", "Canoe Fisher", "Oh-moo-loon-nyahn-jah", "🛶", 0.75f, 0.28f),
                StorybookObject("COM_OMUBUMBI", "Omubumbi", "Clay Potter", "Oh-moo-boom-bee", "🏺", 0.48f, 0.46f),
                StorybookObject("COM_MUKAANO", "Mukaano", "Cherished Friend", "Moo-kah-ah-noh", "🤝", 0.20f, 0.64f),
                StorybookObject("COM_OMUGENYI", "Omugenyi", "Welcomed Guest", "Oh-moo-geh-nyee", "🌟", 0.75f, 0.64f)
            )
        ),
        StorybookSpread(
            bookId = "community",
            pageIndex = globalPageIndex++,
            pageNumberInBook = 2,
            totalPagesInBook = 2,
            bookTitleLukenye = "Abantu",
            bookTitleEnglish = "Community",
            bookIconEmoji = "🤝",
            titleLukenye = "Obumu n'Empisa z'Obuntu",
            titleEnglish = "Unity, Kindness & Generosity",
            conceptDescription = "Sharing the catch, welcoming strangers, kindness and clan celebrations.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            narrativeLukenye = "Buri omu agabana ku ky'afunye, tewali asigala mu kwetaaga.",
            narrativeEnglish = "Everyone shares their daily harvest so that no child or neighbor goes without.",
            proverbLukenye = "Engalo ezirira awamu tezivaamu bavu.",
            proverbEnglish = "Hands that cook and share together never encounter poverty.",
            culturalFact = "Generosity to guests is the cornerstone of Bakenyi hospitality.",
            interactiveObjects = listOf(
                StorybookObject("COM_OBUMU", "Obumu", "Unity & Harmony", "Oh-boo-moo", "💖", 0.20f, 0.28f),
                StorybookObject("COM_KIGAMBO", "Ekigambo", "Kind Words", "Eh-kee-gahm-boh", "🗣️", 0.75f, 0.28f),
                StorybookObject("COM_OKUGABA", "Okugaba", "Generous Sharing", "Oh-koo-gah-bah", "🎁", 0.48f, 0.46f),
                StorybookObject("COM_OKUKUUMA", "Okukuuma", "Protecting Nature", "Oh-koo-koo-mah", "🌿", 0.20f, 0.64f),
                StorybookObject("COM_ESSANYU", "Essanyu", "Shared Joy", "Es-sahn-yoo", "🎉", 0.75f, 0.64f)
            )
        )
    )
    booksList.add(Storybook("community", "Abantu", "Community", "🤝", "Storyteller elders, potters & cherished friends", b18Spreads))

    return booksList
}

/**
 * Returns all flattened spreads for smooth horizontal reading across all 18 books.
 */
fun getAllStorybookSpreads(): List<StorybookSpread> {
    return getLaunchStorybooks().flatMap { it.spreads }
}
