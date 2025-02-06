package com.example.ardi_project

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ardi_project.ui.theme.Ardi_ProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = DatabaseHelper(this)
        val isLoggedIn = dbHelper.isUserLoggedIn()
        Log.d("MainActivity", "User Logged In Status: $isLoggedIn")

        if (!isLoggedIn) {
            Log.d("MainActivity", "User not logged in, redirecting to LoginActivity")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            setContent {
                Ardi_ProjectTheme {
                    MainActivityContent()
                }
            }
        }
    }
}

@Composable
fun MainActivityContent() {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Selamat datang di aplikasi saya!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Menggunakan AndroidView untuk menghubungkan XML ke Jetpack Compose
            AndroidView(
                modifier = Modifier.padding(bottom = 16.dp),
                factory = { context ->
                    LayoutInflater.from(context).inflate(R.layout.activity_main, null, false).apply {
                        // Logout
                        val logoutButton: Button = findViewById(R.id.logout_button)

                        logoutButton.setOnClickListener {
                            dbHelper.setUserLoggedIn(false)
                            Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            (context as? ComponentActivity)?.finish()
                        }

                        // Teks pengantar (buka dialog informasi)
                        val introText: TextView = findViewById(R.id.introduction_text)
                        introText.setOnClickListener {
                            showDialog.value = true
                        }

                        // Navigasi ke Kalkulator
                        val item1: LinearLayout = findViewById(R.id.item_1)
                        item1.setOnClickListener {
                            val intent = Intent(context, Calculator::class.java)
                            context.startActivity(intent)
                        }
                    }
                }
            )
        }

        // Menampilkan dialog informasi
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Informasi") },
                text = { Text(text = "Hai, nama saya Ardi Okdianto. Saya seorang web developer.") },
                confirmButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivity() {
    Ardi_ProjectTheme {
        MainActivityContent()
    }
}
class LoginActivity : AppCompatActivity() {
    private lateinit var sqliteHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_background)

        sqliteHelper = DatabaseHelper(this)
        if (sqliteHelper.isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }




        val usernameInput = findViewById<EditText>(R.id.username_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.login_btn)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (sqliteHelper.checkLogin(username, password)) {
                sqliteHelper.setUserLoggedIn(true)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login gagal!", Toast.LENGTH_SHORT).show()
            }
        }

        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

class SignupActivity : AppCompatActivity() {
    private lateinit var sqliteHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_background)

        sqliteHelper = DatabaseHelper(this)
        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val signupButton = findViewById<Button>(R.id.register)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        signupButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (sqliteHelper.addUser(username, password)) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show()
            }
        }

        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
