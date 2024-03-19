package com.aramSA.carTing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class splashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
    }
}