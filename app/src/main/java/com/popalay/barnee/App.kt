package com.popalay.barnee

import android.app.Application
import com.popalay.barnee.di.initKoin
import com.popalay.barnee.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
            androidLogger()
            modules(uiModule)
        }
    }
}