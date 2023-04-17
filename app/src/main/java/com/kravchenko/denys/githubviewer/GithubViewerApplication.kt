package com.kravchenko.denys.githubviewer

import android.app.Application
import com.kravchenko.denys.githubviewer.di.appModule
import com.kravchenko.denys.githubviewer.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class GithubViewerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@GithubViewerApplication)
            modules(listOf(appModule, networkModule))
        }
    }
}