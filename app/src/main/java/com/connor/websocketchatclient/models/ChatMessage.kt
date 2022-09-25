package com.connor.websocketchatclient.models

private const val currentUserId = 1

data class ChatMessage(
    val content: String,
    val userId: Int,
) {
    fun isMine(): Boolean {
        return currentUserId == userId
    }
}
