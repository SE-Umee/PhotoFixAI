package com.umeetech.photofixai.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class SplashDestination { Undecided, Onboarding, Home }

class SplashViewModel(container: AppContainer) : ViewModel() {

    private val _destination = MutableStateFlow(SplashDestination.Undecided)
    val destination: StateFlow<SplashDestination> = _destination

    init {
        viewModelScope.launch {
            val completed = container.isOnboardingCompletedUseCase().first()
            _destination.value =
                if (completed) SplashDestination.Home else SplashDestination.Onboarding
        }
    }
}
