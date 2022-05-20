package com.milosev.justanotherrealtimetrackingapp

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MainActivity : AppCompatActivity() {

    private val broadcastTickReceiver: BroadcastReceiver = BroadcastTickReceiver()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filter = IntentFilter(IntentAction.TICK_LOCATION)
        registerReceiver(broadcastTickReceiver, filter)

        val broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.action) {
                    IntentAction.TICK_LOCATION -> {
                        println("test")
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter(IntentAction.TICK_LOCATION))

        val btnStart: Button = findViewById<View>(R.id.btnStart) as Button
        btnStart.setOnClickListener {

            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            val component = ComponentName(this, BroadcastTickReceiver::class.java)
            packageManager.setComponentEnabledSetting(
                component,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            val numOfSecondsForTick: TextView =
                findViewById<View>(R.id.txtRequestUpdates) as TextView
            var strNumOfSecondsForTick: String = numOfSecondsForTick.text.toString()
            if (strNumOfSecondsForTick.isEmpty()) strNumOfSecondsForTick = "30"

            val intentStartForegroundTickService = Intent(this, ForegroundTickService::class.java)
            intentStartForegroundTickService.action = IntentAction.START_FOREGROUND_TICK_SERVICE
            intentStartForegroundTickService.putExtra(IntentExtras.NUM_OF_SECONDS_FOR_TICK, strNumOfSecondsForTick.toLong())
            startForegroundService(intentStartForegroundTickService)
        }

        val btnStop: Button = findViewById<View>(R.id.btnStop) as Button
        btnStop.setOnClickListener {
            val component = ComponentName(this, BroadcastTickReceiver::class.java)
            packageManager.setComponentEnabledSetting(
                component,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )

            val intentStopForegroundTickService = Intent(this, ForegroundTickService::class.java)
            intentStopForegroundTickService.action = IntentAction.STOP_FOREGROUND_TICK_SERVICE
            startForegroundService(intentStopForegroundTickService)
        }
    }
}