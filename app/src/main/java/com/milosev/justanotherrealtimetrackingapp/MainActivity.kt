package com.milosev.justanotherrealtimetrackingapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val btnStart: Button = findViewById<View>(R.id.btnStart) as Button
        btnStart.setOnClickListener {
            val intent = Intent(this, ForegroundTickService::class.java)
            intent.action = "startForegroundTickService"
            startForegroundService(intent)
        }

        val btnStop: Button = findViewById<View>(R.id.btnStop) as Button
        btnStop.setOnClickListener {
            val intent = Intent(this, ForegroundTickService::class.java)
            intent.action = "stopForegroundTickService"
            startForegroundService(intent)
        }
    }
}