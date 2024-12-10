package com.example.constraintlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() , TextWatcher, TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var valorConta: EditText
    private lateinit var numPessoas: EditText
    private lateinit var showResult: TextView
    private lateinit var btnShare: FloatingActionButton
    private var ttsSucess: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        valorConta = findViewById<EditText>(R.id.valorConta)
        numPessoas = findViewById<EditText>(R.id.numPessoas)
        showResult = findViewById<TextView>(R.id.showResult)
        btnShare = findViewById<FloatingActionButton>(R.id.shareActionButton)

        valorConta.addTextChangedListener(this)
        numPessoas.addTextChangedListener(this)

        showResult.text = getString(R.string.result_default)

        tts = TextToSpeech(this, this)

        btnShare.setOnClickListener {
            val resultText = showResult.text.toString()
            compartilharResultado(resultText)
        }

    }

    private fun calcularResultado() {

        val totalValor = valorConta.text.toString().toDoubleOrNull()
        val numPessoas = numPessoas.text.toString().toIntOrNull()

        if (totalValor == null || numPessoas == null || numPessoas <= 0) {
            showResult.text = getString(R.string.error_empty_values)
            return
        }

        val result = totalValor / numPessoas

        showResult.text = getString(R.string.result_message, result)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
       Log.d("PDM24","Antes de mudar")

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d("PDM24","Mudando")
        calcularResultado()
    }

    override fun afterTextChanged(s: Editable?) {
        Log.d ("PDM24", "Depois de mudar")

        val valor: Double

        if(s.toString().length>0) {
             valor = s.toString().toDouble()
            Log.d("PDM24", "v: " + valor)
        }
    }



    fun clickFalar(v: View){
        if (tts.isSpeaking) {
            tts.stop()
        }
        if(ttsSucess) {
            val resultText = showResult.text.toString()

            if (resultText.isBlank() || resultText == getString(R.string.error_empty_values) || resultText == getString(R.string.result_default)) {
                Toast.makeText(this, getString(R.string.error_speak), Toast.LENGTH_SHORT).show()
                return
            } else {
                Log.d ("PDM23", tts.language.toString())
                tts.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    fun compartilharResultado (resultText: String) {

            if (resultText.isBlank() || resultText == getString(R.string.error_empty_values) || resultText == getString(R.string.result_default)) {
                Toast.makeText(this, getString(R.string.error_share), Toast.LENGTH_SHORT).show()
                return
            }

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${getString(R.string.result_base)} $resultText")
                type = "text/plain"
            }

            startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))

    }

    override fun onDestroy() {
            tts.stop()
            tts.shutdown()
            super.onDestroy()
        }

    override fun onInit(status: Int) {
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.getDefault()
                ttsSucess=true
                Log.d("PDM23","Sucesso na Inicialização")
            } else {
                Log.e("PDM23", "Failed to initialize TTS engine.")
                ttsSucess=false
            }
        }


}

