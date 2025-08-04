package com.temp.mail.di

import com.temp.mail.data.datastore.SettingsDataStore
import com.temp.mail.data.network.MailCxApiService
import com.temp.mail.data.network.MailService
import com.temp.mail.data.network.MailServiceImpl
import com.temp.mail.data.repository.TokenRepository
import com.temp.mail.data.repository.EmailRepository
import com.temp.mail.data.repository.EmailRepositoryImpl
import com.temp.mail.ui.settings.SettingsViewModel
import com.temp.mail.ui.viewmodel.MainViewModel
import com.temp.mail.ui.viewmodel.EmailListViewModel
import com.temp.mail.ui.viewmodel.EmailDetailViewModel
import com.temp.mail.util.FileLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.temp.mail.R

val appModule = module {
    // DataStore
    single { SettingsDataStore(androidContext()) }

    // Network
    single { androidContext().getString(R.string.base_url) }
    singleOf(::MailCxApiService)
    single<MailService> { MailServiceImpl(get(), get()) }

    // Repositories
    single { TokenRepository(get(), androidContext(), get()) }
    single<EmailRepository> { EmailRepositoryImpl(get(), get()) }

    // Utils
    single { FileLogger(androidContext()) }

    // ViewModels
    viewModelOf(::MainViewModel)
    viewModelOf(::SettingsViewModel)
    singleOf(::EmailListViewModel)
    viewModelOf(::EmailDetailViewModel)
}