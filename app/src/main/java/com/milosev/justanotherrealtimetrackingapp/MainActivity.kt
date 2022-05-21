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

        val filter = IntentFilter(IntentAction.NUM_OF_TICKS)
        registerReceiver(broadcastTickReceiver, filter)

        val btnStop: Button = findViewById<View>(R.id.btnStop) as Button
        val btnStart: Button = findViewById<View>(R.id.btnStart) as Button

        val broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    IntentAction.NUM_OF_TICKS -> {
                        val numOfTicks = intent.getIntExtra (IntentExtras.NUM_OF_TICKS, 30)
                        val numberOfTicks: TextView =
                            findViewById<View>(R.id.textViewNumberOfTicks) as TextView
                        numberOfTicks.text = numOfTicks.toString()

                        EnableStartButton(false, btnStart, btnStop)

                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter(IntentAction.NUM_OF_TICKS))

        EnableStartButton(true, btnStart, btnStop)

        btnStart.setOnClickListener {

            EnableStartButton(false, btnStart, btnStop)

            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            val numberOfTicks: TextView =
                findViewById<View>(R.id.textViewNumberOfTicks) as TextView
            numberOfTicks.text = "0"

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

            val intentStartForegroundTickService =
                Intent(this, ForegroundTickService::class.java)
            intentStartForegroundTickService.action = IntentAction.START_FOREGROUND_TICK_SERVICE
            intentStartForegroundTickService.putExtra(
                IntentExtras.NUM_OF_SECONDS_FOR_TICK,
                strNumOfSecondsForTick.toLong()
            )
            startForegroundService(intentStartForegroundTickService)
        }

        btnStop.setOnClickListener {

            EnableStartButton(true, btnStart, btnStop)

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

    fun EnableStartButton(enable: Boolean, btnStart: Button, btnStop: Button) {
        btnStart.isEnabled = enable
        btnStop.isEnabled = !btnStart.isEnabled
    }
}