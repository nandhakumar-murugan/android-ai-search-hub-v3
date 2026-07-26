package com.example.aisearchhub.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(aiViewModel: AIViewModel = viewModel()) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    val apiKey = "AIzaSyBs6xPvmfdoNMPPORUGrf-bfXWo3-3usy0"

    Column(modifier = Modifier.padding(16.dp)) {
        Text("AI Search Hub", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(10.dp))

        BasicTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            if (query.isNotEmpty()) {
                aiViewModel.fetchAIResponse(apiKey, query) { result ->
                    response = result
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Chat with Gemini")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Response: $response")
    }
}
