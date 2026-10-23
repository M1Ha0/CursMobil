package com.example.mycursmob.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val appName: String = "Uplift.ai",
    val splashSubtitle: String = "Your personal AI fitness coach.",
    val welcomeTitle: String = "Welcome To\nUplift.ai",
    val welcomeSubtitle: String = "Your personal fitness AI Assistant 🤖"
)

sealed class OnboardingNavigationEvent {
    object NavigateToWelcome : OnboardingNavigationEvent()
    object NavigateToGetStarted : OnboardingNavigationEvent()
    object NavigateToSignIn : OnboardingNavigationEvent()
}

class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<OnboardingNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        startSplashTimer()
    }

    private fun startSplashTimer() {
        viewModelScope.launch {
            delay(2500L) // Задержка 2.5 секунды перед переходом на Welcome Screen
            _navigationEvent.emit(OnboardingNavigationEvent.NavigateToWelcome)
        }
    }

    fun onGetStartedClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(OnboardingNavigationEvent.NavigateToGetStarted)
        }
    }

    fun onSignInClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(OnboardingNavigationEvent.NavigateToSignIn)
        }
    }
}