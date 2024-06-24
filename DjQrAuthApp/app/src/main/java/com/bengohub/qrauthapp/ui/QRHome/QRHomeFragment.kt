package com.bengohub.qrauthapp.ui.QRHome

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bengohub.qrauthapp.databinding.FragmentQrhomeBinding
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import okio.ByteString
import java.io.IOException

class QRHomeFragment : Fragment() {

    private var _binding: FragmentQrhomeBinding? = null
    private val binding get() = _binding!!

    private var webSocket: WebSocket? = null
    private var useWebSocket = false // Flag to determine WebSocket usage
    private val TAG= "QRHomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrhomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val scanButton: Button = binding.scanButton

        // Button click listener to initiate QR code scanning
        scanButton.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan the QR code")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }

        // Example toggle button to switch between HTTP and WebSocket
        val toggleButton: Button = binding.toggleButton
        toggleButton.setOnClickListener {
            useWebSocket = !useWebSocket
            val protocol = if (useWebSocket) "WebSocket" else "HTTP"
            Toast.makeText(context, "Using $protocol", Toast.LENGTH_SHORT).show()

            // Connect or disconnect WebSocket based on selection
            if (useWebSocket) {
                // Check if WebSocket is already connected
                if (webSocket == null) {
                    webSocketAuthenticate("Initial") // Replace with actual session ID if needed
                } else {
                    Log.d(TAG, "WebSocket already connected")
                }
            } else {
                disconnectWebSocket()
            }
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        val scanResult: TextView = binding.scanResult

        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
                scanResult.text = "Cancelled"
            } else {
                val sessionId = result.contents // Get the session ID from QR scan result
                scanResult.text = "Auth Key: $sessionId"
                authenticate(sessionId) // Authenticate with the session ID
                sendWebSocketMessage(sessionId, "Scanned")
            }
        }
    }

    private fun authenticate(sessionId: String) {
        if (useWebSocket) {
            // Use WebSocket for authentication
            webSocketAuthenticate(sessionId)
        } else {
            // Use HTTP for authentication
            authenticateWithHttp(sessionId)
        }
    }

    private fun authenticateWithHttp(sessionId: String) {
        val sharedPreferences = activity?.getSharedPreferences("AppSettings", Activity.MODE_PRIVATE)
        val apiEndpoint = sharedPreferences?.getString("api_endpoint", "")

        if (apiEndpoint.isNullOrEmpty()) {
            Toast.makeText(context, "API endpoint not set", Toast.LENGTH_LONG).show()
            return
        }
        val client = OkHttpClient()
        val url = "$apiEndpoint/authenticate/$sessionId/"

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, ByteArray(0)))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    activity?.runOnUiThread {
                        binding.scanResult.text = "Authentication successful"
                        Toast.makeText(context, "Authentication successful", Toast.LENGTH_LONG).show()
                    }
                } else {
                    activity?.runOnUiThread {
                        binding.scanResult.text = "Authentication failed"
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun webSocketAuthenticate(sessionId: String) {
        val sharedPreferences = activity?.getSharedPreferences("AppSettings", Activity.MODE_PRIVATE)
        val wsEndpoint = sharedPreferences?.getString("ws_endpoint", "")

        if (wsEndpoint.isNullOrEmpty()) {
            Toast.makeText(context, "WebSocket endpoint not set", Toast.LENGTH_LONG).show()
            return
        }

        val request = Request.Builder()
            .url("$wsEndpoint/ws/auth/$sessionId/")
            .build()

        val status = if (sessionId == "Initial") "Initial" else "Scanned"
        webSocket = OkHttpClient().newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "WebSocket connection opened")
                // You may send initial authentication message here if needed
                sendWebSocketMessage(sessionId,status)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d(TAG, "WebSocket message received: $text")
                handleWebSocketMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                Log.d(TAG, "WebSocket bytes message received: $bytes")
                // Handle bytes message if needed
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "WebSocket closing: $code / $reason")
                // Handle WebSocket closing if needed
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e(TAG, "WebSocket failure: ${t.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "WebSocket authentication failed", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun disconnectWebSocket() {
        webSocket?.cancel()
        webSocket = null
        Log.d(TAG, "WebSocket disconnected")
    }

    private fun handleWebSocketMessage(message: String) {
        // Implement WebSocket message handling logic here
        // Example: Parse JSON message and update UI accordingly
        activity?.runOnUiThread {
            binding.scanResult.text = message // Example: Update UI with WebSocket message
        }
    }

    private fun sendWebSocketMessage(sessionId: String,status: String) {
        // Create a message to send over WebSocket
        val message = mapOf("status" to status, "key" to sessionId, "message" to "QR code scanned: $sessionId")
        val gson = Gson()
        val jsonMessage = gson.toJson(message)

        // Check if WebSocket is connected before sending the message
        webSocket?.send(jsonMessage)
        activity?.runOnUiThread {
            binding.scanResult.text = sessionId // Example: Update UI with WebSocket message
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        disconnectWebSocket() // Ensure WebSocket is disconnected when fragment is destroyed
        _binding = null
    }

    companion object {
        private const val TAG = "QRHomeFragment"
    }
}
