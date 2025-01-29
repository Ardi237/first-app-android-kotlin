package com.example.ardi_project

import android.content.Intent
import android.graphics.PorterDuff
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import net.objecthunter.exp4j.ExpressionBuilder

class Calculator : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var resultTextView: EditText
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0  // ID untuk suara yang dimuat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        // Inisialisasi input dan output
        editText = findViewById(R.id.editText)
        resultTextView = findViewById(R.id.resultTextView)

        setupInputChangeListener()
        setupButtons()
        setupSoundPool()
    }

    // Setup SoundPool untuk memainkan suara dengan variasi pitch
    private fun setupSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load file suara dari res/raw/sound.wav
        soundId = soundPool.load(this, R.raw.sound, 1)
    }

    // Setup listener untuk mendeteksi perubahan input
    private fun setupInputChangeListener() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val expression = s.toString()
                    val result = evaluateExpression(expression)
                    resultTextView.setText(result.toString())
                } catch (e: Exception) {
                    resultTextView.text.clear()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Setup tombol-tombol kalkulator
    private fun setupButtons() {
        val buttonIds = arrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
            R.id.buttonDot, R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply,
            R.id.buttonDivide, R.id.buttonBr1, R.id.buttonBr2, R.id.backspace,
            R.id.AC, R.id.buttonEquals
        )

        for ((index, buttonId) in buttonIds.withIndex()) {
            val button = findViewById<Button>(buttonId)
            styleButton(button, buttonId)
            button.setOnClickListener { onButtonClick(it, index) }  // Kirim index untuk pitch
        }

        findViewById<Button>(R.id.buttonEquals).setOnClickListener { onEqualsButtonClick() }
        findViewById<Button>(R.id.buttonExit).setOnClickListener { navigateToMainActivity() }
    }

    // Fungsi untuk memberi gaya pada tombol
    private fun styleButton(button: Button, buttonId: Int) {
        val colorResId = when (buttonId) {
            in arrayOf(
                R.id.buttonDot, R.id.button0, R.id.button1, R.id.button2,
                R.id.button3, R.id.button4, R.id.button5, R.id.button6,
                R.id.button7, R.id.button8, R.id.button9
            ) -> R.color.navi
            in arrayOf(
                R.id.buttonEquals, R.id.buttonAdd, R.id.buttonSubtract,
                R.id.buttonMultiply, R.id.buttonDivide
            ) -> R.color.yellow
            in arrayOf(R.id.backspace, R.id.AC) -> R.color.red
            else -> null
        }

        colorResId?.let {
            val color = ContextCompat.getColor(this, it)
            val drawable = ContextCompat.getDrawable(this, R.drawable.rounded_button)
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC)
            button.background = drawable
        }
    }

    // Logika untuk setiap tombol kalkulator
    private fun onButtonClick(view: View, index: Int) {
        val button = view as Button
        val buttonText = button.text.toString()
        val currentText = editText.text.toString()

        // Ubah pitch berdasarkan tombol yang ditekan
        playSoundWithPitch(index)

        when (buttonText) {
            "AC" -> editText.setText("")
            "C" -> {
                if (currentText.isNotEmpty()) {
                    editText.setText(currentText.dropLast(1))
                }
            }
            else -> editText.setText(currentText + buttonText)
        }
    }

    // Memainkan suara dengan pitch berbeda untuk setiap tombol
    private fun playSoundWithPitch(index: Int) {
        val minPitch = 0.5f  // Nada rendah
        val maxPitch = 2.0f  // Nada tinggi
        val pitch = minPitch + (index.toFloat() / 20) * (maxPitch - minPitch)  // Variasi pitch

        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, pitch)
    }

    // Logika untuk tombol "="
    private fun onEqualsButtonClick() {
        val currentText = editText.text.toString()
        try {
            val result = evaluateExpression(currentText)
            val resultInt = result.toInt()
            resultTextView.text = Editable.Factory.getInstance().newEditable(resultInt.toString())
        } catch (e: Exception) {
            editText.setText("Error: ${e.message}")
        }
    }

    // Evaluasi ekspresi matematika menggunakan ekspresi builder
    private fun evaluateExpression(expression: String): Int {
        return ExpressionBuilder(expression).build().evaluate().toInt()
    }

    // Navigasi ke MainActivity
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Hentikan SoundPool saat aplikasi dihentikan
    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
