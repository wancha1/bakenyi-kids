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
    val pageIndex: Int,
    val titleLukenye: String,
    val titleEnglish: String,
    val conceptDescription: String,
    val biome: SanctuaryBiome,
    val themeStyle: StorybookThemeStyle,
    val interactiveObjects: List<StorybookObject>
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
 * Helper providing the 7 full-screen illustrated learning spreads.
 */
fun getStorybookSpreads(): List<StorybookSpread> {
    return listOf(
        // SPREAD 1: Riverbank Greetings
        StorybookSpread(
            pageIndex = 0,
            titleLukenye = "Okulamusa ku Nnyanja",
            titleEnglish = "Greetings on the Riverbank",
            conceptDescription = "Tap objects to hear Bakenyi greetings and shore life.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.RIVERBANK_DAWN,
            interactiveObjects = listOf(
                StorybookObject(
                    id = "KIGO_OTTER",
                    labelLukenye = "Oli otya!",
                    labelEnglish = "Hello! / How are you!",
                    pronunciation = "Oh-lee oh-tyah!",
                    illustrationEmoji = "🦦",
                    xPercent = 0.22f,
                    yPercent = 0.38f
                ),
                StorybookObject(
                    id = "ERYATO_CANOE",
                    labelLukenye = "Eryato",
                    labelEnglish = "Dugout Canoe",
                    pronunciation = "Eh-ryah-toh",
                    illustrationEmoji = "🛶",
                    xPercent = 0.72f,
                    yPercent = 0.45f
                ),
                StorybookObject(
                    id = "NALUBA_KINGFISHER",
                    labelLukenye = "Ensomba",
                    labelEnglish = "Fish",
                    pronunciation = "En-sohm-bah",
                    illustrationEmoji = "🐦",
                    xPercent = 0.78f,
                    yPercent = 0.20f
                ),
                StorybookObject(
                    id = "AMABAALE_RIVER",
                    labelLukenye = "Amabaale",
                    labelEnglish = "River Water",
                    pronunciation = "Ah-mah-bah-ah-leh",
                    illustrationEmoji = "🌊",
                    xPercent = 0.20f,
                    yPercent = 0.68f
                ),
                StorybookObject(
                    id = "PAPYRUS_REED",
                    labelLukenye = "Kiyanja",
                    labelEnglish = "Papyrus Reed",
                    pronunciation = "Kee-yahn-jah",
                    illustrationEmoji = "🌾",
                    xPercent = 0.50f,
                    yPercent = 0.28f
                )
            )
        ),

        // SPREAD 2: Village Hearth & Home
        StorybookSpread(
            pageIndex = 1,
            titleLukenye = "Amaaka n'Ekyalo",
            titleEnglish = "Family & Village Hearth",
            conceptDescription = "Learn home terms, hearth fires and family roles.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.VILLAGE_HEARTH,
            interactiveObjects = listOf(
                StorybookObject(
                    id = "JJAJJA_GRANDMOTHER",
                    labelLukenye = "JjaJja",
                    labelEnglish = "Grandmother",
                    pronunciation = "Jjah-Jjah",
                    illustrationEmoji = "👵🏽",
                    xPercent = 0.24f,
                    yPercent = 0.36f
                ),
                StorybookObject(
                    id = "ENJU_HOUSE",
                    labelLukenye = "Enju",
                    labelEnglish = "Clay Home",
                    pronunciation = "En-joo",
                    illustrationEmoji = "🏡",
                    xPercent = 0.72f,
                    yPercent = 0.32f
                ),
                StorybookObject(
                    id = "EKYOTO_HEARTH",
                    labelLukenye = "Ekyoto",
                    labelEnglish = "Fireside Hearth",
                    pronunciation = "Eh-kyoh-toh",
                    illustrationEmoji = "🔥",
                    xPercent = 0.48f,
                    yPercent = 0.62f
                ),
                StorybookObject(
                    id = "OMWANA_CHILD",
                    labelLukenye = "Omwana",
                    labelEnglish = "Young Child",
                    pronunciation = "Oh-mwah-nah",
                    illustrationEmoji = "🧒🏽",
                    xPercent = 0.18f,
                    yPercent = 0.65f
                ),
                StorybookObject(
                    id = "ENSIIMU_POT",
                    labelLukenye = "Ensiimu",
                    labelEnglish = "Clay Cooking Pot",
                    pronunciation = "En-see-moo",
                    illustrationEmoji = "🍲",
                    xPercent = 0.76f,
                    yPercent = 0.68f
                )
            )
        ),

        // SPREAD 3: Animals of the Wetlands
        StorybookSpread(
            pageIndex = 2,
            titleLukenye = "Ebisolo mu Musiri",
            titleEnglish = "Animals of the Wetlands",
            conceptDescription = "Discover wild wetland animals of Lake Kyoga.",
            biome = SanctuaryBiome.HILLS_AND_MEADOWS,
            themeStyle = StorybookThemeStyle.WETLAND_MARSH,
            interactiveObjects = listOf(
                StorybookObject(
                    id = "SSOZI_CRANE",
                    labelLukenye = "Engwali",
                    labelEnglish = "Crested Crane",
                    pronunciation = "En-gwah-lee",
                    illustrationEmoji = "🦩",
                    xPercent = 0.22f,
                    yPercent = 0.30f
                ),
                StorybookObject(
                    id = "ENVYUBU_HIPPO",
                    labelLukenye = "Envyubu",
                    labelEnglish = "Hippopotamus",
                    pronunciation = "En-vyoo-boo",
                    illustrationEmoji = "🦛",
                    xPercent = 0.75f,
                    yPercent = 0.55f
                ),
                StorybookObject(
                    id = "LUMU_MONKEY",
                    labelLukenye = "Nkima",
                    labelEnglish = "Vervet Monkey",
                    pronunciation = "N-kee-mah",
                    illustrationEmoji = "🐒",
                    xPercent = 0.50f,
                    yPercent = 0.22f
                ),
                StorybookObject(
                    id = "ENGWE_LEOPARD",
                    labelLukenye = "Engwe",
                    labelEnglish = "Leopard",
                    pronunciation = "En-gweh",
                    illustrationEmoji = "🐆",
                    xPercent = 0.25f,
                    yPercent = 0.65f
                ),
                StorybookObject(
                    id = "ENNYONYI_BIRD",
                    labelLukenye = "Ennyonyi",
                    labelEnglish = "Songbird",
                    pronunciation = "En-nyoh-nyee",
                    illustrationEmoji = "🕊️",
                    xPercent = 0.78f,
                    yPercent = 0.25f
                )
            )
        ),

        // SPREAD 4: Trees, Fruits & Nature
        StorybookSpread(
            pageIndex = 3,
            titleLukenye = "Ebibala n'Emiti",
            titleEnglish = "Trees, Fruit & Nature",
            conceptDescription = "Explore baobab trees, wild fruits and soil.",
            biome = SanctuaryBiome.BAOBAB_FOREST,
            themeStyle = StorybookThemeStyle.BAOBAB_CANOPY,
            interactiveObjects = listOf(
                StorybookObject(
                    id = "OMUTI_BAOBAB",
                    labelLukenye = "Omuti",
                    labelEnglish = "Baobab Tree",
                    pronunciation = "Oh-moo-tee",
                    illustrationEmoji = "🌳",
                    xPercent = 0.28f,
                    yPercent = 0.32f
                ),
                StorybookObject(
                    id = "EBIBALA_FRUIT",
                    labelLukenye = "Ebibala",
                    labelEnglish = "Ripe Sweet Fruit",
                    pronunciation = "Eh-bee-bah-lah",
                    illustrationEmoji = "🥭",
                    xPercent = 0.72f,
                    yPercent = 0.28f
                ),
                StorybookObject(
                    id = "EKIMULI_FLOWER",
                    labelLukenye = "Ekimuli",
                    labelEnglish = "Wild Flower",
                    pronunciation = "Eh-kee-moo-lee",
                    illustrationEmoji = "🌸",
                    xPercent = 0.20f,
                    yPercent = 0.65f
                ),
                StorybookObject(
                    id = "ETAKA_SOIL",
                    labelLukenye = "Etaka",
                    labelEnglish = "Rich Earth",
                    pronunciation = "Eh-tah-kah",
                    illustrationEmoji = "🌱",
                    xPercent = 0.50f,
                    yPercent = 0.68f
                ),
                StorybookObject(
                    id = "ENJUBA_SUN",
                    labelLukenye = "Enjuba",
                    labelEnglish = "Golden Sun",
                    pronunciation = "En-joo-bah",
                    illustrationEmoji = "☀️",
                    xPercent = 0.78f,
                    yPercent = 0.62f
                )
            )
        ),

        // SPREAD 5: Counting by the Shore
        StorybookSpread(
            pageIndex = 4,
            titleLukenye = "Ebibalo mu Bakenyi",
            titleEnglish = "Counting by the Shore",
            conceptDescription = "Learn numbers 1 through 5 with shore items.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.LAKESIDE_COUNTING,
            interactiveObjects = listOf(
                StorybookObject(
                    id = "COUNT_1_CANOE",
                    labelLukenye = "Emu (1)",
                    labelEnglish = "One Canoe",
                    pronunciation = "Eh-moo",
                    illustrationEmoji = "1️⃣ 🛶",
                    xPercent = 0.20f,
                    yPercent = 0.28f
                ),
                StorybookObject(
                    id = "COUNT_2_FISH",
                    labelLukenye = "Bbiri (2)",
                    labelEnglish = "Two Fish",
                    pronunciation = "Bbee-ree",
                    illustrationEmoji = "2️⃣ 🐟",
                    xPercent = 0.75f,
                    yPercent = 0.28f
                ),
                StorybookObject(
                    id = "COUNT_3_REEDS",
                    labelLukenye = "Ssatu (3)",
                    labelEnglish = "Three Reeds",
                    pronunciation = "Ssah-too",
                    illustrationEmoji = "3️⃣ 🌾",
                    xPercent = 0.22f,
                    yPercent = 0.62f
                ),
                StorybookObject(
                    id = "COUNT_4_STONES",
                    labelLukenye = "Nnya (4)",
                    labelEnglish = "Four Stones",
                    pronunciation = "N-nyah",
                    illustrationEmoji = "4️⃣ 🪨",
                    xPercent = 0.78f,
                    yPercent = 0.62f
                ),
                StorybookObject(
                    id = "COUNT_5_BIRDS",
                    labelLukenye = "Tano (5)",
                    labelEnglish = "Five Birds",
                    pronunciation = "Tah-noh",
                    illustrationEmoji = "5️⃣ 🕊️",
                    xPercent = 0.50f,
                    yPercent = 0.45f
                )
            )
        ),

        // SPREAD 6: Colours of the Lakeside Sky
        StorybookSpread(
            pageIndex = 5,
            titleLukenye = "Langi z'Eggulu",
            titleEnglish = "Colours of the Lake Sky",
            conceptDescription = "Discover red sun, golden dawn, blue water and green reeds.",
            biome = SanctuaryBiome.RIVER_WETLANDS,
            themeStyle = StorybookThemeStyle.LAKESIDE_COLOURS,
            interactiveObjects = listOf(
                StorybookObject(
                    id = "COLOUR_MYOFU",
                    labelLukenye = "Myofu (Red)",
                    labelEnglish = "Red Sunset",
                    pronunciation = "Myoh-foo",
                    illustrationEmoji = "🔴 🌅",
                    xPercent = 0.22f,
                    yPercent = 0.30f
                ),
                StorybookObject(
                    id = "COLOUR_KIRAGALA",
                    labelLukenye = "Kiragala (Green)",
                    labelEnglish = "Green Leaf",
                    pronunciation = "Kee-rah-gah-lah",
                    illustrationEmoji = "🟢 🍃",
                    xPercent = 0.75f,
                    yPercent = 0.30f
                ),
                StorybookObject(
                    id = "COLOUR_BULURU",
                    labelLukenye = "Buluru (Blue)",
                    labelEnglish = "Blue River",
                    pronunciation = "Boo-loo-roo",
                    illustrationEmoji = "🔵 💧",
                    xPercent = 0.25f,
                    yPercent = 0.64f
                ),
                StorybookObject(
                    id = "COLOUR_KYENVO",
                    labelLukenye = "Kyenvo (Yellow)",
                    labelEnglish = "Yellow Sun",
                    pronunciation = "Kyen-voh",
                    illustrationEmoji = "🟡 ☀️",
                    xPercent = 0.75f,
                    yPercent = 0.64f
                )
            )
        ),

        // SPREAD 7: Fisherman Song & Story
        StorybookSpread(
            pageIndex = 6,
            titleLukenye = "Ennyimba z'Abalunnyanja",
            titleEnglish = "Song of the Canoe Fishermen",
            conceptDescription = "Listen to the tranquil rhythm of paddle strokes and lake lullabies.",
            biome = SanctuaryBiome.VILLAGE_LANDING,
            themeStyle = StorybookThemeStyle.FISHERMAN_SONG,
            interactiveObjects = listOf(
                StorybookObject(
                    id = "SONG_PADDLE",
                    labelLukenye = "Nkasi",
                    labelEnglish = "Wooden Paddle",
                    pronunciation = "N-kah-see",
                    illustrationEmoji = "🛶 🪵",
                    xPercent = 0.22f,
                    yPercent = 0.35f
                ),
                StorybookObject(
                    id = "SONG_DRUM",
                    labelLukenye = "Engoma",
                    labelEnglish = "Heritage Drum",
                    pronunciation = "En-goh-mah",
                    illustrationEmoji = "🪘",
                    xPercent = 0.74f,
                    yPercent = 0.35f
                ),
                StorybookObject(
                    id = "SONG_VOICE",
                    labelLukenye = "Lullaby",
                    labelEnglish = "Evening Lake Song",
                    pronunciation = "Loo-lah-bye",
                    illustrationEmoji = "🎵 🌙",
                    xPercent = 0.48f,
                    yPercent = 0.65f
                )
            )
        )
    )
}
