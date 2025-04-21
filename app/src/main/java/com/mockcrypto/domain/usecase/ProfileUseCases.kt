package com.mockcrypto.domain.usecase

import com.mockcrypto.data.repository.PortfolioRepository
import com.mockcrypto.data.repository.SettingsRepository
import com.mockcrypto.data.repository.UserRepository
import com.mockcrypto.domain.model.ThemeMode
import com.mockcrypto.domain.model.UserProfile
import com.mockcrypto.domain.model.UserSettings

class GetUserProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<UserProfile> {
        return repository.getUserProfile()
    }
}

class LogoutUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}

class GetUserSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(): Result<UserSettings> {
        return repository.getUserSettings()
    }
}

class UpdateThemeModeUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(themeMode: ThemeMode): Result<UserSettings> {
        return repository.updateThemeMode(themeMode)
    }
}

class ResetPortfolioUseCase(private val repository: PortfolioRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.resetPortfolio()
    }
}

data class ProfileUseCases(
    val getUserProfile: GetUserProfileUseCase,
    val logout: LogoutUseCase,
    val getUserSettings: GetUserSettingsUseCase,
    val updateThemeMode: UpdateThemeModeUseCase,
    val resetPortfolio: ResetPortfolioUseCase
) 