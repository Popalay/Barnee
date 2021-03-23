package com.popalay.barnee

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.popalay.barnee.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(this)
        initKoin {
            androidContext(this@App)
            androidLogger()
        }
    }
}