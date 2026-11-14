package com.example.mycursmob.domain.model

data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val userName: String = "",
    val phone: String = "",
    val isAssessmentCompleted: Boolean = false,
    val age: Int = 0,
    val weightKg: Int = 0,
    val isKgSelected: Boolean = true,
    val fitnessLevel: Int = 0,
    val fitnessLevelName: String = "",
    val gender: String = "",
    val fitnessGoals: List<String> = emptyList()
)
