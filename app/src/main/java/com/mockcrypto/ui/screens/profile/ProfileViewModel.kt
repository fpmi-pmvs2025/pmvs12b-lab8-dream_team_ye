package com.mockcrypto.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mockcrypto.data.repository.MockPortfolioRepository
import com.mockcrypto.data.repository.MockSettingsRepository
import com.mockcrypto.data.repository.MockUserRepository
import com.mockcrypto.domain.model.ThemeMode
import com.mockcrypto.domain.model.UserProfile
import com.mockcrypto.domain.model.UserSettings
import com.mockcrypto.domain.usecase.GetUserProfileUseCase
import com.mockcrypto.domain.usecase.GetUserSettingsUseCase
import com.mockcrypto.domain.usecase.LogoutUseCase
import com.mockcrypto.domain.usecase.ProfileUseCases
import com.mockcrypto.domain.usecase.ResetPortfolioUseCase
import com.mockcrypto.domain.usecase.UpdateThemeModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val userSettings: UserSettings? = null,
    val error: String? = null,
    val operationSuccess: Boolean? = null,
    val operationMessage: String? = null
)

class ProfileViewModel : ViewModel() {
    // Todo: Replace with dependency injection
    private val userRepository = MockUserRepository()
    private val settingsRepository = MockSettingsRepository()
    private val portfolioRepository = MockPortfolioRepository()
    
    private val profileUseCases = ProfileUseCases(
        getUserProfile = GetUserProfileUseCase(userRepository),
        logout = LogoutUseCase(userRepository),
        getUserSettings = GetUserSettingsUseCase(settingsRepository),
        updateThemeMode = UpdateThemeModeUseCase(settingsRepository),
        resetPortfolio = ResetPortfolioUseCase(portfolioRepository)
    )
    
    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfileData()
    }
    
    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    error = null,
                    operationSuccess = null,
                    operationMessage = null
                ) 
            }
            
            try {
                val profileResult = profileUseCases.getUserProfile()
                val settingsResult = profileUseCases.getUserSettings()
                
                when {
                    profileResult.isFailure -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = profileResult.exceptionOrNull()?.message
                            ) 
                        }
                    }
                    
                    settingsResult.isFailure -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = settingsResult.exceptionOrNull()?.message
                            ) 
                        }
                    }
                    
                    else -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                userProfile = profileResult.getOrNull(),
                                userSettings = settingsResult.getOrNull(),
                                error = null
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    ) 
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val result = profileUseCases.logout()
                
                if (result.isSuccess) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            operationSuccess = true,
                            operationMessage = "Successfully logged out"
                        ) 
                    }
                    loadProfileData() // Refresh the profile data
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            operationSuccess = false,
                            operationMessage = result.exceptionOrNull()?.message
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        operationSuccess = false,
                        operationMessage = e.message
                    ) 
                }
            }
        }
    }
    
    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val result = profileUseCases.updateThemeMode(themeMode)
                
                if (result.isSuccess) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            userSettings = result.getOrNull(),
                            operationSuccess = true,
                            operationMessage = "Theme updated to ${themeMode.name.lowercase()}"
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            operationSuccess = false,
                            operationMessage = result.exceptionOrNull()?.message
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        operationSuccess = false,
                        operationMessage = e.message
                    ) 
                }
            }
        }
    }
    
    fun resetPortfolio() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val result = profileUseCases.resetPortfolio()
                
                if (result.isSuccess) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            operationSuccess = true,
                            operationMessage = "Portfolio has been reset successfully"
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            operationSuccess = false,
                            operationMessage = result.exceptionOrNull()?.message
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        operationSuccess = false,
                        operationMessage = e.message
                    ) 
                }
            }
        }
    }
    
    fun resetOperationStatus() {
        _uiState.update { it.copy(operationSuccess = null, operationMessage = null) }
    }
} 