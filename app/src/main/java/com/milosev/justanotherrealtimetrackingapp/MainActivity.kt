package com.milosev.justanotherrealtimetrackingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart: Button = findViewById<View>(R.id.btnStart) as Button
        btnStart.setOnClickListener {

        }

        val btnStop: Button = findViewById<View>(R.id.btnStop) as Button
        btnStop.setOnClickListener {
        }
    }
}