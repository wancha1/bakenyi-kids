package com.example.language.repository

import com.example.language.db.LanguageContentDao
import com.example.language.enforcer.VerificationStateEnforcer
import com.example.language.model.LanguageContent
import com.example.language.model.LanguageContentEntity
import com.example.language.model.VerificationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class ContentImportSummary(
    val totalImported: Int,
    val verifiedCount: Int,
    val pendingCount: Int,
    val rejectedCount: Int,
    val errors: List<String>
)

interface LanguageRepository {
    fun getVerifiedContentForChild(): Flow<List<LanguageContent>>
    fun getAllContentForDev(): Flow<List<LanguageContent>>
    fun getContentByCategoryForChild(category: String): Flow<List<LanguageContent>>
    suspend fun getContentById(contentId: String): LanguageContent?
    suspend fun importVerifiedHumanBatch(items: List<LanguageContent>, verifierRef: String): ContentImportSummary
    suspend fun updateVerificationStatus(contentId: String, status: VerificationStatus, verifierRef: String?): Boolean
    suspend fun getContentStats(): Pair<Int, Int> // (Total, Verified)
}

class RoomLanguageRepository(
    private val dao: LanguageContentDao
) : LanguageRepository {

    override fun getVerifiedContentForChild(): Flow<List<LanguageContent>> {
        return dao.getVerifiedLanguageContent().map { entities ->
            entities.map { it.toDomainModel() }
                .filter { VerificationStateEnforcer.isAllowedInChildExperience(it) }
        }
    }

    override fun getAllContentForDev(): Flow<List<LanguageContent>> {
        return dao.getAllLanguageContent().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getContentByCategoryForChild(category: String): Flow<List<LanguageContent>> {
        return dao.getContentByCategory(category).map { entities ->
            entities.map { it.toDomainModel() }
                .filter { VerificationStateEnforcer.isAllowedInChildExperience(it) }
        }
    }

    override suspend fun getContentById(contentId: String): LanguageContent? {
        return dao.getContentById(contentId)?.toDomainModel()
    }

    override suspend fun importVerifiedHumanBatch(
        items: List<LanguageContent>,
        verifierRef: String
    ): ContentImportSummary {
        val errors = mutableListOf<String>()
        var verifiedCount = 0
        var pendingCount = 0
        var rejectedCount = 0

        val entitiesToInsert = items.map { item ->
            // Enforce source attribution and audit trail
            val finalSource = item.source ?: verifierRef
            val updatedItem = item.copy(source = finalSource)

            when (updatedItem.verificationStatus) {
                VerificationStatus.VERIFIED -> verifiedCount++
                VerificationStatus.PENDING_VERIFICATION -> pendingCount++
                VerificationStatus.REJECTED -> rejectedCount++
                else -> {}
            }

            LanguageContentEntity.fromDomainModel(updatedItem)
        }

        dao.insertOrUpdateBatch(entitiesToInsert)

        return ContentImportSummary(
            totalImported = items.size,
            verifiedCount = verifiedCount,
            pendingCount = pendingCount,
            rejectedCount = rejectedCount,
            errors = errors
        )
    }

    override suspend fun updateVerificationStatus(
        contentId: String,
        status: VerificationStatus,
        verifierRef: String?
    ): Boolean {
        dao.updateVerificationStatus(contentId, status.name, verifierRef)
        return true
    }

    override suspend fun getContentStats(): Pair<Int, Int> {
        val total = dao.getContentCount()
        val verified = dao.getVerifiedCount()
        return Pair(total, verified)
    }
}
