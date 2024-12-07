package com.felina.ummuquran.di

import com.felina.ummuquran.data.network.ApiClient
import com.felina.ummuquran.data.network.ApiService
import com.felina.ummuquran.data.network.ApiService2
import com.felina.ummuquran.data.repository.QuranRepository
import com.felina.ummuquran.ui.view.dashboard.DashboardViewModel
import com.felina.ummuquran.ui.view.read.ReadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Provide ApiService
    single<ApiService> { ApiClient.retrofit }
    single<ApiService2> { ApiClient.retrofit2 }

    // Provide UserRepository
    single { QuranRepository(get(),get()) }

    // Provide UserViewModel
    viewModel {
        DashboardViewModel(get())
    }
    viewModel {
        ReadViewModel(get())
    }
}