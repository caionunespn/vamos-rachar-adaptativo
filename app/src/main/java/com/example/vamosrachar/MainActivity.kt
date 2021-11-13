package com.example.vamosrachar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.view.View
import android.speech.tts.TextToSpeech
import android.util.Log
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private lateinit var moneyValue: EditText
    private lateinit var peopleValue: EditText
    private lateinit var peopleError: TextView
    private lateinit var moneyError: TextView
    private lateinit var result: TextView
    private lateinit var speakFab: View
    private lateinit var shareFab: View
    private var amount = 0.0;
    private var people = 0;
    private var divisionResult = "R$ 0,00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moneyValue = findViewById<EditText>(R.id.money_value)
        moneyError = findViewById<TextView>(R.id.money_error)
        peopleValue = findViewById<EditText>(R.id.people_value)
        peopleError = findViewById<TextView>(R.id.people_error)
        result = findViewById<TextView>(R.id.result)
        speakFab = findViewById<View>(R.id.speak_button)
        shareFab = findViewById<View>(R.id.share_button)

        moneyValue.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                try {
                    val moneyAmount = editable.toString().toDouble()
                    moneyError.setText("")
                    amount = moneyAmount
                } catch (nfe: NumberFormatException) {
                    return moneyError.setText("Valor inválido")
                }
                calculateDivision()
            }
        })

        peopleValue.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                try {
                    val peopleAmount = editable.toString().toInt()
                    peopleError.setText("")
                    people = peopleAmount
                } catch (nfe: NumberFormatException) {
                    return peopleError.setText("Valor inválido")
                }
                calculateDivision()
            }
        })

        speakFab!!.isEnabled = false;
        tts = TextToSpeech(this,this)

        speakFab!!.setOnClickListener { onSpeak() }
        shareFab!!.setOnClickListener { onShare() }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val port = Locale("pt", "BR")
            var result = tts!!.setLanguage(port);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                result = tts!!.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "A lingua específicada não é suportada")
                } else {
                    speakFab!!.isEnabled = true
                }
            } else {
                speakFab!!.isEnabled = true
            }
        } else {
            Log.e("TTS", "Falha na inicialização do Text do Speech")
        }
    }

    public override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    private fun calculateDivision() {
        if (people == 0) {
            peopleError.setText("Você precisa ter no mínimo uma pessoa para dividir")
        } else {
            peopleError.setText("")
            val division = String.format("%.2f", amount / people);
            val resultString = "R$ ${division}"
            divisionResult = resultString
            result.setText(resultString)
        }
    }

    private fun onSpeak() {
        tts!!.speak(divisionResult, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun onShare() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Vamos Rachar !\n\nQtde de Pessoas: ${people}\nValor Total: R$ ${amount}\n\nO valor a ser rachado é: ${divisionResult}")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}