package com.connor.websocketchatclient.ktor

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*

object HttpClient {

    val client = HttpClient(CIO) {
        install(WebSockets)
    }
}