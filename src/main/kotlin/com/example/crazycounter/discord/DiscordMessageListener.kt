package com.example.crazycounter.discord

import com.example.crazycounter.service.CounterService
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DiscordMessageListener(private val counterService: CounterService) : ListenerAdapter() {
    
    private val logger = LoggerFactory.getLogger(DiscordMessageListener::class.java)
    
    companion object {
        private val SUCCESS_EMOJI = Emoji.fromUnicode("✅") // Green check mark
        private val FAILURE_EMOJI = Emoji.fromUnicode("❌") // Red X
    }
    
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        
        // Only interact with channels named "counting"
        val channelName = event.channel.name.lowercase()
        if (channelName != "counting") {
            return
        }
        
        val message = event.message.contentRaw
        val channelId = event.channel.id
        val userId = event.author.id
        
        when {
            message.startsWith("!counter") -> handleCounterCommand(event, message)
            !message.startsWith("!") && isNumeric(message) -> { // Process numeric non-command messages
                val result = counterService.processCount(channelId, userId, message)
                
                if (result.success) {
                    // Add green check reaction for successful counts
                    event.message.addReaction(SUCCESS_EMOJI).queue(
                        { logger.debug("Added success reaction to message in channel {}", channelId) },
                        { error -> logger.warn("Failed to add success reaction: {}", error.message) }
                    )
                } else {
                    // Add red X reaction and send error message for unsuccessful counts
                    event.message.addReaction(FAILURE_EMOJI).queue(
                        { logger.debug("Added failure reaction to message in channel {}", channelId) },
                        { error -> logger.warn("Failed to add failure reaction: {}", error.message) }
                    )
                    event.channel.sendMessage(result.message).queue()
                }
                
                logger.debug("Counter validation for channel {}: {}", channelId, result.message)
            }
            // Non-numeric messages are silently ignored
        }
    }
    
    private fun handleCounterCommand(event: MessageReceivedEvent, message: String) {
        val parts = message.split("\\s+".toRegex())
        
        if (parts.size < 2) {
            event.channel.sendMessage("Available commands: `!counter status`, `!counter reset`").queue()
            return
        }
        
        val command = parts[1].lowercase()
        val channelId = event.channel.id
        
        when (command) {
            "status" -> {
                val state = counterService.getCounterState(channelId)
                event.channel.sendMessage(
                    "📊 Current count: ${state.currentNumber} | Next expected: ${state.getNextExpectedNumber()}"
                ).queue()
            }
            
            "reset" -> {
                counterService.resetCounter(channelId)
                event.channel.sendMessage("🔄 Counter has been reset to 0!").queue()
            }
            
            else -> {
                event.channel.sendMessage("Unknown command. Available: `!counter status`, `!counter reset`").queue()
            }
        }
    }
    
    private fun isNumeric(message: String): Boolean {
        return message.trim().toIntOrNull() != null
    }
    
}