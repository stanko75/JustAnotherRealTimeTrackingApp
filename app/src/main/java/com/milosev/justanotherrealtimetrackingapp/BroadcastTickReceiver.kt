package com.milosev.justanotherrealtimetrackingapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BroadcastTickReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            IntentAction.START_FOREGROUND_TICK_SERVICE -> {
                val startBroadcastTickReceiverIntent =
                    Intent(context, MainActivity::class.java).setAction(IntentAction.MAIN_ACTIVITY_RECEIVER)
                LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(startBroadcastTickReceiverIntent)
            }
            IntentAction.STOP_FOREGROUND_TICK_SERVICE -> {
                val restartForegroundTickServiceIntent = Intent(
                    context,
                    ForegroundTickService::class.java
                ).setAction(IntentAction.STOP_FOREGROUND_TICK_SERVICE)
                context.startForegroundService(restartForegroundTickServiceIntent)
            }
        }
    }
}