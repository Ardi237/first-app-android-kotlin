package com.example.ardi_project

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.Locale
import java.lang.Exception
import android.widget.Toast


class Calculator : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var resultTextView: EditText
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        editText = findViewById(R.id.editText)
        resultTextView = findViewById(R.id.resultTextView)

        // Inisialisasi TextToSpeech
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val langResult = tts.setLanguage(Locale.US)
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported or missing data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Initialization failed", Toast.LENGTH_SHORT).show()
            }
        }

        setupInputChangeListener()

        val buttonIds = arrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
            R.id.buttonDot, R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply,
            R.id.buttonDivide, R.id.buttonBr1, R.id.buttonBr2, R.id.backspace,
            R.id.AC, R.id.buttonEquals
        )

        for (buttonId in buttonIds) {
            val button = findViewById<Button>(buttonId)
            if (buttonId in arrayOf(
                    R.id.buttonDot, R.id.button0, R.id.button1, R.id.button2,
                    R.id.button3, R.id.button4, R.id.button5, R.id.button6,
                    R.id.button7, R.id.button8, R.id.button9
                )
            ) {
                val color = ContextCompat.getColor(this, R.color.navi)
                val drawable = ContextCompat.getDrawable(this, R.drawable.rounded_button)
                drawable?.setColorFilter(color, PorterDuff.Mode.SRC)
                button.background = drawable
            }

            if (buttonId in arrayOf(
                    R.id.buttonEquals, R.id.buttonAdd, R.id.buttonSubtract,
                    R.id.buttonMultiply, R.id.buttonDivide
                )
            ) {
                val color = ContextCompat.getColor(this, R.color.yellow)
                val drawable = ContextCompat.getDrawable(this, R.drawable.rounded_button)
                drawable?.setColorFilter(color, PorterDuff.Mode.SRC)
                button.background = drawable
            }

            if (buttonId in arrayOf(R.id.backspace, R.id.AC)) {
                val color = ContextCompat.getColor(this, R.color.red)
                val drawable = ContextCompat.getDrawable(this, R.drawable.rounded_button)
                drawable?.setColorFilter(color, PorterDuff.Mode.SRC)
                button.background = drawable
            }

            button.setOnClickListener { onButtonClick(it) }
        }

        findViewById<Button>(R.id.buttonEquals).setOnClickListener {
            onEqualsButtonClick()
        }

        findViewById<Button>(R.id.buttonExit).setOnClickListener { navigateToMainActivity() }
    }

    private fun setupInputChangeListener() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val expression = s.toString()
                try {
                    val result = evaluateExpression(expression)
                    resultTextView.setText(result.toString())
                } catch (e: Exception) {
                    resultTextView.text.clear()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun onButtonClick(view: View) {
        val button = view as Button
        val buttonText = button.text.toString()
        val currentText = editText.text.toString()

        when (buttonText) {
            "AC" -> editText.setText("")
            "C" -> {
                if (currentText.isNotEmpty()) {
                    editText.setText(currentText.dropLast(1))
                }
            }
            else -> editText.setText(currentText + buttonText)
        }
        
        speakResult()
    }


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

    private fun evaluateExpression(expression: String): Int {
        return ExpressionBuilder(expression).build().evaluate().toInt()
    }

    private fun speakResult() {
        val resultText = resultTextView.text.toString()
        if (resultText.isNotEmpty()) {
            tts.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}