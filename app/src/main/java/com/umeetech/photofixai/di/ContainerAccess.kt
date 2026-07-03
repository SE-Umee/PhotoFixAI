package com.umeetech.photofixai.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.umeetech.photofixai.PhotoFixApp

/** Resolves the app-wide [AppContainer] from any Context. */
val Context.appContainer: AppContainer
    get() = (applicationContext as PhotoFixApp).container

/** Convenience for composables that need the DI container. */
@Composable
fun rememberAppContainer(): AppContainer {
    val context = LocalContext.current
    return remember(context) { context.appContainer }
}
