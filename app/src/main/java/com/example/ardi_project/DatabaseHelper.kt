package com.example.ardi_project

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.ardi_project.models.Book

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "user_db"
        private const val TABLE_USER = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        // Table Books
        private const val TABLE_BOOKS = "books"
        private const val COLUMN_BOOK_ID = "id"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_NAMA_PANGGILAN = "namaPanggilan"
        private const val COLUMN_PHOTO_URI = "photoUri"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ALAMAT = "alamat"
        private const val COLUMN_TGL_LAHIR = "tanggalLahir"
        private const val COLUMN_NO_HP = "noHp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_USER ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT, $COLUMN_PASSWORD TEXT)")
        db?.execSQL(createTable)

        val createBooksTable = """
            CREATE TABLE $TABLE_BOOKS (
                $COLUMN_BOOK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAMA TEXT NOT NULL,
                $COLUMN_NAMA_PANGGILAN TEXT NOT NULL,
                $COLUMN_PHOTO_URI TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_ALAMAT TEXT,
                $COLUMN_TGL_LAHIR TEXT NOT NULL,
                $COLUMN_NO_HP TEXT NOT NULL
            )
        """.trimIndent()

        db?.execSQL(createBooksTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        onCreate(db)
    }

    fun setUserLoggedIn(status: Boolean) {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", status)
            apply()
        }
    }

    fun isUserLoggedIn(): Boolean {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }





    // adduser baru ke database
    fun addUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)

        val result = db.insert(TABLE_USER, null, values)
        db.close()
        return result != -1L
    }


    fun isUserExist(): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_USER"
        val cursor = db.rawQuery(query, null)

        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }

        cursor.close()
        db.close()
        return exists
    }

    // Cek login user
    fun checkLogin(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))

        val isValid = cursor.count > 0
        cursor.close()
        db.close()
        Log.d("DatabaseHelper", "Login attempt: Username = $username, Password = $password, Success = $isValid")
        return isValid
    }

    fun logoutUser() {
        setUserLoggedIn(false)  // Tandai pengguna sebagai logout di SharedPreferences
    }

    // Menambahkan buku baru
    fun addBook(book: Book): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA, book.nama)
            put(COLUMN_NAMA_PANGGILAN, book.namaPanggilan)
            put(COLUMN_PHOTO_URI, book.photoUri)
            put(COLUMN_EMAIL, book.email)
            put(COLUMN_ALAMAT, book.alamat)
            put(COLUMN_TGL_LAHIR, book.tanggalLahir)
            put(COLUMN_NO_HP, book.noHp)
        }
        val result = db.insert(TABLE_BOOKS, null, values)
        db.close()
        return result != -1L
    }

    // Mengambil semua buku
    fun getAllBooks(): MutableList<Book> {
        val booksList = mutableListOf<Book>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_BOOKS"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val book = Book(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                    nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA)),
                    namaPanggilan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_PANGGILAN)),
                    photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)),
                    tanggalLahir = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TGL_LAHIR)),
                    noHp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NO_HP))
                )
                booksList.add(book)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return booksList
    }

    // Mendapatkan buku berdasarkan ID
    fun getBookById(bookId: Int): Book? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_BOOKS WHERE $COLUMN_BOOK_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(bookId.toString()))

        var book: Book? = null

        if (cursor.moveToFirst()) {
            book = Book(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID)),
                nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA)),
                namaPanggilan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_PANGGILAN)),
                photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)),
                tanggalLahir = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TGL_LAHIR)),
                noHp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NO_HP))
            )

            // Log for debugging
            Log.d("DatabaseHelper", "Retrieved book with ID: $bookId, Name: ${book.nama}, PhotoURI: ${book.photoUri}")
        }

        cursor.close()
        db.close()
        return book
    }

    // Mengupdate data buku
    fun updateBook(book: Book): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA, book.nama)
            put(COLUMN_NAMA_PANGGILAN, book.namaPanggilan)
            put(COLUMN_PHOTO_URI, book.photoUri)
            put(COLUMN_EMAIL, book.email)
            put(COLUMN_ALAMAT, book.alamat)
            put(COLUMN_TGL_LAHIR, book.tanggalLahir)
            put(COLUMN_NO_HP, book.noHp)
        }

        // Log for debugging
        Log.d("DatabaseHelper", "Updating book ID: ${book.id}, Name: ${book.nama}, PhotoURI: ${book.photoUri}")

        val result = db.update(TABLE_BOOKS, values, "$COLUMN_BOOK_ID = ?", arrayOf(book.id.toString()))
        db.close()
        return result > 0
    }

    // Menghapus data buku berdasarkan ID
    fun deleteBook(bookId: Int): Boolean {
        val db = writableDatabase
        // Log for debugging
        Log.d("DatabaseHelper", "Deleting book with ID: $bookId")

        val result = db.delete(TABLE_BOOKS, "$COLUMN_BOOK_ID = ?", arrayOf(bookId.toString()))
        db.close()
        return result > 0
    }

//    fun deleteAllUsers(): Boolean {
//        val db = this.writableDatabase
//        val result = db.delete(TABLE_USER, null, null)
//        db.close()
//        return result > 0
//    }
}