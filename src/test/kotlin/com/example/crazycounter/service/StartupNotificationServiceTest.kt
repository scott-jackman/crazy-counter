package com.example.crazycounter.service

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import org.mockito.kotlin.*
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class StartupNotificationServiceTest : AbstractTestNGSpringContextTests() {

    private lateinit var startupNotificationService: StartupNotificationService
    private lateinit var jda: JDA
    private lateinit var guild: Guild
    private lateinit var textChannel: TextChannel

    @BeforeMethod
    fun setUp() {
        startupNotificationService = StartupNotificationService(true, "Test startup message")
        jda = mock()
        guild = mock()
        textChannel = mock()
        
        whenever(guild.name).thenReturn("Test Guild")
        whenever(textChannel.name).thenReturn("counting")
        whenever(textChannel.canTalk()).thenReturn(true)
    }

    @Test
    fun `should send startup notification to counting channels`() {
        // Given
        whenever(jda.guilds).thenReturn(listOf(guild))
        whenever(guild.textChannels).thenReturn(listOf(textChannel))
        
        val messageAction = mock<MessageCreateAction>()
        whenever(textChannel.sendMessage(any<String>())).thenReturn(messageAction)
        
        // When
        startupNotificationService.notifyChannelsOfStartup(jda)
        
        // Then
        verify(textChannel).sendMessage("Test startup message")
        verify(messageAction).queue(any(), any())
    }

    @Test
    fun `should skip channels where bot cannot talk`() {
        // Given
        whenever(jda.guilds).thenReturn(listOf(guild))
        whenever(guild.textChannels).thenReturn(listOf(textChannel))
        whenever(textChannel.canTalk()).thenReturn(false)
        
        // When
        startupNotificationService.notifyChannelsOfStartup(jda)
        
        // Then
        verify(textChannel, never()).sendMessage(any<String>())
    }

    @Test
    fun `should handle multiple guilds with multiple channels`() {
        // Given
        val guild2 = mock<Guild>()
        val textChannel2 = mock<TextChannel>()
        
        whenever(guild2.name).thenReturn("Test Guild 2")
        whenever(textChannel2.name).thenReturn("counting")
        whenever(textChannel2.canTalk()).thenReturn(true)
        
        whenever(jda.guilds).thenReturn(listOf(guild, guild2))
        whenever(guild.textChannels).thenReturn(listOf(textChannel))
        whenever(guild2.textChannels).thenReturn(listOf(textChannel2))
        
        val messageAction1 = mock<MessageCreateAction>()
        val messageAction2 = mock<MessageCreateAction>()
        whenever(textChannel.sendMessage(any<String>())).thenReturn(messageAction1)
        whenever(textChannel2.sendMessage(any<String>())).thenReturn(messageAction2)
        
        // When
        startupNotificationService.notifyChannelsOfStartup(jda)
        
        // Then
        verify(textChannel).sendMessage(any<String>())
        verify(textChannel2).sendMessage(any<String>())
        verify(messageAction1).queue(any(), any())
        verify(messageAction2).queue(any(), any())
    }

    @Test
    fun `should ignore non-counting channels`() {
        // Given
        val textChannel2 = mock<TextChannel>()
        whenever(textChannel2.name).thenReturn("general")
        whenever(textChannel2.canTalk()).thenReturn(true)
        
        whenever(jda.guilds).thenReturn(listOf(guild))
        whenever(guild.textChannels).thenReturn(listOf(textChannel, textChannel2))
        
        val messageAction = mock<MessageCreateAction>()
        whenever(textChannel.sendMessage(any<String>())).thenReturn(messageAction)
        
        // When
        startupNotificationService.notifyChannelsOfStartup(jda)
        
        // Then
        verify(textChannel).sendMessage(any<String>()) // Counting channel should get message
        verify(textChannel2, never()).sendMessage(any<String>()) // General channel should not
        verify(messageAction).queue(any(), any())
    }

    @Test
    fun `should skip sending notifications when disabled`() {
        // Given
        val disabledService = StartupNotificationService(false, "Test message")
        whenever(jda.guilds).thenReturn(listOf(guild))
        whenever(guild.textChannels).thenReturn(listOf(textChannel))
        
        // When
        disabledService.notifyChannelsOfStartup(jda)
        
        // Then
        verify(textChannel, never()).sendMessage(any<String>())
    }

    @Test
    fun `should handle empty guilds list gracefully`() {
        // Given
        whenever(jda.guilds).thenReturn(emptyList())
        
        // When
        startupNotificationService.notifyChannelsOfStartup(jda)
        
        // Then
        // Should not throw exception and complete successfully
        verify(jda).guilds
    }

    @Test
    fun `should send to counting channels with different cases`() {
        // Given
        val textChannel2 = mock<TextChannel>()
        whenever(textChannel2.name).thenReturn("COUNTING")
        whenever(textChannel2.canTalk()).thenReturn(true)
        
        whenever(jda.guilds).thenReturn(listOf(guild))
        whenever(guild.textChannels).thenReturn(listOf(textChannel, textChannel2))
        
        val messageAction1 = mock<MessageCreateAction>()
        val messageAction2 = mock<MessageCreateAction>()
        whenever(textChannel.sendMessage(any<String>())).thenReturn(messageAction1)
        whenever(textChannel2.sendMessage(any<String>())).thenReturn(messageAction2)
        
        // When
        startupNotificationService.notifyChannelsOfStartup(jda)
        
        // Then
        verify(textChannel).sendMessage(any<String>()) // "counting" channel
        verify(textChannel2).sendMessage(any<String>()) // "COUNTING" channel
        verify(messageAction1).queue(any(), any())
        verify(messageAction2).queue(any(), any())
    }
}