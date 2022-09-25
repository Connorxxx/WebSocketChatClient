package com.connor.websocketchatclient.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.connor.websocketchatclient.models.ChatMessage
import com.drake.serialize.serialize.serialLazy

class MainViewModel : ViewModel() {

    companion object {
        private var isConnect = false
    }

    val liveData = MutableLiveData<Boolean>()

    var url: String? by serialLazy()
    var content: String? by serialLazy()

    fun getIsConnect() = isConnect

    fun setIsConnect(open: Boolean) {
        isConnect = open
        liveData.value = open
    }

    fun getMsg(userId: Int, msg: String): List<ChatMessage> {
        return listOf(ChatMessage(msg, userId))
    }
}