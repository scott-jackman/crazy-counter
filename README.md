# Crazy Counter Discord Bot

A Spring Boot Discord bot written in Kotlin that implements counting games in Discord channels.

## Technology Stack

- **Kotlin** 2.0.20 (latest stable)
- **Spring Boot** 3.3.4 (latest stable) 
- **Gradle** with Kotlin DSL
- **JDA** 5.0.0-beta.24 (Discord API)
- **H2** in-memory database
- **TestNG** 7.9.0 (testing framework)

## Setup

1. **Create a Discord Bot Application**:
   - Go to [Discord Developer Portal](https://discord.com/developers/applications)
   - Create a new application and bot
   - Copy the bot token

2. **Configure the Bot Token**:
   - Set environment variable: `DISCORD_BOT_TOKEN=your-bot-token-here`
   - Or update `src/main/resources/application.yml`

3. **Invite Bot to Server**:
   - In Discord Developer Portal, go to OAuth2 > URL Generator
   - Select "bot" scope and "Send Messages", "Read Message History" permissions
   - Use generated URL to invite bot to your server

## Running the Application

```bash
./gradlew bootRun
```

Or set the environment variable and run:
```bash
DISCORD_BOT_TOKEN=your-token-here ./gradlew bootRun
```

## Bot Commands

- `!counter status` - Shows current count and next expected number
- `!counter reset` - Resets the counter to 0

## How to Play

1. Users take turns typing sequential numbers starting from 1
2. **Successful counts** get a ✅ (green check) reaction
3. **Failed counts** get a ❌ (red X) reaction + error message
4. If wrong number is typed, counter resets to 0
5. If same user types twice in a row, counter resets to 0
6. Each Discord channel maintains its own counter state

## Visual Feedback

- ✅ **Green Check**: Successful count, continue to next number
- ❌ **Red X**: Failed count with explanatory message (wrong number, same user twice, invalid format)

## Game Rules

- **Basic Counter**: Starts at 1 and increments by 1 each turn
- Numbers must be typed as plain integers (e.g., "1", "2", "3")
- Bot processes all messages (except commands starting with "!")
- Non-numeric messages get ❌ reaction and error explanation
- Each channel has independent counter state

## Configuration

### Startup Notifications

The bot can send a startup message to Discord channels when it comes online:

```yaml
discord:
  startup-notification:
    enabled: true  # Set to false to disable startup messages
    message: "🚀 **Crazy Counter Bot is online!** Ready to start counting. Type `!counter status` for help."
```

- **Default**: Enabled with a friendly startup message
- **Behavior**: Sends message to the first available text channel in each Discord server
- **Permissions**: Only sends to channels where the bot can write messages

### Other Configuration

- Bot requires `DISCORD_BOT_TOKEN` environment variable or update `application.yml`
- Database configuration uses H2 in-memory by default