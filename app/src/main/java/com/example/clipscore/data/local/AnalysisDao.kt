package com.example.clipscore.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analyses WHERE userEmail = :userEmail ORDER BY createdAt DESC LIMIT 10")
    fun getRecentAnalyses(userEmail: String): Flow<List<AnalysisEntity>>

    @Insert
    suspend fun insertAnalysis(analysis: AnalysisEntity)

    @Query("DELETE FROM analyses WHERE userEmail = :userEmail AND id NOT IN (SELECT id FROM analyses WHERE userEmail = :userEmail ORDER BY createdAt DESC LIMIT 10)")
    suspend fun keepOnlyLast10(userEmail: String)

    @Query("SELECT * FROM analyses WHERE id = :id")
    suspend fun getAnalysisById(id: Int): AnalysisEntity?
}
