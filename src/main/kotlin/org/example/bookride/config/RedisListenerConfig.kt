package org.example.bookride.config

import org.example.bookride.listener.RedisMessageListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter

@Configuration
class RedisListenerConfig(
    val redisMessageListener: RedisMessageListener,
) {
    @Bean
    fun topic(): PatternTopic = PatternTopic("__keyevent@*__:expired")

    @Bean
    fun redisContainer(lettuceConnectionFactory: LettuceConnectionFactory): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(lettuceConnectionFactory)
        container.addMessageListener(messageListener(), topic())
        container.isRunning
        return container
    }

    @Bean
    fun messageListener(): MessageListenerAdapter = MessageListenerAdapter(redisMessageListener)
}
