package com.example.clipscore.di

import android.content.Context
import com.example.clipscore.data.local.AnalysisDao
import com.example.clipscore.data.local.ClipScoreDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ClipScoreDatabase {
        return ClipScoreDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideAnalysisDao(database: ClipScoreDatabase): AnalysisDao {
        return database.analysisDao()
    }
}
