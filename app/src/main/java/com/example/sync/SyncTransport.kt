package com.example.sync

import com.example.data.model.SyncOutboxEntity

enum class SyncResultStatus {
    SUCCESS,
    RETRYABLE_FAILURE,
    PERMANENT_FAILURE
}

data class SyncResult(
    val status: SyncResultStatus,
    val errorMessage: String? = null
)

interface SyncTransport {
    suspend fun push(event: SyncOutboxEntity): SyncResult
}

class FakeSyncTransport(
    private val defaultStatus: SyncResultStatus = SyncResultStatus.SUCCESS,
    private val defaultError: String? = null
) : SyncTransport {

    private val customResponses = mutableMapOf<String, SyncResult>()
    val pushedEvents = mutableListOf<SyncOutboxEntity>()

    fun setResponseForEvent(eventId: String, result: SyncResult) {
        customResponses[eventId] = result
    }

    fun setResponseForEntity(entityId: String, result: SyncResult) {
        customResponses["entity:$entityId"] = result
    }

    fun clear() {
        customResponses.clear()
        pushedEvents.clear()
    }

    override suspend fun push(event: SyncOutboxEntity): SyncResult {
        pushedEvents.add(event)
        val custom = customResponses[event.id] ?: customResponses["entity:${event.entityId}"]
        return custom ?: SyncResult(defaultStatus, defaultError)
    }
}
