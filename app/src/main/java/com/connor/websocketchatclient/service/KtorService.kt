package com.connor.websocketchatclient.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.connor.websocketchatclient.MainActivity
import com.connor.websocketchatclient.R
import com.connor.websocketchatclient.ktor.inputMessages
import com.connor.websocketchatclient.ktor.outputMessages
import com.connor.websocketchatclient.tools.showToast
import com.drake.channel.sendTag
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class KtorService : Service() {

    private val ioDispatcher: CoroutineDispatcher by inject()
    private val client: HttpClient by inject()

    private val job by lazy { Job() }
    private val ioScope by lazy { CoroutineScope(ioDispatcher + job) }

    override fun onCreate() {
        super.onCreate()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("ktor_server", "Ktor Service",
                NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, "ktor_server")
            .setContentTitle("Ktor server is running")
            .setContentText("You could disable it notification")
            .setSmallIcon(R.drawable.outline_rss_feed_24)
            .setContentIntent(pi)
            .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("URL") ?: ""
        ioScope.launch {
            kotlin.runCatching {
                client.webSocket(url) {
                    inputMessages()
                    outputMessages()
                }
            }.onFailure {
                Log.e("onFailure", "onStartCommand: ${it.localizedMessage}", )
                launch(Dispatchers.Main) {
                    "ERROR".showToast()
                    stopSelf()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        super.onDestroy()
        sendTag("serverStop")
        job.cancel()
    }
}