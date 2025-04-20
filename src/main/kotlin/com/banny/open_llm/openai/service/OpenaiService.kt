package com.banny.open_llm.openai.service

import com.banny.open_llm.openai.model.*
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage as OpenAiChatMessage
import com.theokanning.openai.service.OpenAiService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class OpenaiService(private val openAiService: OpenAiService) {

    private val logger = LoggerFactory.getLogger(OpenaiService::class.java)

    @Value("\${openai.api.model}")
    private lateinit var defaultModel: String

    fun createChatCompletion(chatRequest: ChatRequest): ChatResponse {
        logger.info("### [OpenaiService.createChatCompletion] Preparing OpenAI request with model: {}", chatRequest.model ?: defaultModel)
        
        val messages = chatRequest.messages.map { message ->
            logger.trace("Message [{}]: {}", message.role, message.content)
            OpenAiChatMessage(message.role, message.content)
        }

        val completionRequest = ChatCompletionRequest.builder()
            .model(chatRequest.model ?: defaultModel)
            .messages(messages)
            .apply {
                chatRequest.temperature?.let { 
                    logger.debug("Setting temperature: {}", it)
                    this.temperature(it) 
                }
                chatRequest.maxTokens?.let { 
                    logger.debug("Setting maxTokens: {}", it)
                    this.maxTokens(it) 
                }
            }
            .build()

        logger.info("[OpenaiService.createChatCompletion > openAiService.createChatCompletion] Sending request to OpenAI API: model={}, messages_count={}",
            completionRequest.model, 
            completionRequest.messages.size
        )
        
        val startTime = System.currentTimeMillis()
        val chatCompletion = openAiService.createChatCompletion(completionRequest)
        val endTime = System.currentTimeMillis()
        
        logger.info("[OpenaiService.createChatCompletion] Received response from OpenAI in {}ms", endTime - startTime)
        logger.info("[OpenaiService.createChatCompletion] Received response from OpenAI, chatCompletion is {}", chatCompletion)

        /**
         * {
         *   "id": "chatcmpl-B9MBs8CjcvOU2jLn4n570S5qMJKcT",
         *   "object": "chat.completion",
         *   "created": 1741569952,
         *   "model": "gpt-4.1-2025-04-14",
         *   "choices": [
         *     {
         *       "index": 0,
         *       "message": {
         *         "role": "assistant",
         *         "content": "Hello! How can I assist you today?",
         *         "refusal": null,
         *         "annotations": []
         *       },
         *       "logprobs": null,
         *       "finish_reason": "stop"
         *     }
         *   ],
         *   "usage": {
         *     "prompt_tokens": 19,
         *     "completion_tokens": 10,
         *     "total_tokens": 29,
         *     "prompt_tokens_details": {
         *       "cached_tokens": 0,
         *       "audio_tokens": 0
         *     },
         *     "completion_tokens_details": {
         *       "reasoning_tokens": 0,
         *       "audio_tokens": 0,
         *       "accepted_prediction_tokens": 0,
         *       "rejected_prediction_tokens": 0
         *     }
         *   },
         *   "service_tier": "default"
         * }
         *
         */

        // Map OpenAI response to our model
        val choices = chatCompletion.choices.map { choice ->
            logger.trace("Choice {}: role={}, content={}", 
                choice.index, 
                choice.message.role, 
                choice.message.content.take(50) + if (choice.message.content.length > 50) "..." else ""
            )
            
            ChatChoice(
                index = choice.index,
                message = ChatMessage(choice.message.role, choice.message.content),
                finishReason = choice.finishReason
            )
        }

        chatCompletion.usage?.let {
            logger.info("Token usage: prompt={}, completion={}, total={}", 
                it.promptTokens, 
                it.completionTokens, 
                it.totalTokens
            )
        }

        return ChatResponse(
            id = chatCompletion.id,
            chatCompletion.`object`,
            choices = choices,
            created = chatCompletion.created,
            model = chatCompletion.model,
            usage = chatCompletion.usage?.let {
                TokenUsage(
                    promptTokens = it.promptTokens,
                    completionTokens = it.completionTokens,
                    totalTokens = it.totalTokens
                )
            },
            serviceTier = null,
            systemFingerprint = null
        )
    }
}
