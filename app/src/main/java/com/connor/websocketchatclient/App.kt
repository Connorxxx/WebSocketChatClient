package com.connor.websocketchatclient

import android.app.Application
import android.content.Context
import com.connor.websocketchatclient.di.appModule
import com.drake.brv.utils.BRV
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        BRV.modelId = BR.m

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}