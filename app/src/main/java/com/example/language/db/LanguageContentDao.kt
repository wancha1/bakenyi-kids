package com.example.language.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.language.model.LanguageContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageContentDao {

    @Query("SELECT * FROM language_content WHERE active = 1 AND verificationStatus = 'VERIFIED'")
    fun getVerifiedLanguageContent(): Flow<List<LanguageContentEntity>>

    @Query("SELECT * FROM language_content WHERE active = 1 AND verificationStatus = 'VERIFIED'")
    suspend fun getVerifiedLanguageContentOnce(): List<LanguageContentEntity>

    @Query("SELECT * FROM language_content ORDER BY contentCategory, contentId")
    fun getAllLanguageContent(): Flow<List<LanguageContentEntity>>

    @Query("SELECT * FROM language_content WHERE contentId = :contentId")
    suspend fun getContentById(contentId: String): LanguageContentEntity?

    @Query("SELECT * FROM language_content WHERE contentCategory = :category AND active = 1")
    fun getContentByCategory(category: String): Flow<List<LanguageContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBatch(items: List<LanguageContentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: LanguageContentEntity)

    @Query("UPDATE language_content SET verificationStatus = :status, source = :sourceRef, updatedAtTimestamp = :updatedAt WHERE contentId = :contentId")
    suspend fun updateVerificationStatus(contentId: String, status: String, sourceRef: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM language_content")
    suspend fun getContentCount(): Int

    @Query("SELECT COUNT(*) FROM language_content WHERE verificationStatus = 'VERIFIED'")
    suspend fun getVerifiedCount(): Int
}
