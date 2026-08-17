package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persistent Sanctuary State stored in local Room DB.
 * Guarantees that active biome and diurnal time phase are remembered across app restarts.
 */
@Entity(tableName = "sanctuary_state")
data class SanctuaryStateEntity(
    @PrimaryKey val id: String = "PRIMARY_SANCTUARY",
    val activeBiome: String = "RIVER_WETLANDS",
    val timeOfDay: String = "MORNING_DAWN",
    val updatedAtTimestamp: Long = System.currentTimeMillis()
)
