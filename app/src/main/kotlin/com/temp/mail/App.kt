package com.temp.mail

import android.app.Application
import com.temp.mail.data.repository.TokenRepository
import com.temp.mail.di.appModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    
    private val tokenRepository: TokenRepository by inject()
    
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
        
        // Start token management
        tokenRepository.startTokenRefresh()
    }
    
    override fun onTerminate() {
        super.onTerminate()
        tokenRepository.cleanup()
    }
}