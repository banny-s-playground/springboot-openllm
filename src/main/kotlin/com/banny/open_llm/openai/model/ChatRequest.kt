package com.banny.open_llm.openai.model

data class ChatRequest(
    val messages: List<ChatMessage>,
    val model: String? = null,
    val temperature: Double? = null,
    val maxTokens: Int? = null
)

data class ChatMessage(
    val role: String,
    val content: String
)
