package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Kato's Journey",
    val level: Int = 4,
    val stars: Int = 125,
    val coins: Int = 450,
    val streakDays: Int = 3,
    val guideAvatar: String = "🦒",
    val currentWorld: Int = 1,
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val updatedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "worlds")
data class World(
    @PrimaryKey val worldId: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = true,
    val totalLessons: Int = 4
)

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val lessonId: String,
    val worldId: Int,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val isLocked: Boolean = false,
    val starReward: Int = 3
)

@Entity(tableName = "phrases")
data class Phrase(
    @PrimaryKey val id: String,
    val worldId: Int,
    val bakenyeText: String,
    val englishText: String,
    val pronunciation: String,
    val iconEmoji: String,
    val category: String,
    val audioNote: String
)

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String
)

@Entity(
    tableName = "child_lesson_progress",
    primaryKeys = ["childProfileId", "lessonId"]
)
data class ChildLessonProgressEntity(
    val childProfileId: String,
    val lessonId: String,
    val isCompleted: Boolean = true,
    val updatedAtTimestamp: Long = System.currentTimeMillis(),
    val completedAtTimestamp: Long? = System.currentTimeMillis()
)

@Entity(
    tableName = "child_badge_unlocks",
    primaryKeys = ["childProfileId", "badgeId"]
)
data class ChildBadgeUnlockEntity(
    val childProfileId: String,
    val badgeId: String,
    val isUnlocked: Boolean = true,
    val updatedAtTimestamp: Long = System.currentTimeMillis(),
    val unlockedAtTimestamp: Long? = System.currentTimeMillis()
)

data class LessonWithProgress(
    val lesson: Lesson,
    val isCompleted: Boolean
) {
    val lessonId: String get() = lesson.lessonId
    val worldId: Int get() = lesson.worldId
    val title: String get() = lesson.title
    val subtitle: String get() = lesson.subtitle
    val iconEmoji: String get() = lesson.iconEmoji
    val isLocked: Boolean get() = lesson.isLocked
    val starReward: Int get() = lesson.starReward
}

data class BadgeWithProgress(
    val badge: Badge,
    val isUnlocked: Boolean
) {
    val id: String get() = badge.id
    val title: String get() = badge.title
    val description: String get() = badge.description
    val iconEmoji: String get() = badge.iconEmoji
}
