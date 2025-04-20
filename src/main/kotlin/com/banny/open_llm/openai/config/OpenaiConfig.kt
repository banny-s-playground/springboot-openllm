package com.banny.open_llm.openai.config

import com.theokanning.openai.service.OpenAiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class OpenaiConfig {

    @Value("\${openai.api.key}")
    private lateinit var apiKey: String
    
    @Value("\${openai.api.timeout:30000}")
    private var timeout: Long = 30000
    
    @Bean
    fun openAiService(): OpenAiService {
        return OpenAiService(apiKey, Duration.ofMillis(timeout))
    }
}
