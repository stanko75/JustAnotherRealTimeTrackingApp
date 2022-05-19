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
            "startBroadcastTickReceiver" -> {
                val startBroadcastTickReceiverIntent = Intent(context, MainActivity::class.java).setAction("mainActivityReceiver")
                LocalBroadcastManager.getInstance(context).sendBroadcast(startBroadcastTickReceiverIntent)
            }
            "restartForegroundTickService" -> {
                val restartForegroundTickServiceIntent = Intent(context, ForegroundTickService::class.java).setAction("startForegroundTickService")
                context.startForegroundService(restartForegroundTickServiceIntent)
            }
        }
    }
}