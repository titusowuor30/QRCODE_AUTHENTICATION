// ApiSettingsFragment.kt
package com.bengohub.qrauthapp.ui.ApiSettings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bengohub.qrauthapp.R
import com.bengohub.qrauthapp.databinding.FragmentApisettingsBinding

class ApiSettingsFragment : Fragment() {

    private var _binding: FragmentApisettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApisettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val apiEndpointEditText: EditText = binding.apiEndpoint
        val wsEndpointEditText: EditText = binding.wsEndpoint
        val saveButton: Button = binding.saveButton

        // Load saved API endpoint from shared preferences
        val sharedPreferences = activity?.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val savedApiEndpoint = sharedPreferences?.getString("api_endpoint", "http://192.168.8.6:8000")
        val savedWSEndpoint = sharedPreferences?.getString("ws_endpoint", "ws://192.168.8.6:8000")

        apiEndpointEditText.setText(savedApiEndpoint)
        wsEndpointEditText.setText(savedWSEndpoint)

        saveButton.setOnClickListener {
            val newApiEndpoint = apiEndpointEditText.text.toString()
            val newWSEndpoint = wsEndpointEditText.text.toString()
            if (newApiEndpoint.isNotEmpty() && newWSEndpoint.isNotEmpty()) {
                // Save the new API endpoint to shared preferences
                sharedPreferences?.edit()?.apply {
                    putString("api_endpoint", newApiEndpoint)
                    putString("ws_endpoint", newWSEndpoint)
                    apply()
                }
                Toast.makeText(context, "Endpoint settings saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter a valid endpoints", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
