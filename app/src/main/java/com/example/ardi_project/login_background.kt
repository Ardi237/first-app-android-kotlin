package com.example.ardi_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginBackground : AppCompatActivity() {

    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_background)

        // Inisialisasi komponen dari layout
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)

        // Login button click listener
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            Log.i("Test Credentials", "Username: $username and Password: $password")

            // Validasi username dan password statis
            if (username == "ardi" && password == "12345") {
                // Tampilkan pesan berhasil
                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()

                // Pindah ke activity dengan layout_main
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Menutup activity login
            } else {
                // Tampilkan pesan gagal
                Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
