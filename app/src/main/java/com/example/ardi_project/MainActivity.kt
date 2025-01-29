package com.example.ardi_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.view.LayoutInflater
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ardi_project.ui.theme.Ardi_ProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ardi_ProjectTheme {
                MainActivityContent()
            }
        }
    }
}

@Composable
fun MainActivityContent() {
    val showDialog: MutableState<Boolean> = remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                        // Tombol logout
                        val logoutButton: Button = findViewById(R.id.logout_button)
                        logoutButton.setOnClickListener {
                            Toast.makeText(context, "Aplikasi ditutup", Toast.LENGTH_SHORT).show()
                            (context as? ComponentActivity)?.finish() // Menutup aplikasi
                        }

                        // Teks pengantar
                        val introText: TextView = findViewById(R.id.introduction_text)
                        introText.setOnClickListener {
                            showDialog.value = true
                        }

                        // LinearLayout item_1 untuk navigasi ke Calculator
                        val item1: LinearLayout = findViewById(R.id.item_1)
                        item1.setOnClickListener {
                            val intent = Intent(context, Calculator::class.java)
                            context.startActivity(intent)
                        }
                    }
                }
            )
        }

        // Menampilkan dialog jika diperlukan
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showDialog.value = false
                },
                title = {
                    Text(text = "Informasi")
                },
                text = {
                    Text(text = "Hai, nama saya Ardi Okdianto. Saya seorang web developer.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                        }
                    ) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Ardi_ProjectTheme {
        MainActivityContent()
    }
}
