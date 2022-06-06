package com.milosev.justanotherrealtimetrackingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MainActivity : AppCompatActivity() {

    private val broadcastTickReceiver: BroadcastReceiver = BroadcastTickReceiver()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                return
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission(context)
            }
        }

        checkOptimization(context)

        val filter = IntentFilter(IntentAction.NUM_OF_TICKS)
        registerReceiver(broadcastTickReceiver, filter)

        val btnStop: Button = findViewById<View>(R.id.btnStop) as Button
        val btnStart: Button = findViewById<View>(R.id.btnStart) as Button
        val btnOpenBatteryOptimization: Button = findViewById<View>(R.id.btnOpenBatteryOptimization) as Button

        val broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    IntentAction.NUM_OF_TICKS -> {
                        val numOfTicks = intent.getIntExtra (IntentExtras.NUM_OF_TICKS, 30)
                        val numberOfTicks: TextView =
                            findViewById<View>(R.id.textViewNumberOfTicks) as TextView
                        numberOfTicks.text = numOfTicks.toString()

                        enableStartButton(false, btnStart, btnStop)

                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter(IntentAction.NUM_OF_TICKS))

        enableStartButton(true, btnStart, btnStop)

        btnStart.setOnClickListener {

            enableStartButton(false, btnStart, btnStop)

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

            enableStartButton(true, btnStart, btnStop)

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

        btnOpenBatteryOptimization.setOnClickListener {
            openBatteryOptimization(this)
        }
    }

    fun enableStartButton(enable: Boolean, btnStart: Button, btnStop: Button) {
        btnStart.isEnabled = enable
        btnStop.isEnabled = !btnStart.isEnabled
    }

    private fun requestLocationPermission(context: Context) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            99
        )
    }

    @SuppressLint("NewApi", "BatteryLife")
    private fun checkOptimization(context: Context) {
        val packageName = applicationContext.packageName
        val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent()
            intent.action = ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
        }
    }

    private fun openBatteryOptimization(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            context.startActivity(intent)
        } else {
            //Timber.d("Battery optimization not necessary")
        }

}