package com.banny.open_llm.openai.model

data class ChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<ChatChoice>,
    val usage: TokenUsage?,
    val serviceTier: String?,
    val systemFingerprint: String?
)

data class ChatChoice(
    val index: Int,
    val message: ChatMessage,
    val finishReason: String?
)

data class TokenUsage(
    val promptTokens: Long,
    val completionTokens: Long,
    val totalTokens: Long
)
