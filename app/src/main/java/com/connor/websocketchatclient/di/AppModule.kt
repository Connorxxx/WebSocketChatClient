package com.connor.websocketchatclient.di

import com.connor.websocketchatclient.vm.MainViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { MainViewModel() }
    single { client() }
    single { ioDispatcher() }

}

fun client() = HttpClient(CIO) {
    install(WebSockets)
}

fun ioDispatcher() = Dispatchers.IO