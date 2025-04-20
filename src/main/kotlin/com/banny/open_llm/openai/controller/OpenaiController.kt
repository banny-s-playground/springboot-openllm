package com.banny.open_llm.openai.controller

import com.banny.open_llm.openai.model.ChatRequest
import com.banny.open_llm.openai.model.ChatResponse
import com.banny.open_llm.openai.service.OpenaiService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/openai")
class OpenaiController(private val openaiService: OpenaiService) {
    
    private val logger = LoggerFactory.getLogger(OpenaiController::class.java)

    @PostMapping("/chat")
    fun createChatCompletion(@RequestBody chatRequest: ChatRequest): ResponseEntity<ChatResponse> {
        logger.info("[OpenaiController.createChatCompletion.Request] Received chat request: model={}, messages={}, temperature={}, maxTokens={}",
            chatRequest.model ?: "default", 
            chatRequest.messages.map { "${it.role}: ${it.content.take(50)}${if (it.content.length > 50) "..." else ""}" },
            chatRequest.temperature,
            chatRequest.maxTokens
        )
        
        val response = openaiService.createChatCompletion(chatRequest)
        
        logger.info("[OpenaiController.createChatCompletion.Response] Chat completion response: id={}, model={}, totalTokens={}",
            response.id,
            response.model,
            response.usage?.totalTokens
        )
        
        return ResponseEntity.ok(response)
    }
}
