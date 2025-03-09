package com.example.ardi_project

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
//import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.compose.rememberAsyncImagePainter
import com.example.ardi_project.adapters.BookAdapter
import com.example.ardi_project.ui.theme.Ardi_ProjectTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import java.io.File

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
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val dbHelper = DatabaseHelper(context)

    // State untuk menyimpan URI gambar yang diambil
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // URI tempat menyimpan foto dari kamera
    val imageFile = remember { File(context.cacheDir, "profile_image.jpg") }
    val imageUriForCamera = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
    }

    // Activity Result untuk memilih gambar dari galeri
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri = it } // Simpan URI ke state
        }

    // Activity Result untuk mengambil gambar dari kamera
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                imageUri = imageUriForCamera // Simpan URI ke state
            }
        }

    Surface(
        modifier = Modifier.fillMaxSize()
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
//
//            // Tombol untuk memilih gambar dari galeri
//            Button(onClick = { pickImageLauncher.launch("image/*") }) {
//                Text("Pilih dari Galeri")
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Tombol untuk mengambil gambar dari kamera
//            Button(onClick = { takePictureLauncher.launch(imageUriForCamera) }) {
//                Text("Ambil Foto")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Menampilkan gambar yang dipilih atau diambil
//            imageUri?.let {
//                Image(
//                    painter = rememberAsyncImagePainter(it),
//                    contentDescription = "Foto Profil",
//                    modifier = Modifier
//                        .size(150.dp)
//                        .clip(CircleShape)
//                        .border(2.dp, Color.Gray, CircleShape)
//                        .padding(8.dp)
//                )
//            }

            Spacer(modifier = Modifier.height(16.dp))

            // Layout XML yang sudah ada
            AndroidView(
                modifier = Modifier.padding(bottom = 16.dp),
                factory = { context ->
                    LayoutInflater.from(context).inflate(R.layout.activity_main, null, false).apply {
                        val logoutButton: Button = findViewById(R.id.logout_button)
                        logoutButton.setOnClickListener {
                            dbHelper.setUserLoggedIn(false)
                            Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            (context as? ComponentActivity)?.finish()
                        }

                        val introText: TextView = findViewById(R.id.introduction_text)
                        introText.setOnClickListener {
                            showDialog.value = true
                        }

                        val item1: LinearLayout = findViewById(R.id.item_1)
                        item1.setOnClickListener {
                            val intent = Intent(context, Calculator::class.java)
                            context.startActivity(intent)
                        }

                        val item2: LinearLayout = findViewById(R.id.item_2)
                        item2.setOnClickListener {
                            val intent = Intent(context, NotesActivity::class.java)
                            context.startActivity(intent)
                        }

                        val item3: LinearLayout = findViewById(R.id.item_3)
                        item3.setOnClickListener {
                            val intent = Intent(context, BookMainActivity::class.java)
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

class BookMainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var btnInfo: Button
    private lateinit var fabAddBook: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_main)

        databaseHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewBooks)
        btnInfo = findViewById(R.id.btnInfo)
        fabAddBook = findViewById(R.id.fabAddBook)

        val books = databaseHelper.getAllBooks().toMutableList()

        // 🔹 Pastikan memanggil adapter dengan benar
        bookAdapter = BookAdapter(books,
            onItemClick = { book ->
                val intent = Intent(this, BookDetailActivity::class.java)
                intent.putExtra("BOOK_ID", book.id)
                startActivity(intent)
            },
            onDeleteClick = { book ->
                databaseHelper.deleteBook(book.id)
                refreshBookList()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = bookAdapter

        btnInfo.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        fabAddBook.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivityForResult(intent, 200)
        }
    }

    // Terima hasil dari AddBookActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 200 || requestCode == 300) && resultCode == Activity.RESULT_OK) {
            refreshBookList()
        }
    }

    // Fungsi untuk refresh data
    private fun refreshBookList() {
        val books = databaseHelper.getAllBooks().toMutableList()
        bookAdapter.updateBooks(books)
        bookAdapter.notifyDataSetChanged()
    }
    override fun onResume() {
        super.onResume()
        refreshBookList() // Refresh data setelah kembali dari aktivitas lain
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


class AboutActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var btnChangePhoto: Button
    private lateinit var btnBack: Button
    private val PICK_IMAGE_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        profileImage = findViewById(R.id.profileImage)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        btnBack = findViewById(R.id.btnBack)

        // Load foto dari SharedPreferences jika sudah ada
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedImageUri = sharedPref.getString("profile_photo", null)
        if (savedImageUri != null) {
            Picasso.get().load(Uri.parse(savedImageUri)).into(profileImage)
        }

        btnChangePhoto.setOnClickListener {
            pickImageFromGallery()
        }
        btnBack.setOnClickListener {
            finish() // Menutup activity dan kembali ke sebelumnya
        }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            profileImage.setImageURI(imageUri)

            // Simpan URI ke SharedPreferences
            val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("profile_photo", imageUri.toString())
                apply()
            }
        }
    }
}
