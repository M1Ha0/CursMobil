package com.example.mycursmob.presentation.nutrition

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
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MealUiState(
    val userId: String = "",
    val selectedDateKey: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val selectedCategory: MealCategory = MealCategory.BREAKFAST,
    // Meals mapped strictly by key: "${dateKey}_${categoryName}"
    val mealsMap: Map<String, List<MealItem>> = emptyMap()
) {
    val currentKey: String
        get() = "${selectedDateKey}_${selectedCategory.name}"

    val currentMeals: List<MealItem>
        get() = mealsMap[currentKey] ?: emptyList()

    val todayTotalCalories: Int
        get() {
            val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
            return MealCategory.entries.sumOf { cat ->
                (mealsMap["${todayKey}_${cat.name}"] ?: emptyList()).sumOf { it.calories }
            }
        }
}

class MealViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MealUiState())
    val uiState: StateFlow<MealUiState> = _uiState.asStateFlow()

    init {
        val currentUser = UserSessionManager.getCurrentUser()
        val uid = currentUser?.uid?.ifEmpty { null } ?: currentUser?.email ?: "guest"
        initUser(uid)
    }

    fun initUser(userId: String) {
        val finalUserId = userId.ifEmpty { "guest" }
        _uiState.value = _uiState.value.copy(userId = finalUserId)

        // 1. Instantly load locally saved meals for this account from SharedPreferences
        loadMealsFromLocalStorage(finalUserId)

        // 2. Fetch and synchronize with Firestore in background (if configured and online)
        if (finalUserId.isNotEmpty() && finalUserId != "guest") {
            syncWithFirestore(finalUserId)
        }
    }

    fun selectDate(dateKey: String) {
        _uiState.value = _uiState.value.copy(selectedDateKey = dateKey)
    }

    fun selectCategory(category: MealCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun addMeal(meal: MealItem) {
        val current = _uiState.value
        val dateKey = current.selectedDateKey
        val categoryName = current.selectedCategory.name
        val key = "${dateKey}_${categoryName}"

        val currentList = current.mealsMap[key]?.toMutableList() ?: mutableListOf()
        currentList.add(meal)

        val updatedMap = current.mealsMap.toMutableMap()
        updatedMap[key] = currentList
        _uiState.value = current.copy(mealsMap = updatedMap)

        // Save locally per-account
        saveMealsToLocalStorage(current.userId, updatedMap)

        // Save to Firestore under persons/{userId}/meals/{mealId}
        val uid = current.userId
        if (uid.isNotEmpty() && uid != "guest") {
            viewModelScope.launch {
                try {
                    withTimeoutOrNull(2500L) {
                        val map = meal.toFirestoreMap(dateKey, categoryName, uid)
                        FirebaseModule.firestore
                            .collection("persons")
                            .document(uid)
                            .collection("meals")
                            .document(meal.id)
                            .set(map)
                            .await()
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun deleteMeal(mealId: String) {
        val current = _uiState.value
        val key = current.currentKey
        val currentList = current.mealsMap[key]?.filter { it.id != mealId } ?: emptyList()

        val updatedMap = current.mealsMap.toMutableMap()
        updatedMap[key] = currentList
        _uiState.value = current.copy(mealsMap = updatedMap)

        // Update local storage
        saveMealsToLocalStorage(current.userId, updatedMap)

        // Delete from Firestore
        val uid = current.userId
        if (uid.isNotEmpty() && uid != "guest") {
            viewModelScope.launch {
                try {
                    withTimeoutOrNull(2500L) {
                        FirebaseModule.firestore
                            .collection("persons")
                            .document(uid)
                            .collection("meals")
                            .document(mealId)
                            .delete()
                            .await()
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun loadMealsFromLocalStorage(userId: String) {
        try {
            val jsonStr = UserSessionManager.getUserMeals(userId)
            val jsonObj = JSONObject(jsonStr)
            val resultMap = mutableMapOf<String, List<MealItem>>()

            val keys = jsonObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val array = jsonObj.getJSONArray(key)
                val itemList = mutableListOf<MealItem>()
                for (i in 0 until array.length()) {
                    itemList.add(MealItem.fromJson(array.getJSONObject(i)))
                }
                resultMap[key] = itemList
            }
            _uiState.value = _uiState.value.copy(mealsMap = resultMap)
        } catch (_: Exception) {}
    }

    private fun saveMealsToLocalStorage(userId: String, map: Map<String, List<MealItem>>) {
        try {
            val jsonObj = JSONObject()
            for ((key, list) in map) {
                val array = JSONArray()
                for (item in list) {
                    array.put(item.toJson())
                }
                jsonObj.put(key, array)
            }
            UserSessionManager.saveUserMeals(userId, jsonObj.toString())
        } catch (_: Exception) {}
    }

    private fun syncWithFirestore(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = withTimeoutOrNull(3000L) {
                    FirebaseModule.firestore
                        .collection("persons")
                        .document(userId)
                        .collection("meals")
                        .get()
                        .await()
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val firestoreMap = mutableMapOf<String, MutableList<MealItem>>()
                    for (doc in snapshot.documents) {
                        val pair = MealItem.fromFirestoreDoc(doc)
                        if (pair != null) {
                            val list = firestoreMap.getOrPut(pair.first) { mutableListOf() }
                            list.add(pair.second)
                        }
                    }

                    // Merge firestore meals with local meals
                    val mergedMap = _uiState.value.mealsMap.toMutableMap()
                    for ((k, fList) in firestoreMap) {
                        val existing = mergedMap[k]?.toMutableList() ?: mutableListOf()
                        for (fItem in fList) {
                            if (existing.none { it.id == fItem.id }) {
                                existing.add(fItem)
                            }
                        }
                        mergedMap[k] = existing
                    }

                    _uiState.value = _uiState.value.copy(mealsMap = mergedMap)
                    saveMealsToLocalStorage(userId, mergedMap)
                }
            } catch (_: Exception) {}
        }
    }
}
