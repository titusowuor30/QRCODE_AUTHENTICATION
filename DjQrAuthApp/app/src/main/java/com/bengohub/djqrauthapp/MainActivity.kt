// MainActivity.kt

package com.bengohub.djqrauthapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

class MainActivity : Activity() {

    private lateinit var scanResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanResult = findViewById(R.id.scan_result)
        val scanButton: Button = findViewById(R.id.scan_button)

        scanButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan the QR code")
            integrator.setCameraId(0) // Use default camera
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }

        val menuButton: ImageButton = findViewById(R.id.nav_right_button)
        menuButton.setOnClickListener {
            val intent = Intent(this, ApiSettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                scanResult.text = "Scan cancelled"
            } else {
                scanResult.text = "Auth Key: ${result.contents}"
                println(result.contents)
                authenticate(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun authenticate(key: String) {
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val apiEndpoint = sharedPreferences.getString("api_endpoint", "")

        if (apiEndpoint.isNullOrEmpty()) {
            Toast.makeText(this, "API endpoint not set", Toast.LENGTH_LONG).show()
            return
        }
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, ByteArray(0))

        val request = Request.Builder()
            .url("$apiEndpoint/authenticate/$key/")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println(e.message)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Authentication failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                println(response.body)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Authentication successful", Toast.LENGTH_LONG).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Authentication failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
