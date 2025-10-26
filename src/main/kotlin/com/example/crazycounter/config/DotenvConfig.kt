package com.example.crazycounter.config

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.DotenvException
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource

/**
 * Loads environment variables from .env file before Spring Boot context initialization.
 * This allows .env values to be available for @Value and ${...} property resolution.
 */
class DotenvConfig : ApplicationContextInitializer<ConfigurableApplicationContext> {

    private val logger = LoggerFactory.getLogger(DotenvConfig::class.java)

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        try {
            val dotenv = Dotenv.configure()
                .ignoreIfMissing()  // Don't fail if .env doesn't exist (e.g., in production)
                .load()

            val dotenvProperties = dotenv.entries()
                .associate { it.key to it.value }

            if (dotenvProperties.isNotEmpty()) {
                logger.info("Loaded ${dotenvProperties.size} variables from .env file")
                val propertySource = MapPropertySource("dotenvProperties", dotenvProperties)
                applicationContext.environment.propertySources.addFirst(propertySource)
            } else {
                logger.debug("No .env file found or file is empty")
            }
        } catch (e: DotenvException) {
            logger.warn("Could not load .env file: ${e.message}")
        }
    }
}
