package com.example.clipscore.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.clipscore.data.local.AnalysisDao
import com.example.clipscore.data.local.AnalysisEntity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val analysisDao: AnalysisDao
) : ViewModel() {
    private val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val recentAnalyses: Flow<List<AnalysisEntity>> = analysisDao.getRecentAnalyses(currentUserEmail)
}
