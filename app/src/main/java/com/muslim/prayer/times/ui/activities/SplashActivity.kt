package com.muslim.prayer.times.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muslim.prayer.times.ui.activities.MainActivity
import com.muslim.prayer.times.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val background: Thread = object : Thread() {
            override fun run() {
                sleep((5 * 1000).toLong())
                val i = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(i)
                finish()
            }
        }
        background.start()


    }
}