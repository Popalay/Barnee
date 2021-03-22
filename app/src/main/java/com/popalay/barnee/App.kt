package com.popalay.barnee

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.popalay.barnee.data.LocalStore

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(this)
        LocalStore.init(this)
    }
}