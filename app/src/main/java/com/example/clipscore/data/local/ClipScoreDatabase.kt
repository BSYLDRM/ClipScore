package com.example.clipscore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AnalysisEntity::class], version = 4)
abstract class ClipScoreDatabase : RoomDatabase() {
    abstract fun analysisDao(): AnalysisDao

    companion object {
        @Volatile private var INSTANCE: ClipScoreDatabase? = null

        fun getDatabase(context: Context): ClipScoreDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ClipScoreDatabase::class.java,
                    "clipscore_database"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
        }
    }
}
