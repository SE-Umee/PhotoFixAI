package com.umeetech.photofixai

import android.app.Application
import com.umeetech.photofixai.di.AppContainer

/**
 * Application entry point. Owns the [AppContainer] (manual DI graph) and performs
 * one-time initialization (e.g. ads SDK — currently a no-op placeholder).
 */
class PhotoFixApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)

        // AdMob init is a no-op until monetization is enabled — see AdsManager.
        container.adsManager.initialize()
    }
}
