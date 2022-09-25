package com.connor.websocketchatclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.connor.websocketchatclient.databinding.ActivityMainBinding
import com.connor.websocketchatclient.models.ChatMessage
import com.connor.websocketchatclient.service.KtorService
import com.connor.websocketchatclient.vm.MainViewModel
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.drake.channel.receiveEvent
import com.drake.channel.sendEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        startService<KtorService> {}
        binding.rv.setup {
            addType<ChatMessage> {
                if (isMine()) R.layout.item_msg_right else R.layout.item_msg_left
            }
        }
        receiveEvent<String>("receivedText") {
            binding.rv.addModels(viewModel.getMsg(2, it))
            Log.d("getMsg", "onCreate: $it")
            binding.rv.scrollToPosition(binding.rv.adapter!!.itemCount - 1)
        }
        binding.btnSend.setOnClickListener {
            val msg = binding.etMsg.text.toString()
            sendEvent(msg, "sendText")
            binding.rv.apply {
                addModels(listOf(ChatMessage(msg, 1)))
                scrollToPosition(binding.rv.adapter!!.itemCount - 1)
            }
            binding.etMsg.setText("")
        }
    }

    private inline fun <reified T> startService(block: Intent.() -> Unit) {
        val intent = Intent(this, T::class.java)
        intent.block()
        startService(intent)
    }
}