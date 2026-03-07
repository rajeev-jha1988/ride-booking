package org.example.bookride.config

import org.example.bookride.listener.RedisMessageListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.ObjectMapper

@Configuration
class RedisConfig(
    val redisMessageListener: RedisMessageListener,
) {
    // Redis configuration beans would go here

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        // Configure and return RedisConnectionFactory
        val redisConfig = RedisStandaloneConfiguration("localhost", 6379)

        val clientConfig =
            LettucePoolingClientConfiguration
                .builder()
                .build()

        val factory = LettuceConnectionFactory(redisConfig, clientConfig)
        factory.afterPropertiesSet() // Initialize the factory
        return factory
    }

    @Bean
    fun redisTemplate(objectMapper: ObjectMapper): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        val stringSerializer = StringRedisSerializer()

        // 2. Use GenericJacksonJsonRedisSerializer (The modern way)
        val jsonSerializer = GenericJacksonJsonRedisSerializer(objectMapper)

        template.connectionFactory = redisConnectionFactory()
        template.keySerializer = stringSerializer
        template.valueSerializer = jsonSerializer
        template.hashKeySerializer = stringSerializer
        template.hashValueSerializer = jsonSerializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun redisReactiveTemplate(objectMapper: ObjectMapper): ReactiveRedisTemplate<String, Any> {
        val stringSerializer = StringRedisSerializer()
        val jsonSerializer = GenericJacksonJsonRedisSerializer(objectMapper)

        val serializationContext =
            RedisSerializationContext
                .newSerializationContext<String, Any>(stringSerializer)
                .key(stringSerializer)
                .value(jsonSerializer)
                .hashKey(stringSerializer)
                .hashValue(jsonSerializer)
                .build()

        return ReactiveRedisTemplate(redisConnectionFactory(), serializationContext)
    }

    @Bean fun objectMapper(): ObjectMapper = ObjectMapper()

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
