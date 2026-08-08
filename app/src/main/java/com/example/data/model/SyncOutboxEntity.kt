package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_outbox")
data class SyncOutboxEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val childProfileId: String,
    val entityType: String,
    val entityId: String,
    val operation: String,
    val payloadJson: String,
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val updatedAtTimestamp: Long = System.currentTimeMillis(),
    val attemptCount: Int = 0,
    val lastAttemptAtTimestamp: Long? = null,
    val nextAttemptAtTimestamp: Long? = null,
    val status: String = "PENDING",
    val lastError: String? = null
)
