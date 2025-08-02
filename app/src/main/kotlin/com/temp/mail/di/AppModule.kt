package com.temp.mail.di

import com.temp.mail.data.datastore.SettingsDataStore
import com.temp.mail.ui.settings.SettingsViewModel
import com.temp.mail.ui.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // DataStore
    single { SettingsDataStore(androidContext()) }

    // ViewModels
    viewModel { MainViewModel() }
    viewModel { SettingsViewModel() }
}