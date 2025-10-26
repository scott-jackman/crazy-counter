# Security Guide: Discord Token Management

This document explains how to securely manage the Discord bot token across different environments.

## Overview

The Discord bot token is a **sensitive credential** that must never be committed to version control. This project uses a multi-layered approach for token management:

- **Local Development**: Environment variables via `.env` file
- **Testing**: Mock token with Discord disabled (test profile)
- **GitHub Actions**: Encrypted GitHub Secrets
- **Production**: Environment variables from deployment platform

## 🔒 Security Principles

1. **Never commit secrets to git** - The `.gitignore` file excludes `.env` files
2. **Use environment-specific configurations** - Spring profiles separate concerns
3. **Principle of least privilege** - Tests don't need real Discord access
4. **Encrypted storage in CI/CD** - GitHub Secrets are encrypted at rest

---

## Local Development Setup

### Step 1: Create Your Discord Bot

1. Visit [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a new application or select an existing one
3. Navigate to the **Bot** section
4. Click **"Reset Token"** to view/copy your bot token
5. **Important**: Save this token securely - you can only view it once!

### Step 2: Configure Local Environment

Create a `.env` file in the project root (this file is git-ignored):

```bash
cp .env.example .env
```

Edit `.env` and add your token:

```bash
DISCORD_BOT_TOKEN=your_actual_discord_bot_token_here
```

### Step 3: Run the Application

Spring Boot automatically loads `.env` files:

```bash
./gradlew bootRun
```

The bot will connect to Discord using your token.

---

## Testing

Tests use the `test` profile which **disables Discord** entirely - no real token needed!

### Run All Tests

```bash
./gradlew test
```

The test configuration (`src/test/resources/application-test.yml`) sets:

```yaml
discord:
  enabled: false  # Discord bot won't initialize
```

### Test Profile Features

- ✅ **No real Discord connection** - Tests run offline
- ✅ **Faster execution** - No network delays
- ✅ **No credentials required** - Safe for CI/CD
- ✅ **Isolated testing** - Tests don't affect production bot

---

## GitHub Actions (CI/CD)

### Setup GitHub Secrets

1. Navigate to your repository on GitHub
2. Go to **Settings** → **Secrets and variables** → **Actions**
3. Click **"New repository secret"**
4. Add secret:
   - **Name**: `DISCORD_BOT_TOKEN`
   - **Value**: Your Discord bot token
   - Click **"Add secret"**

### CI Workflow Configuration

The `.github/workflows/ci.yml` workflow:

```yaml
- name: Run TestNG tests
  run: ./gradlew test
  env:
    SPRING_PROFILES_ACTIVE: test  # Uses test profile (Discord disabled)
```

**Note**: The CI workflow uses the `test` profile, so it doesn't need the Discord token secret. However, if you add integration tests that need a real Discord connection, you can reference the secret:

```yaml
env:
  DISCORD_BOT_TOKEN: ${{ secrets.DISCORD_BOT_TOKEN }}
  SPRING_PROFILES_ACTIVE: integration
```

---

## Production Deployment

### Environment Variables

Set the `DISCORD_BOT_TOKEN` environment variable on your deployment platform:

#### Docker / Docker Compose

```yaml
services:
  crazy-counter:
    image: crazy-counter:latest
    environment:
      - DISCORD_BOT_TOKEN=${DISCORD_BOT_TOKEN}
```

Then use:

```bash
DISCORD_BOT_TOKEN=your_token docker-compose up
```

#### Heroku

```bash
heroku config:set DISCORD_BOT_TOKEN=your_token_here
```

#### AWS / Cloud Platforms

Use your platform's secrets management:
- **AWS**: AWS Secrets Manager or Parameter Store
- **Azure**: Azure Key Vault
- **GCP**: Secret Manager

---

## Configuration Reference

### Main Configuration (`application.yml`)

```yaml
discord:
  token: ${DISCORD_BOT_TOKEN:}  # Required for normal operation
  enabled: true  # Can be set to false to disable Discord
```

### Test Configuration (`application-test.yml`)

```yaml
discord:
  enabled: false  # Disables Discord bot completely
  token: test-token-not-used  # Placeholder (never used)
```

### Docker Profile (`application.yml`)

```yaml
spring:
  config:
    activate:
      on-profile: docker
  # MySQL configuration for Docker deployment
```

---

## Troubleshooting

### "Could not resolve placeholder 'discord.token'"

**Cause**: The `DISCORD_BOT_TOKEN` environment variable is not set.

**Solution**:
1. Verify `.env` file exists with token
2. Restart application to load new environment
3. Check token is correctly formatted (no quotes in `.env`)

### Tests Failing with "InvalidTokenException"

**Cause**: Tests are not using the `test` profile.

**Solution**:
```bash
# Ensure test profile is active
./gradlew test -Dspring.profiles.active=test
```

Or check that `@ActiveProfiles("test")` is on your test class.

### GitHub Actions Build Fails with Token Error

**Cause**: Workflow is not using test profile or trying to connect to Discord.

**Solution**:
- Ensure workflow sets `SPRING_PROFILES_ACTIVE: test`
- Verify integration tests are separate from unit tests
- Check `discord.enabled: false` in test configuration

---

## Security Best Practices

### ✅ DO

- Use environment variables for secrets
- Keep `.env` files in `.gitignore`
- Rotate tokens if accidentally exposed
- Use test profiles for automated testing
- Encrypt secrets in CI/CD platforms

### ❌ DON'T

- Commit tokens to git (even in private repos)
- Share tokens in plain text (chat, email, etc.)
- Use production tokens in development
- Log tokens in application logs
- Hard-code tokens in source files

---

## Token Rotation

If your token is compromised:

1. **Immediately** go to Discord Developer Portal
2. Navigate to your application → **Bot** section
3. Click **"Reset Token"** to generate a new one
4. Update token in all environments:
   - Local `.env` file
   - GitHub Secrets
   - Production environment variables
5. Restart all running bot instances

---

## Additional Resources

- [Discord Bot Best Practices](https://discord.com/developers/docs/topics/oauth2#bot-vs-user-accounts)
- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [GitHub Actions Encrypted Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [12-Factor App Config](https://12factor.net/config)
