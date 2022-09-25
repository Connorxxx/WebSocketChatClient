package com.connor.websocketchatclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.connor.websocketchatclient.databinding.ActivityMainBinding
import com.connor.websocketchatclient.models.ChatMessage
import com.connor.websocketchatclient.service.KtorService
import com.connor.websocketchatclient.vm.MainViewModel
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.drake.channel.receiveEvent
import com.drake.channel.receiveTag
import com.drake.channel.sendEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
       // startService<KtorService> {}
        viewModel.liveData.observe(this) {
            binding.btnConnect.text = if (it) "Disconnect" else "Connect"
        }
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
        receiveTag("serverStop") {
            viewModel.setIsConnect(false)
        }
        viewModel.url?.let {
            binding.editURL.setText(it)
        }
        viewModel.content?.let {
            binding.etMsg.setText(it)
            binding.btnSend.visibility = View.VISIBLE
        }
        initVIewListener()
    }

    private fun initVIewListener() {
        binding.etMsg.addTextChangedListener {
            viewModel.content = it.toString()
            if (it.isNullOrEmpty()) binding.btnSend.visibility = View.GONE
            else binding.btnSend.visibility = View.VISIBLE
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
        binding.btnConnect.setOnClickListener {
            if (!viewModel.getIsConnect()) {
                val url = binding.editURL.text.toString()
                startService<KtorService> {
                    putExtra("URL", url)
                }
                viewModel.url = url
                viewModel.setIsConnect(true)
            } else {
                stopService<KtorService> {}
            }
        }
    }

    private inline fun <reified T> startService(block: Intent.() -> Unit) {
        val intent = Intent(this, T::class.java)
        intent.block()
        startService(intent)
    }

    private inline fun <reified T> stopService(block: Intent.() -> Unit) {
        val stopService = Intent(this, T::class.java)
        stopService.block()
        stopService(stopService)
    }
}