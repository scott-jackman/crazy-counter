package com.example.crazycounter.service

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class StartupNotificationService(
    @Value("\${discord.startup-notification.enabled:true}") private val notificationEnabled: Boolean,
    @Value("\${discord.startup-notification.message:🚀 **Crazy Counter Bot is online!** Ready to start counting.}") 
    private val startupMessage: String
) {

    private val logger = LoggerFactory.getLogger(StartupNotificationService::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun sendStartupNotification() {
        // We need to get the JDA instance after it's been created
        // This will be injected when the application context is ready
    }

    fun notifyChannelsOfStartup(jda: JDA) {
        if (!notificationEnabled) {
            logger.info("Startup notifications disabled, skipping...")
            return
        }
        
        logger.info("Sending startup notifications to Discord channels...")

        try {
            // Send startup message to all text channels the bot has access to
            val guilds = jda.guilds
            var channelCount = 0

            for (guild in guilds) {
                val textChannels = guild.textChannels
                
                // Send to channels named "counting" that the bot can write to
                for (channel in textChannels) {
                    if (channel.name.lowercase() == "counting" && channel.canTalk()) {
                        channel.sendMessage(startupMessage)
                            .queue(
                                { 
                                    channelCount++
                                    logger.info("Sent startup notification to counting channel '{}' in guild '{}'", channel.name, guild.name)
                                },
                                { error -> 
                                    logger.warn("Failed to send startup notification to counting channel '{}' in guild '{}': {}", 
                                        channel.name, guild.name, error.message)
                                }
                            )
                    }
                }
            }

            if (channelCount == 0) {
                logger.warn("No 'counting' channels found to send startup notifications to")
            } else {
                logger.info("Startup notifications sent to {} counting channels", channelCount)
            }

        } catch (exception: Exception) {
            logger.error("Error sending startup notifications: {}", exception.message, exception)
        }
    }
}