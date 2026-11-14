package com.example.mycursmob.presentation.assessment

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AssessmentUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 6,
    val age: Int = 19,
    val weightKg: Int = 62,
    val isKgSelected: Boolean = true,
    val fitnessLevel: Int = 3,
    val gender: String = "Female",
    val fitnessGoals: List<String> = listOf("🤖  I wanna try AI Coach"),
    val avatarPath: String = "premade_bunny"
)

class AssessmentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()

    fun onAgeChange(age: Int) {
        _uiState.update { it.copy(age = age) }
    }

    fun onWeightChange(weight: Int) {
        _uiState.update { it.copy(weightKg = weight) }
    }

    fun onUnitToggle(isKg: Boolean) {
        _uiState.update { it.copy(isKgSelected = isKg) }
    }

    fun onFitnessLevelChange(level: Int) {
        _uiState.update { it.copy(fitnessLevel = level.coerceIn(1, 5)) }
    }

    fun onGenderSelect(gender: String) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onAvatarChange(path: String) {
        _uiState.update { it.copy(avatarPath = path) }
    }

    fun onGoalToggle(goal: String) {
        _uiState.update { state ->
            val goals = state.fitnessGoals.toMutableList()
            if (goals.contains(goal)) goals.remove(goal) else goals.add(goal)
            state.copy(fitnessGoals = goals)
        }
    }

    fun nextStep() {
        _uiState.update {
            if (it.currentStep < it.totalSteps) it.copy(currentStep = it.currentStep + 1)
            else it
        }
    }

    fun previousStep() {
        _uiState.update {
            if (it.currentStep > 1) it.copy(currentStep = it.currentStep - 1)
            else it
        }
    }

    fun isLastStep(): Boolean = _uiState.value.currentStep == _uiState.value.totalSteps

    fun getFitnessLevelName(): String {
        return when (_uiState.value.fitnessLevel) {
            1 -> "Beginner"
            2 -> "Lightly Active"
            3 -> "Somewhat Athletic"
            4 -> "Very Athletic"
            5 -> "Professional"
            else -> "Somewhat Athletic"
        }
    }
}
