package com.example.aisearchhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load API Key from secrets.xml


    }
}

@Composable
fun ChatScreen(apiKey: String) {
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var responseText by remember { mutableStateOf("Ask something to Gemini!") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("AI Search Hub", fontSize = 24.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                ) {
                    if (userInput.text.isEmpty()) {
                        Text("Type a message...", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                GeminiService.queryGemini(userInput.text, apiKey) { response ->
                    responseText = response
                    isLoading = false
                }
            },
            enabled = userInput.text.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Chat with Gemini")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Text("Fetching response...", color = Color.Gray)
        } else {
            Text(responseText, fontSize = 16.sp, color = Color.Black)
        }
    }
}

internal object GeminiService {
    private val client = OkHttpClient()

    fun queryGemini(userInput: String, apiKey: String, callback: (String) -> Unit) {
        val apiUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateText?key=$apiKey"

        val mediaType = "application/json".toMediaTypeOrNull()
        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().put(
                JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", userInput)))
                }
            ))
        }.toString()

        val requestBody = jsonBody.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    try {
                        val jsonResponse = JSONObject(it)
                        val candidates = jsonResponse.optJSONArray("candidates")
                        val firstCandidate = candidates?.optJSONObject(0)
                        val result = firstCandidate?.optJSONArray("content")?.optString(0, "No response from Gemini")
                            ?: "No response"

                        callback(result)
                    } catch (e: Exception) {
                        callback("Error parsing response: ${e.message}")
                    }
                } ?: callback("Empty response from server")
            }
        })
    }
}
