package com.example.mycursmob.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycursmob.domain.model.User
import com.example.mycursmob.domain.repository.AuthFirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    // Login form
    val loginEmail: String = "",
    val loginPassword: String = "",
    val isLoginPasswordVisible: Boolean = false,
    val isLoginLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccessMessage: String? = null,

    // Sign Up form
    val signUpEmail: String = "",
    val signUpPassword: String = "",
    val signUpConfirmPassword: String = "",
    val isSignUpPasswordVisible: Boolean = false,
    val isSignUpConfirmPasswordVisible: Boolean = false,
    val fullName: String = "",
    val userName: String = "",
    val phone: String = "",
    val isSignUpLoading: Boolean = false,
    val signUpError: String? = null,
    val passwordMismatchError: Boolean = false,

    // Current authenticated user
    val currentUser: User? = null
)

class AuthViewModel(
    private val repository: AuthFirebaseRepository = AuthFirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        val savedUser = repository.getSavedUser()
        if (savedUser != null) {
            _uiState.update { it.copy(currentUser = savedUser) }
        }
    }

    // ---------------- Login Actions ----------------
    fun onLoginEmailChange(email: String) {
        _uiState.update { it.copy(loginEmail = email, loginError = null, loginSuccessMessage = null) }
    }

    fun onLoginPasswordChange(password: String) {
        _uiState.update { it.copy(loginPassword = password, loginError = null, loginSuccessMessage = null) }
    }

    fun toggleLoginPasswordVisibility() {
        _uiState.update { it.copy(isLoginPasswordVisible = !it.isLoginPasswordVisible) }
    }

    fun login(onSuccess: (isAssessmentCompleted: Boolean) -> Unit) {
        val state = _uiState.value
        val email = state.loginEmail.trim()
        val password = state.loginPassword.trim()

        if (email.isEmpty()) {
            _uiState.update { it.copy(loginError = "Please enter email address") }
            return
        }
        if (password.isEmpty()) {
            _uiState.update { it.copy(loginError = "Please enter password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoginLoading = true, loginError = null) }
            val result = repository.Login(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                _uiState.update { it.copy(isLoginLoading = false, currentUser = user, loginError = null) }
                onSuccess(user?.isAssessmentCompleted == true)
            } else {
                val errorMsg = formatErrorMessage(result.exceptionOrNull())
                _uiState.update { it.copy(isLoginLoading = false, loginError = errorMsg) }
            }
        }
    }

    fun saveAssessment(
        age: Int,
        weightKg: Int,
        isKgSelected: Boolean,
        fitnessLevel: Int,
        fitnessLevelName: String,
        gender: String,
        fitnessGoals: List<String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.saveAssessmentData(
                age = age,
                weightKg = weightKg,
                isKgSelected = isKgSelected,
                fitnessLevel = fitnessLevel,
                fitnessLevelName = fitnessLevelName,
                gender = gender,
                fitnessGoals = fitnessGoals
            )
            if (result.isSuccess) {
                _uiState.update { it.copy(currentUser = result.getOrNull()) }
                onSuccess()
            }
        }
    }

    // ---------------- Sign Up Actions ----------------
    fun onSignUpEmailChange(email: String) {
        _uiState.update { it.copy(signUpEmail = email, signUpError = null) }
    }

    fun onSignUpPasswordChange(password: String) {
        _uiState.update {
            val mismatch = it.signUpConfirmPassword.isNotEmpty() && password != it.signUpConfirmPassword
            it.copy(
                signUpPassword = password,
                signUpError = null,
                passwordMismatchError = mismatch
            )
        }
    }

    fun onSignUpConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            val mismatch = confirmPassword.isNotEmpty() && it.signUpPassword != confirmPassword
            it.copy(
                signUpConfirmPassword = confirmPassword,
                signUpError = null,
                passwordMismatchError = mismatch
            )
        }
    }

    fun toggleSignUpPasswordVisibility() {
        _uiState.update { it.copy(isSignUpPasswordVisible = !it.isSignUpPasswordVisible) }
    }

    fun toggleSignUpConfirmPasswordVisibility() {
        _uiState.update { it.copy(isSignUpConfirmPasswordVisible = !it.isSignUpConfirmPasswordVisible) }
    }

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun onUserNameChange(userName: String) {
        _uiState.update { it.copy(userName = userName) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun signUp(onSuccess: () -> Unit) {
        val state = _uiState.value
        val email = state.signUpEmail.trim()
        val password = state.signUpPassword
        val confirmPassword = state.signUpConfirmPassword

        if (email.isEmpty()) {
            _uiState.update { it.copy(signUpError = "Please enter email address") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(signUpError = "Password must be at least 6 characters") }
            return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(passwordMismatchError = true, signUpError = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSignUpLoading = true, signUpError = null, passwordMismatchError = false) }
            val result = repository.Register(
                email = email,
                password = password,
                userName = state.userName.ifEmpty { email.substringBefore("@") },
                fullName = state.fullName.ifEmpty { email.substringBefore("@") },
                phone = state.phone
            )
            if (result.isSuccess) {
                repository.Logout()
                _uiState.update {
                    it.copy(
                        isSignUpLoading = false,
                        currentUser = null,
                        signUpError = null,
                        signUpPassword = "",
                        signUpConfirmPassword = "",
                        loginEmail = email,
                        loginPassword = "",
                        loginError = null,
                        loginSuccessMessage = "Аккаунт успешно создан! Войдите в приложение."
                    )
                }
                onSuccess()
            } else {
                val errorMsg = formatErrorMessage(result.exceptionOrNull())
                _uiState.update { it.copy(isSignUpLoading = false, signUpError = errorMsg) }
            }
        }
    }

    private fun formatErrorMessage(throwable: Throwable?): String {
        val message = throwable?.localizedMessage ?: return "Произошла неизвестная ошибка"
        return when {
            message.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
                "Ошибка Firebase: в консоли Firebase (проект mycursmobb-a7ee3) не включен провайдер Email/Password! Включите его в Firebase Console -> Authentication -> Sign-in method."
            message.contains("EMAIL_EXISTS", ignoreCase = true) || message.contains("already in use", ignoreCase = true) ->
                "Пользователь с таким Email уже зарегистрирован."
            message.contains("INVALID_EMAIL", ignoreCase = true) || message.contains("badly formatted", ignoreCase = true) ->
                "Некорректный формат Email адреса."
            message.contains("USER_NOT_FOUND", ignoreCase = true) || message.contains("WRONG_PASSWORD", ignoreCase = true) || message.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
                "Неверный логин или пароль."
            message.contains("WEAK_PASSWORD", ignoreCase = true) ->
                "Пароль слишком простой (минимум 6 символов)."
            else -> message
        }
    }

    fun logout() {
        repository.Logout()
        _uiState.update { AuthUiState() }
    }
}
