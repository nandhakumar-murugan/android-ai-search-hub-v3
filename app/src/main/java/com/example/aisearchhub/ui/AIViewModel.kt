package com.example.aisearchhub.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisearchhub.network.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AIViewModel : ViewModel() {
    var responseText: String = ""

    fun fetchAIResponse(apiKey: String, query: String, onResult: (String) -> Unit) {
        val request = AIRequest(
            contents = listOf(
                ContentRequest(
                    role = "user",
                    parts = listOf(PartRequest(text = query))
                )
            )
        )

        val call = RetrofitClient.apiService.getAIResponse(request)
        call.enqueue(object : Callback<AIResponse> {
            override fun onResponse(call: Call<AIResponse>, response: Response<AIResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        responseText = it.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response"
                    }
                } else {
                    responseText = "Error: ${response.errorBody()?.string()}"
                }
                onResult(responseText)
            }

            override fun onFailure(call: Call<AIResponse>, t: Throwable) {
                responseText = "Failed: ${t.message}"
                onResult(responseText)
            }
        })
    }
}
