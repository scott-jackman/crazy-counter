package com.example.crazycounter.discord

import com.example.crazycounter.service.CounterService
import com.example.crazycounter.service.CounterValidationResult
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import org.mockito.kotlin.*
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class DiscordMessageListenerTest : AbstractTestNGSpringContextTests() {

    private lateinit var counterService: CounterService
    private lateinit var discordMessageListener: DiscordMessageListener
    private lateinit var event: MessageReceivedEvent
    private lateinit var message: Message
    private lateinit var channel: MessageChannelUnion
    private lateinit var author: User

    @BeforeMethod
    fun setUp() {
        counterService = mock()
        discordMessageListener = DiscordMessageListener(counterService)
        
        event = mock()
        message = mock()
        channel = mock()
        author = mock()
        
        whenever(event.message).thenReturn(message)
        whenever(event.channel).thenReturn(channel)
        whenever(event.author).thenReturn(author)
        whenever(author.isBot).thenReturn(false)
        whenever(channel.id).thenReturn("test-channel-id")
        whenever(channel.name).thenReturn("counting")
        whenever(author.id).thenReturn("test-user-id")
    }

    @Test
    fun `should add green check reaction on successful count`() {
        // Given
        whenever(message.contentRaw).thenReturn("1")
        whenever(counterService.processCount(any(), any(), any())).thenReturn(
            CounterValidationResult.success(1)
        )
        
        val reactionAction = mock<RestAction<Void>>()
        whenever(message.addReaction(Emoji.fromUnicode("✅"))).thenReturn(reactionAction)
        
        // When
        discordMessageListener.onMessageReceived(event)
        
        // Then
        verify(message).addReaction(Emoji.fromUnicode("✅"))
        verify(reactionAction).queue(any(), any())
        verify(channel, never()).sendMessage(any<String>())
    }

    @Test
    fun `should add red X reaction and send message on unsuccessful count`() {
        // Given
        whenever(message.contentRaw).thenReturn("5")
        whenever(counterService.processCount(any(), any(), any())).thenReturn(
            CounterValidationResult.wrongNumber(1, 5)
        )
        
        val reactionAction = mock<RestAction<Void>>()
        val sendMessageAction = mock<MessageCreateAction>()
        whenever(message.addReaction(Emoji.fromUnicode("❌"))).thenReturn(reactionAction)
        whenever(channel.sendMessage(any<String>())).thenReturn(sendMessageAction)
        
        // When
        discordMessageListener.onMessageReceived(event)
        
        // Then
        verify(message).addReaction(Emoji.fromUnicode("❌"))
        verify(reactionAction).queue(any(), any())
        verify(channel).sendMessage(argThat<String> { contains("Wrong number!") })
        verify(sendMessageAction).queue()
    }

    @Test
    fun `should add red X reaction on same user counting twice`() {
        // Given
        whenever(message.contentRaw).thenReturn("2")
        whenever(counterService.processCount(any(), any(), any())).thenReturn(
            CounterValidationResult.sameUserTwice()
        )
        
        val reactionAction = mock<RestAction<Void>>()
        val sendMessageAction = mock<MessageCreateAction>()
        whenever(message.addReaction(Emoji.fromUnicode("❌"))).thenReturn(reactionAction)
        whenever(channel.sendMessage(any<String>())).thenReturn(sendMessageAction)
        
        // When
        discordMessageListener.onMessageReceived(event)
        
        // Then
        verify(message).addReaction(Emoji.fromUnicode("❌"))
        verify(reactionAction).queue(any(), any())
        verify(channel).sendMessage(argThat<String> { contains("Same user cannot count twice") })
        verify(sendMessageAction).queue()
    }

    @Test
    fun `should ignore invalid number format messages`() {
        // Given
        whenever(message.contentRaw).thenReturn("abc")
        
        // When
        discordMessageListener.onMessageReceived(event)
        
        // Then
        verify(counterService, never()).processCount(any(), any(), any())
        verify(message, never()).addReaction(any<Emoji>())
        verify(channel, never()).sendMessage(any<String>())
    }

    @Test
    fun `should ignore bot messages`() {
        // Given
        whenever(author.isBot).thenReturn(true)
        whenever(message.contentRaw).thenReturn("1")
        
        // When
        discordMessageListener.onMessageReceived(event)
        
        // Then
        verify(counterService, never()).processCount(any(), any(), any())
        verify(message, never()).addReaction(any<Emoji>())
    }

    @Test
    fun `should ignore non-numeric messages`() {
        // Given
        whenever(message.contentRaw).thenReturn("hello world")
        
        // When
        discordMessageListener.onMessageReceived(event)
        
        // Then
        verify(counterService, never()).processCount(any(), any(), any())
        verify(message, never()).addReaction(any<Emoji>())
        verify(channel, never()).sendMessage(any<String>())
    }

    @Test
    fun `should ignore messages in non-counting channels`() {
        // Given
        whenever(channel.name).thenReturn("general")
        whenever(message.contentRaw).thenReturn("1")
        
        // When
        discordMessageListener.onMessageReceived(event)
        
        // Then
        verify(counterService, never()).processCount(any(), any(), any())
        verify(message, never()).addReaction(any<Emoji>())
        verify(channel, never()).sendMessage(any<String>())
    }
}