package org.example.bookride.config

import com.uber.h3core.H3Core
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class H3Config {
    @Bean
    fun h3Core(): H3Core = H3Core.newInstance()
}
