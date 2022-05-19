package com.milosev.justanotherrealtimetrackingapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class ForegroundTickService : Service(), CoroutineScope by MainScope() {

    private var job: Job? = null

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "startForegroundTickService" -> {
                startForeground(101, createNotification())

                val context = this
                val bundle = intent.extras
                var numOfSecondsForTick = bundle!!.getString("numOfSecondsForTick")
                if (numOfSecondsForTick == null) {
                    numOfSecondsForTick = "30"
                }
                job = launch {
                    while (true) {
                        val serviceIntent = Intent(context, BroadcastTickReceiver::class.java).setAction("TickLocation")
                        serviceIntent.putExtra("numOfSecondsForTick", numOfSecondsForTick)
                        sendBroadcast(serviceIntent)
                        delay(numOfSecondsForTick.toLong() * 1_000)
                    }
                }
            }
            "stopForegroundTickService" -> {
                job?.cancel()
                stopForeground(true)
                stopSelfResult(startId)
            }
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification(): Notification {
        val channelId =
            createNotificationChannel()
        val notificationBuilder = NotificationCompat.Builder(this, channelId)

        return notificationBuilder.setOngoing(true)
            .setContentTitle("test")
            .setContentText("test")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(1)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "Foreground_TickService"
        val channel = NotificationChannel(
            channelId, "Foreground tick service", NotificationManager.IMPORTANCE_LOW
        )
        channel.lightColor = Color.RED
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }
}