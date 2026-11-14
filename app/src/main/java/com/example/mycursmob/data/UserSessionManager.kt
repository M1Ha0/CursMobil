package com.example.mycursmob.data

import android.content.Context
import android.content.SharedPreferences
import com.example.mycursmob.FitnessApp
import com.example.mycursmob.domain.model.User
import org.json.JSONArray
import org.json.JSONObject

object UserSessionManager {
    private const val PREF_NAME = "uplift_fitness_session"
    private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    private const val KEY_UID = "key_uid"
    private const val KEY_EMAIL = "key_email"
    private const val KEY_FULL_NAME = "key_full_name"
    private const val KEY_USER_NAME = "key_user_name"
    private const val KEY_PHONE = "key_phone"
    private const val KEY_IS_ASSESSMENT_COMPLETED = "key_is_assessment_completed"
    private const val KEY_AGE = "key_age"
    private const val KEY_WEIGHT_KG = "key_weight_kg"
    private const val KEY_IS_KG = "key_is_kg"
    private const val KEY_FITNESS_LEVEL = "key_fitness_level"
    private const val KEY_FITNESS_LEVEL_NAME = "key_fitness_level_name"
    private const val KEY_GENDER = "key_gender"
    private const val KEY_GOALS = "key_goals"
    private const val KEY_AVATAR_PATH = "key_avatar_path"
    private const val KEY_REGISTERED_USERS_JSON = "key_registered_users_json"
    private const val KEY_USER_MEALS_PREFIX = "key_user_meals_"
    private const val KEY_USER_WATER_PREFIX = "key_user_water_"

    private val prefs: SharedPreferences by lazy {
        FitnessApp.instance.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun isAssessmentCompleted(): Boolean = prefs.getBoolean(KEY_IS_ASSESSMENT_COMPLETED, false)

    fun getCurrentUser(): User? {
        if (!isLoggedIn()) return null
        val uid = prefs.getString(KEY_UID, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        if (uid.isEmpty() && email.isEmpty()) return null

        val goalsSet = prefs.getStringSet(KEY_GOALS, emptySet()) ?: emptySet()
        return User(
            uid = uid,
            email = email,
            fullName = prefs.getString(KEY_FULL_NAME, "") ?: "",
            userName = prefs.getString(KEY_USER_NAME, "") ?: "",
            phone = prefs.getString(KEY_PHONE, "") ?: "",
            isAssessmentCompleted = prefs.getBoolean(KEY_IS_ASSESSMENT_COMPLETED, false),
            age = prefs.getInt(KEY_AGE, 19),
            weightKg = prefs.getInt(KEY_WEIGHT_KG, 62),
            isKgSelected = prefs.getBoolean(KEY_IS_KG, true),
            fitnessLevel = prefs.getInt(KEY_FITNESS_LEVEL, 3),
            fitnessLevelName = prefs.getString(KEY_FITNESS_LEVEL_NAME, "Somewhat Athletic") ?: "Somewhat Athletic",
            gender = prefs.getString(KEY_GENDER, "Female") ?: "Female",
            fitnessGoals = goalsSet.toList(),
            avatarPath = prefs.getString(KEY_AVATAR_PATH, "") ?: ""
        )
    }

    fun saveSession(user: User) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_UID, user.uid)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_FULL_NAME, user.fullName)
            .putString(KEY_USER_NAME, user.userName)
            .putString(KEY_PHONE, user.phone)
            .putBoolean(KEY_IS_ASSESSMENT_COMPLETED, user.isAssessmentCompleted)
            .putInt(KEY_AGE, user.age)
            .putInt(KEY_WEIGHT_KG, user.weightKg)
            .putBoolean(KEY_IS_KG, user.isKgSelected)
            .putInt(KEY_FITNESS_LEVEL, user.fitnessLevel)
            .putString(KEY_FITNESS_LEVEL_NAME, user.fitnessLevelName)
            .putString(KEY_GENDER, user.gender)
            .putStringSet(KEY_GOALS, user.fitnessGoals.toSet())
            .putString(KEY_AVATAR_PATH, user.avatarPath)
            .apply()
    }

    fun saveAvatar(avatarPath: String): User {
        val current = getCurrentUser() ?: User()
        val updated = current.copy(avatarPath = avatarPath)
        saveSession(updated)
        if (updated.email.isNotEmpty()) {
            saveRegisteredUser(updated.email, getRegisteredUserPassword(updated.email), updated)
        }
        return updated
    }

    fun saveAssessment(
        age: Int,
        weightKg: Int,
        isKgSelected: Boolean,
        fitnessLevel: Int,
        fitnessLevelName: String,
        gender: String,
        fitnessGoals: List<String>,
        avatarPath: String = ""
    ): User {
        val current = getCurrentUser() ?: User()
        val updated = current.copy(
            isAssessmentCompleted = true,
            age = age,
            weightKg = weightKg,
            isKgSelected = isKgSelected,
            fitnessLevel = fitnessLevel,
            fitnessLevelName = fitnessLevelName,
            gender = gender,
            fitnessGoals = fitnessGoals,
            avatarPath = if (avatarPath.isNotEmpty()) avatarPath else current.avatarPath
        )
        saveSession(updated)
        if (updated.email.isNotEmpty()) {
            saveRegisteredUser(updated.email, getRegisteredUserPassword(updated.email), updated)
        }
        return updated
    }

    fun saveRegisteredUser(email: String, pass: String, user: User) {
        try {
            val jsonStr = prefs.getString(KEY_REGISTERED_USERS_JSON, "{}") ?: "{}"
            val jsonObj = JSONObject(jsonStr)
            val userObj = JSONObject().apply {
                put("password", pass)
                put("uid", user.uid)
                put("email", user.email)
                put("fullName", user.fullName)
                put("userName", user.userName)
                put("phone", user.phone)
                put("isAssessmentCompleted", user.isAssessmentCompleted)
                put("age", user.age)
                put("weightKg", user.weightKg)
                put("isKgSelected", user.isKgSelected)
                put("fitnessLevel", user.fitnessLevel)
                put("fitnessLevelName", user.fitnessLevelName)
                put("gender", user.gender)
                put("fitnessGoals", JSONArray(user.fitnessGoals))
                put("avatarPath", user.avatarPath)
            }
            jsonObj.put(email.lowercase(), userObj)
            prefs.edit().putString(KEY_REGISTERED_USERS_JSON, jsonObj.toString()).apply()
        } catch (_: Exception) {}
    }

    fun getRegisteredUser(email: String): Pair<String, User>? {
        try {
            val jsonStr = prefs.getString(KEY_REGISTERED_USERS_JSON, "{}") ?: "{}"
            val jsonObj = JSONObject(jsonStr)
            val lower = email.lowercase()
            if (!jsonObj.has(lower)) return null
            val obj = jsonObj.getJSONObject(lower)
            val pass = obj.optString("password", "")
            val goalsArray = obj.optJSONArray("fitnessGoals")
            val goalsList = mutableListOf<String>()
            if (goalsArray != null) {
                for (i in 0 until goalsArray.length()) {
                    goalsList.add(goalsArray.getString(i))
                }
            }
            val user = User(
                uid = obj.optString("uid", ""),
                email = obj.optString("email", ""),
                fullName = obj.optString("fullName", ""),
                userName = obj.optString("userName", ""),
                phone = obj.optString("phone", ""),
                isAssessmentCompleted = obj.optBoolean("isAssessmentCompleted", false),
                age = obj.optInt("age", 19),
                weightKg = obj.optInt("weightKg", 62),
                isKgSelected = obj.optBoolean("isKgSelected", true),
                fitnessLevel = obj.optInt("fitnessLevel", 3),
                fitnessLevelName = obj.optString("fitnessLevelName", "Somewhat Athletic"),
                gender = obj.optString("gender", "Female"),
                fitnessGoals = goalsList,
                avatarPath = obj.optString("avatarPath", "")
            )
            return pass to user
        } catch (_: Exception) {
            return null
        }
    }

    private fun getRegisteredUserPassword(email: String): String {
        return getRegisteredUser(email)?.first ?: ""
    }

    fun saveUserMeals(userId: String, mealsJson: String) {
        if (userId.isEmpty()) return
        prefs.edit().putString(KEY_USER_MEALS_PREFIX + userId, mealsJson).apply()
    }

    fun getUserMeals(userId: String): String {
        if (userId.isEmpty()) return "{}"
        return prefs.getString(KEY_USER_MEALS_PREFIX + userId, "{}") ?: "{}"
    }

    fun saveUserWater(userId: String, dateKey: String, amountMl: Int) {
        if (userId.isEmpty() || dateKey.isEmpty()) return
        val key = "${KEY_USER_WATER_PREFIX}${userId}_${dateKey}"
        prefs.edit().putInt(key, amountMl).apply()
    }

    fun getUserWater(userId: String, dateKey: String): Int {
        if (userId.isEmpty() || dateKey.isEmpty()) return 0
        val key = "${KEY_USER_WATER_PREFIX}${userId}_${dateKey}"
        return prefs.getInt(key, 0)
    }

    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
}
