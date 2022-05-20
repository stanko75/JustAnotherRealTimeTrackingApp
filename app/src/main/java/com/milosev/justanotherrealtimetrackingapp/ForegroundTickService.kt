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
            IntentAction.START_FOREGROUND_TICK_SERVICE -> {
                startForeground(101, createNotification())

                val context = this
                val numOfSecondsForTick = intent.getLongExtra (IntentExtras.NUM_OF_SECONDS_FOR_TICK, 30)
                job = launch {
                    while (true) {
                        val serviceIntent = Intent(context, BroadcastTickReceiver::class.java).setAction(IntentAction.TICK_LOCATION)
                        serviceIntent.putExtra(IntentExtras.NUM_OF_SECONDS_FOR_TICK, numOfSecondsForTick)
                        sendBroadcast(serviceIntent)
                        delay(numOfSecondsForTick * 1_000)
                    }
                }
            }
            IntentAction.STOP_FOREGROUND_TICK_SERVICE -> {
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