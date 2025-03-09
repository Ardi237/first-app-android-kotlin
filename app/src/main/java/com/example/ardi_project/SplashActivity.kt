package com.example.ardi_project

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.ardi_project.LoginActivity
import com.example.ardi_project.MainActivity
import com.example.ardi_project.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Buat tampilan gambar sebagai splash screen
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.splash_image) // Pastikan file ini ada di res/drawable
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        setContentView(imageView)

        // Delay sebelum berpindah ke halaman berikutnya
        Handler(Looper.getMainLooper()).postDelayed({
            val nextActivity = LoginActivity::class.java
            startActivity(Intent(this, nextActivity))

            finish() // Hapus SplashActivity dari back stack
        }, 2000) // Delay 2 detik
    }
}
