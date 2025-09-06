package com.example.crazycounter.config

import com.example.crazycounter.discord.DiscordMessageListener
import com.example.crazycounter.service.StartupNotificationService
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordBotConfiguration(
    @Value("\${discord.token}") private val token: String,
    private val messageListener: DiscordMessageListener,
    private val startupNotificationService: StartupNotificationService
) {
    
    private val logger = LoggerFactory.getLogger(DiscordBotConfiguration::class.java)
    
    @Bean
    fun jda(): JDA {
        logger.info("Initializing Discord bot...")
        
        val jda = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
            .addEventListeners(messageListener)
            .build()
            
        jda.awaitReady()
        logger.info("Discord bot is ready! Connected as: {}", jda.selfUser.name)
        
        // Send startup notifications to channels
        startupNotificationService.notifyChannelsOfStartup(jda)
        
        return jda
    }
}