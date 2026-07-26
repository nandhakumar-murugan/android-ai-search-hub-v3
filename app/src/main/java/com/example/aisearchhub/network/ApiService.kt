package com.example.aisearchhub.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Call

// API Response Model
data class AIResponse(val candidates: List<Candidate>)
data class Candidate(val content: Content)
data class Content(val parts: List<Part>)
data class Part(val text: String)

// API Request Model
data class AIRequest(val contents: List<ContentRequest>)
data class ContentRequest(val role: String, val parts: List<PartRequest>)
data class PartRequest(val text: String)

// Retrofit API Service
interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-1.5-pro:generateContent")
    fun getAIResponse(@Body request: AIRequest): Call<AIResponse>
}
