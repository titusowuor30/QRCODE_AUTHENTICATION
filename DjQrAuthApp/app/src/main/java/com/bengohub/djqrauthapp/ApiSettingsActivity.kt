package com.bengohub.djqrauthapp

// ApiSettingsActivity.kt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ApiSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_settings)

        val apiEndpointEditText: EditText = findViewById(R.id.api_endpoint)
        val saveButton: Button = findViewById(R.id.save_button)

        // Load saved API endpoint from shared preferences or database
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val savedApiEndpoint = sharedPreferences.getString("api_endpoint", "")
        apiEndpointEditText.setText(savedApiEndpoint)

        saveButton.setOnClickListener {
            val newApiEndpoint = apiEndpointEditText.text.toString()
            if (newApiEndpoint.isNotEmpty()) {
                // Save the new API endpoint to shared preferences or database
                val editor = sharedPreferences.edit()
                editor.putString("api_endpoint", newApiEndpoint)
                editor.apply()

                Toast.makeText(this, "API endpoint saved", Toast.LENGTH_SHORT).show()
                finish()  // Close the activity
            } else {
                Toast.makeText(this, "Please enter a valid API endpoint", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
