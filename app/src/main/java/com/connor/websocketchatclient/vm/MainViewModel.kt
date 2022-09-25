package com.connor.websocketchatclient.vm

import androidx.lifecycle.ViewModel
import com.connor.websocketchatclient.models.ChatMessage

class MainViewModel : ViewModel() {

    fun getMsg(userId: Int, msg: String): List<ChatMessage> {
        return listOf(ChatMessage(msg, userId))
    }
}