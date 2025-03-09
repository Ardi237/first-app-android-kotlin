package com.example.ardi_project

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ardi_project.api.RetrofitClient
import com.example.ardi_project.models.Friend
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddFriendActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var btnSave: Button
    private lateinit var btnSelectImage: Button
    private lateinit var btnTakePhoto: Button
    private lateinit var ivFriendImage: ImageView
    private lateinit var tvQuote: TextView
    private var imageUri: Uri? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        etName = findViewById(R.id.etName)
        etBirthDate = findViewById(R.id.etBirthDate)
        btnSave = findViewById(R.id.btnSaveFriend)
//        btnSelectImage = findViewById(R.id.btnSelectImage)
//        btnTakePhoto = findViewById(R.id.btnTakePhoto)
//        ivFriendImage = findViewById(R.id.ivFriendImage)
//        tvQuote = findViewById(R.id.tvQuote)

        etBirthDate.setOnClickListener { showDatePicker() }

        // Tampilkan kutipan acak
        tvQuote.text = getRandomQuote()

        // Pilih gambar dari galeri
        btnSelectImage.setOnClickListener { selectImageFromGallery() }

        // Ambil foto dari kamera
        btnTakePhoto.setOnClickListener { takePhotoFromCamera() }

        // Simpan data
        btnSave.setOnClickListener { saveFriend() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            etBirthDate.setText(dateFormat.format(selectedDate.time))
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    imageUri = data?.data
                    ivFriendImage.setImageURI(imageUri)
                }
                REQUEST_CAMERA -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    imageUri = getImageUri(bitmap)
                    ivFriendImage.setImageURI(imageUri)
                }
            }
        }
    }

    private fun uriToByteArray(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: byteArrayOf()
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "IMG_${System.currentTimeMillis()}", null)
        return Uri.parse(path)
    }

    private fun saveFriend() {
        val name = etName.text.toString().trim()
        val birthDate = etBirthDate.text.toString().trim()

        if (name.isEmpty() || birthDate.isEmpty()) {
            Toast.makeText(this, "Nama dan tanggal lahir tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        val imageData = imageUri?.let { uriToByteArray(it) }

//        RetrofitClient.instance.addFriend(name, birthDate, imageData).enqueue(object : Callback<Friend> {
//            override fun onResponse(call: Call<Friend>, response: Response<Friend>) {
//                if (response.isSuccessful) {
//                    Toast.makeText(this@AddFriendActivity, "Teman berhasil ditambahkan", Toast.LENGTH_SHORT).show()
//                    finish()
//                } else {
//                    Toast.makeText(this@AddFriendActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<Friend>, t: Throwable) {
//                Toast.makeText(this@AddFriendActivity, "Gagal menambahkan teman", Toast.LENGTH_SHORT).show()
//            }
//        })
    }

    private fun getRandomQuote(): String {
        val quotes = listOf(
            "Hidup adalah petualangan berani atau tidak sama sekali.",
            "Jangan takut gagal, takutlah untuk tidak mencoba.",
            "Kesuksesan dimulai dari langkah kecil yang konsisten.",
            "Teman sejati adalah mereka yang datang saat dunia menjauh.",
            "Bahagia bukan soal memiliki semua, tapi bersyukur atas yang kita miliki."
        )
        return quotes.random()
    }

    companion object {
        private const val REQUEST_GALLERY = 100
        private const val REQUEST_CAMERA = 101
    }
}
