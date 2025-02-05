package com.example.ardi_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ardi_project.ApiClient
import com.example.ardi_project.ApiService
import com.example.ardi_project.LoginRequest
import com.example.ardi_project.LoginResponse
import com.example.ardi_project.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class LoginBackground : AppCompatActivity() {

    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_background)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)

        // Inisialisasi TokenManager
        TokenManager.init(this)

        loginBtn.setOnClickListener {
            val email = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Email dan Password harus diisi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val loginRequest = LoginRequest(email, password)

        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token ?: ""
                    TokenManager.saveToken(token)

                    Toast.makeText(this@LoginBackground, "Login Berhasil", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginBackground, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginBackground, "Login Gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Log error untuk debugging
                Log.e("LoginError", t.message ?: "Unknown error")

                // Tentukan pesan yang akan ditampilkan berdasarkan jenis kesalahan
                val errorMessage = when (t) {
                    is IOException -> "Koneksi jaringan terputus. Periksa koneksi internet Anda."
                    is HttpException -> {
                        // Handle error berdasarkan kode status HTTP
                        when (t.code()) {
                            401 -> "Email atau kata sandi salah."
                            500 -> "Terjadi kesalahan pada server. Silakan coba lagi nanti."
                            else -> "Terjadi kesalahan. Silakan coba lagi."
                        }
                    }
                    else -> "Terjadi kesalahan tidak terduga. Silakan coba lagi."
                }

                // Tampilkan pesan error ke pengguna
                Toast.makeText(this@LoginBackground, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
