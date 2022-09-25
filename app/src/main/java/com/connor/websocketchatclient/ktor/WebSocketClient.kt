package com.connor.websocketchatclient.ktor

import android.util.Log
import com.drake.channel.receiveEventHandler
import com.drake.channel.sendEvent
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.receiveAsFlow


suspend fun DefaultClientWebSocketSession.outputMessages() {
    kotlin.runCatching {
        incoming.receiveAsFlow().filterIsInstance<Frame.Text>().collect {
            sendEvent(it.readText(), "receivedText")
        }
    }.onFailure {
        Log.e("onFailure", "Error while receiving: ${it.localizedMessage} ")
    }
}

suspend fun DefaultClientWebSocketSession.inputMessages() {
        kotlin.runCatching {
            receiveEventHandler<String>("sendText") {
                outgoing.send(Frame.Text(it))
            }
        }.onFailure {
            Log.e("onFailure", "inputMessages: ${it.localizedMessage}", )
            return
        }
}
