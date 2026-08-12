package com.example.ui.sanctuary

import com.example.language.model.LanguageContent
import com.example.language.model.VerificationStatus

/**
 * Diurnal Time Cycle for the Living Sanctuary.
 * Shifts atmospheric lighting, audio soundscapes, and animal behaviors.
 */
enum class DiurnalTimeOfDay(val title: String, val description: String) {
    MORNING_DAWN("Morning Dawn", "Mist rises over papyrus reeds as morning birds wake."),
    MIDDAY_SUN("Midday Sun", "Warm sunlight glitters on river waves and dragonfly wings."),
    EVENING_GOLD("Evening Gold", "Golden sunlight filters through baobab leaves; hearth smoke rises."),
    TWILIGHT_DUSK("Twilight Dusk", "Fireflies emerge along papyrus banks as crickets sing.");

    fun next(): DiurnalTimeOfDay = when (this) {
        MORNING_DAWN -> MIDDAY_SUN
        MIDDAY_SUN -> EVENING_GOLD
        EVENING_GOLD -> TWILIGHT_DUSK
        TWILIGHT_DUSK -> MORNING_DAWN
    }
}

/**
 * Natural Ecosystem Biomes across the connected Sanctuary landscape.
 */
enum class SanctuaryBiome(
    val id: String,
    val title: String,
    val description: String,
    val ambientAudioCue: String
) {
    HILLS_AND_MEADOWS(
        id = "MEADOWS",
        title = "The Misty Hills & Meadows",
        description = "Grassland trails, grazing cattle, wild flowers & wind breezes.",
        ambientAudioCue = "audio_ambient_meadow_wind"
    ),
    BAOBAB_FOREST(
        id = "FOREST",
        title = "Ancient Baobab & Forest Grove",
        description = "Shaded canopy, medicinal herbs, weaver bird nests & oral lore.",
        ambientAudioCue = "audio_ambient_forest_birds"
    ),
    VILLAGE_LANDING(
        id = "VILLAGE",
        title = "Village & Canoe Landing (Ekiziba)",
        description = "Clay homesteads, hearth smoke, wooden canoes & woven baskets.",
        ambientAudioCue = "audio_ambient_village_hearth"
    ),
    RIVER_WETLANDS(
        id = "WETLANDS",
        title = "River, Papyrus Reeds & Wetlands",
        description = "Flowing lake waters, lily pads, otters, tilapia & water ripples.",
        ambientAudioCue = "audio_ambient_river_water"
    )
}

/**
 * Sanctuary Inhabitant (Creature or Community Member) in the Living Ecosystem.
 */
data class SanctuaryInhabitant(
    val id: String,
    val name: String,
    val nativeTitle: String,
    val emojiIcon: String,
    val biome: SanctuaryBiome,
    val naturalBehaviorDesc: String,
    val nativeAudioPhrase: String,
    val englishMeaning: String,
    val pronunciation: String,
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED
)

/**
 * Natural Sanctuary Discovery Record stored in persistent memory.
 */
data class NaturalDiscoveryRecord(
    val contentId: String,
    val primaryText: String,
    val secondaryMeaning: String?,
    val audioAssetPath: String?,
    val isVerified: Boolean,
    val discoveredTimestamp: Long = System.currentTimeMillis()
)
