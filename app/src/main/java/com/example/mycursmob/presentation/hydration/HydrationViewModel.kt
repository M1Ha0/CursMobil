package com.example.mycursmob.presentation.hydration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycursmob.data.UserSessionManager
import com.example.mycursmob.domain.FirebaseModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HydrationUiState(
    val userId: String = "",
    val dateKey: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val currentWaterMl: Int = 0,
    val goalWaterMl: Int = 2000
) {
    val remainingWaterMl: Int
        get() = (goalWaterMl - currentWaterMl).coerceAtLeast(0)

    val progressFraction: Float
        get() = (currentWaterMl.toFloat() / goalWaterMl.toFloat()).coerceIn(0f, 1f)
}

class HydrationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState: StateFlow<HydrationUiState> = _uiState.asStateFlow()

    init {
        val currentUser = UserSessionManager.getCurrentUser()
        val uid = currentUser?.uid?.ifEmpty { null } ?: currentUser?.email ?: "guest"
        initUser(uid)
    }

    fun initUser(userId: String) {
        val finalUserId = userId.ifEmpty { "guest" }
        val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

        // Load today's water amount (defaults to 0 for a new day)
        val savedAmount = UserSessionManager.getUserWater(finalUserId, todayKey)
        _uiState.value = _uiState.value.copy(
            userId = finalUserId,
            dateKey = todayKey,
            currentWaterMl = savedAmount
        )

        // Sync with Firestore in background
        if (finalUserId.isNotEmpty() && finalUserId != "guest") {
            viewModelScope.launch {
                try {
                    val snapshot = withTimeoutOrNull(2500L) {
                        FirebaseModule.firestore
                            .collection("persons")
                            .document(finalUserId)
                            .collection("hydration")
                            .document(todayKey)
                            .get()
                            .await()
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val firestoreAmount = snapshot.getLong("amountMl")?.toInt() ?: savedAmount
                        val maxAmount = maxOf(savedAmount, firestoreAmount)
                        _uiState.value = _uiState.value.copy(currentWaterMl = maxAmount)
                        UserSessionManager.saveUserWater(finalUserId, todayKey, maxAmount)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun addWater(amountMl: Int = 250) {
        val current = _uiState.value
        val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

        // Check if day changed while app was open
        val isNewDay = current.dateKey != todayKey
        val baseAmount = if (isNewDay) 0 else current.currentWaterMl
        val newAmount = baseAmount + amountMl

        _uiState.value = current.copy(
            dateKey = todayKey,
            currentWaterMl = newAmount
        )

        // 1. Save locally per-user and per-day (auto resets on new day)
        UserSessionManager.saveUserWater(current.userId, todayKey, newAmount)

        // 2. Sync to Firestore
        val uid = current.userId
        if (uid.isNotEmpty() && uid != "guest") {
            viewModelScope.launch {
                try {
                    withTimeoutOrNull(2500L) {
                        FirebaseModule.firestore
                            .collection("persons")
                            .document(uid)
                            .collection("hydration")
                            .document(todayKey)
                            .set(
                                mapOf(
                                    "dateKey" to todayKey,
                                    "amountMl" to newAmount,
                                    "goalMl" to current.goalWaterMl,
                                    "updatedAt" to System.currentTimeMillis()
                                )
                            )
                            .await()
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun resetWater() {
        val current = _uiState.value
        val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

        _uiState.value = current.copy(currentWaterMl = 0)
        UserSessionManager.saveUserWater(current.userId, todayKey, 0)

        val uid = current.userId
        if (uid.isNotEmpty() && uid != "guest") {
            viewModelScope.launch {
                try {
                    withTimeoutOrNull(2500L) {
                        FirebaseModule.firestore
                            .collection("persons")
                            .document(uid)
                            .collection("hydration")
                            .document(todayKey)
                            .set(mapOf("dateKey" to todayKey, "amountMl" to 0))
                            .await()
                    }
                } catch (_: Exception) {}
            }
        }
    }
}
