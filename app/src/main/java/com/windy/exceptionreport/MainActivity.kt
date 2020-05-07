package com.windy.exceptionreport

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : Activity() {

    private lateinit var btnSend : MaterialButton
    private lateinit var txtMessage : TextInputEditText
    private lateinit var txtPhoneNumber1 : TextInputEditText
    private lateinit var txtPhoneNumber2 : TextInputEditText
    private lateinit var txtPhoneNumber3 : TextInputEditText
    private lateinit var txtPhoneNumber4 : TextInputEditText
    private lateinit var txtPhoneNumber5 : TextInputEditText
    private lateinit var txtPhoneNumber6 : TextInputEditText
    private lateinit var txtPhoneNumber7 : TextInputEditText
    private lateinit var txtPhoneNumber8 : TextInputEditText
    private lateinit var txtPhoneNumber9 : TextInputEditText
    private lateinit var txtPhoneNumber10 : TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSend = findViewById(R.id.btn_send)
        txtMessage = findViewById(R.id.txt_message)
        txtPhoneNumber1 = findViewById(R.id.txt_phone_number1)
        txtPhoneNumber2 = findViewById(R.id.txt_phone_number2)
        txtPhoneNumber3 = findViewById(R.id.txt_phone_number3)
        txtPhoneNumber4 = findViewById(R.id.txt_phone_number4)
        txtPhoneNumber5 = findViewById(R.id.txt_phone_number5)
        txtPhoneNumber6 = findViewById(R.id.txt_phone_number6)
        txtPhoneNumber7 = findViewById(R.id.txt_phone_number7)
        txtPhoneNumber8 = findViewById(R.id.txt_phone_number8)
        txtPhoneNumber9 = findViewById(R.id.txt_phone_number9)
        txtPhoneNumber10 = findViewById(R.id.txt_phone_number10)
    }

    override fun onStart() {
        super.onStart()

        if (!isAccessibilityOn(this@MainActivity, WhatsAppAccessibilityService::class.java)) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            return
        }

        btnSend.setOnClickListener {
            val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.getWindowToken(), 0)


            val listPhoneNumbers : List<String> =
                mutableListOf(
                    txtPhoneNumber1.text.toString(), txtPhoneNumber2.text.toString(), txtPhoneNumber3.text.toString(),
                    txtPhoneNumber4.text.toString(), txtPhoneNumber5.text.toString(), txtPhoneNumber6.text.toString(),
                    txtPhoneNumber7.text.toString(), txtPhoneNumber8.text.toString(), txtPhoneNumber9.text.toString(),
                    txtPhoneNumber10.text.toString()
                )

            sendMessage(listPhoneNumbers, txtMessage.text.toString())
        }
    }

    private fun sendMessage(listPhoneNumbers : List<String>, message : String) {
        try {
            for (phoneNumber in listPhoneNumbers) {
                if (!phoneNumber.equals("", ignoreCase = true)) {
                    Log.i("MainActivity", "Phone number = $phoneNumber"+" Message = $message" )

                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra(Intent.EXTRA_TEXT, message)
                    intent.putExtra("jid", "$phoneNumber@s.whatsapp.net")
                    intent.setPackage("com.whatsapp")
                    startActivity(intent)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isAccessibilityOn(context: Context, clazz: Class<out AccessibilityService?>): Boolean {
        var accessibilityEnabled = 0
        val service = context.packageName + "/" + clazz.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }

        val colonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                colonSplitter.setString(settingValue)
                while (colonSplitter.hasNext()) {
                    val accessibilityService = colonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }

        return false
    }
}
