package com.umeetech.photofixai.presentation.screens.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.di.AppContainer
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

class OnboardingViewModel(private val container: AppContainer) : ViewModel() {

    val pages = listOf(
        OnboardingPage(
            "Remove Background Instantly",
            "Remove image backgrounds and export transparent PNG or colored background photos.",
            Icons.Filled.AutoFixHigh
        ),
        OnboardingPage(
            "Create Passport & ID Photos",
            "Create passport, visa, CNIC, and ID photos with correct size and background color.",
            Icons.Filled.Badge
        ),
        OnboardingPage(
            "Resize, Compress & Export",
            "Resize, compress, save, and share images in professional quality.",
            Icons.Filled.PhotoSizeSelectLarge
        )
    )

    fun complete(onDone: () -> Unit) {
        viewModelScope.launch {
            container.completeOnboardingUseCase()
            onDone()
        }
    }
}
