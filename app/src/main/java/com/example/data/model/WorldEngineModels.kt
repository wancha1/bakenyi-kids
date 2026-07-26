package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room Entities for World Engine & Fishing Area
@Entity(tableName = "vocabulary_items")
data class VocabularyEntity(
    @PrimaryKey val id: String,
    val locationId: String,
    val lugandaTerm: String,
    val englishMeaning: String,
    val phonetic: String,
    val audioPath: String = "",
    val culturalFact: String = ""
)

@Entity(tableName = "child_discoveries")
data class ChildDiscoveryEntity(
    @PrimaryKey val id: String,
    val childProfileId: String = "1",
    val locationKey: String,
    val itemKey: String,
    val discoveredAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "location_progress")
data class LocationProgressEntity(
    @PrimaryKey val locationId: String,
    val childProfileId: String = "1",
    val termsMastered: Int = 0,
    val starsEarned: Int = 0,
    val isCompleted: Boolean = false
)

// Sealed State Machine for Child Location Journey
sealed class LocationWorldState {
    data object Arrival : LocationWorldState()
    data object Discovery : LocationWorldState()
    data object Learning : LocationWorldState()
    data object Practice : LocationWorldState()
    data object Challenge : LocationWorldState()
    data object Reward : LocationWorldState()
}

// Domain Models
data class VocabularyItem(
    val id: String,
    val lugandaTerm: String,
    val englishMeaning: String,
    val phonetic: String,
    val iconEmoji: String = "🐟",
    val culturalFact: String = "",
    val audioResName: String = ""
)

data class HeritageLocation(
    val id: String,
    val name: String,
    val subtitle: String,
    val backgroundDesc: String,
    val discoveries: List<VocabularyItem>
)
