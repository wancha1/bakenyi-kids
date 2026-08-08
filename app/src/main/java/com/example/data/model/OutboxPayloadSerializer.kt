package com.example.data.model

import org.json.JSONObject

object OutboxPayloadSerializer {

    fun serializeUserProfile(profile: UserProfile): String {
        val json = JSONObject()
        json.put("id", profile.id)
        json.put("name", profile.name)
        json.put("level", profile.level)
        json.put("stars", profile.stars)
        json.put("coins", profile.coins)
        json.put("streakDays", profile.streakDays)
        json.put("guideAvatar", profile.guideAvatar)
        json.put("currentWorld", profile.currentWorld)
        json.put("createdAtTimestamp", profile.createdAtTimestamp)
        json.put("updatedAtTimestamp", profile.updatedAtTimestamp)
        return json.toString()
    }

    fun serializeLocationProgress(progress: LocationProgressEntity): String {
        val json = JSONObject()
        json.put("childProfileId", progress.childProfileId)
        json.put("locationId", progress.locationId)
        json.put("termsMastered", progress.termsMastered)
        json.put("starsEarned", progress.starsEarned)
        json.put("isCompleted", progress.isCompleted)
        json.put("updatedAtTimestamp", progress.updatedAtTimestamp)
        if (progress.completedAtTimestamp != null) {
            json.put("completedAtTimestamp", progress.completedAtTimestamp)
        } else {
            json.put("completedAtTimestamp", JSONObject.NULL)
        }
        return json.toString()
    }

    fun serializeLessonProgress(progress: ChildLessonProgressEntity): String {
        val json = JSONObject()
        json.put("childProfileId", progress.childProfileId)
        json.put("lessonId", progress.lessonId)
        json.put("isCompleted", progress.isCompleted)
        json.put("updatedAtTimestamp", progress.updatedAtTimestamp)
        if (progress.completedAtTimestamp != null) {
            json.put("completedAtTimestamp", progress.completedAtTimestamp)
        } else {
            json.put("completedAtTimestamp", JSONObject.NULL)
        }
        return json.toString()
    }

    fun serializeBadgeUnlock(unlock: ChildBadgeUnlockEntity): String {
        val json = JSONObject()
        json.put("childProfileId", unlock.childProfileId)
        json.put("badgeId", unlock.badgeId)
        json.put("isUnlocked", unlock.isUnlocked)
        json.put("updatedAtTimestamp", unlock.updatedAtTimestamp)
        if (unlock.unlockedAtTimestamp != null) {
            json.put("unlockedAtTimestamp", unlock.unlockedAtTimestamp)
        } else {
            json.put("unlockedAtTimestamp", JSONObject.NULL)
        }
        return json.toString()
    }

    fun serializeDiscovery(discovery: ChildDiscoveryEntity): String {
        val json = JSONObject()
        json.put("childProfileId", discovery.childProfileId)
        json.put("locationKey", discovery.locationKey)
        json.put("itemKey", discovery.itemKey)
        json.put("discoveredAtTimestamp", discovery.discoveredAtTimestamp)
        json.put("updatedAtTimestamp", discovery.updatedAtTimestamp)
        return json.toString()
    }
}
