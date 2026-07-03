package com.umeetech.photofixai.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/**
 * Builds a [ViewModel] scoped to the current nav destination, injecting the manual
 * DI [AppContainer]. Keeps screens free of factory boilerplate while remaining
 * framework-light (no Hilt required).
 */
@Composable
inline fun <reified VM : ViewModel> rememberScopedViewModel(
    crossinline create: (AppContainer) -> VM
): VM {
    val container = rememberAppContainer()
    return viewModel(factory = viewModelFactory { initializer { create(container) } })
}
