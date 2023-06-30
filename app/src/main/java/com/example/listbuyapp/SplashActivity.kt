package com.example.listbuyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        Thread.sleep(600)
        screenSplash.setKeepOnScreenCondition { true }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}