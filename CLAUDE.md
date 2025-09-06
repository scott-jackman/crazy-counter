# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Crazy Counter is a Spring Boot Discord bot written in Kotlin that facilitates counting games in Discord channels. Users take turns typing sequential numbers, with the counter resetting if someone types the wrong number or the same user counts twice.

## Technology Stack

- **Language**: Kotlin 2.0.20 (latest stable)
- **Framework**: Spring Boot 3.3.4 (latest stable)
- **Build Tool**: Gradle with Kotlin DSL
- **Discord Library**: JDA 5.0.0-beta.24 (latest beta)
- **Database**: H2 in-memory database with JPA
- **Testing**: TestNG 7.9.0 with Mockito Kotlin
- **Java Version**: 17

## Architecture

### Core Components
- **Entity Layer**: `CounterState` (JPA entity) and `CounterVariation` (enum) for game state persistence
- **Service Layer**: `CounterService` handles game logic and validation
- **Discord Integration**: `DiscordMessageListener` processes Discord messages and `DiscordBotConfiguration` sets up JDA
- **Database**: H2 in-memory database for counter state persistence

### Key Classes
- `CounterService` (`src/main/kotlin/com/example/crazycounter/service/`) - Main game logic
- `DiscordMessageListener` (`src/main/kotlin/com/example/crazycounter/discord/`) - Discord message handling
- `CounterState` (`src/main/kotlin/com/example/crazycounter/entity/`) - JPA entity for counter persistence

## Version Management

The project uses **Gradle Version Catalogs** (`gradle/libs.versions.toml`) for centralized dependency management:

### Benefits
- **Single Source of Truth**: All versions defined in one file
- **Type Safety**: IDE autocompletion and validation
- **Dependabot Friendly**: Easier for automated updates
- **Bundle Support**: Logical grouping of related dependencies

### Version Catalog Structure
- **Versions**: All version numbers centralized
- **Libraries**: Individual dependency declarations
- **Bundles**: Grouped dependencies (e.g., `spring-boot`, `kotlin`, `testing`)
- **Plugins**: Plugin version management

### Key Version References
All versions are managed in `gradle/libs.versions.toml`:
- `libs.versions.kotlin` → Kotlin version
- `libs.versions.spring-boot` → Spring Boot version  
- `libs.bundles.spring.boot` → Spring Boot starter bundle
- `libs.bundles.testing` → TestNG + Mockito bundle

## Commands

### Build and Run
```bash
./gradlew bootRun
```

### Testing (TestNG)
```bash
./gradlew test
```

### Run specific test suite
```bash
./gradlew test --tests "com.example.crazycounter.service.*"
```

### Build JAR
```bash
./gradlew build
```

### Clean Build
```bash
./gradlew clean build
```

## Configuration

Bot requires `DISCORD_BOT_TOKEN` environment variable or update `application.yml`. Database configuration uses H2 in-memory by default.

## Testing Strategy

### TestNG Framework
The project uses **TestNG** instead of JUnit for testing with the following benefits:
- **Flexible Test Configuration**: XML-based test suite configuration in `testng.xml`
- **Data Providers**: Parameterized tests with easy data injection
- **Test Dependencies**: Define test execution order and dependencies
- **Parallel Execution**: Built-in support for parallel test execution
- **Rich Annotations**: `@BeforeMethod`, `@AfterMethod`, `@Test` with flexible grouping

### Test Structure
- **Unit Tests**: `CounterServiceTest`, `CounterVariationTest`
- **Integration Tests**: `CrazyCounterApplicationTest` (Spring context loading)
- **Mocking**: Mockito Kotlin for clean, type-safe mocks
- **Test Configuration**: `testng.xml` organizes tests into logical suites

### Key Test Features
- Spring TestNG integration with `AbstractTestNGSpringContextTests`
- Comprehensive service layer testing with mocked dependencies
- Entity validation and business logic verification

## Kotlin Features Used

- **Data Classes**: `CounterState` and `CounterValidationResult` use data classes for automatic equals/hashCode/toString
- **Companion Objects**: Static factory methods in `CounterValidationResult`
- **Constructor Delegation**: Primary and secondary constructors in `CounterState`
- **When Expressions**: Pattern matching in `CounterService.processCount()`
- **Extension Functions**: Leverages Kotlin's concise syntax throughout
- **Latest Kotlin 2.0.20**: Benefits from latest language improvements and performance optimizations

## GitHub Integration

### Dependabot Configuration

The project includes Dependabot configuration (`.github/dependabot.yml`) for automated dependency updates with **Version Catalog support**:

- **Gradle Dependencies**: Weekly updates on Mondays at 4:00 AM
- **GitHub Actions**: Weekly updates on Mondays at 4:00 AM  
- **Version Catalog Aware**: Dependabot updates `gradle/libs.versions.toml` directly
- **Enhanced Grouping**: Dependencies grouped by domain with update-type filtering
- **Auto-labeling**: PRs labeled with `dependencies`, `gradle`, and `version-catalog` tags
- **Review Assignment**: PRs assigned to repository maintainers for review

### Dependency Update Groups

All managed through the version catalog with automatic grouping:

- **Spring Boot**: `org.springframework.boot*`, `io.spring.dependency-management`
- **Kotlin**: `org.jetbrains.kotlin*`, `kotlin*`
- **Discord**: `net.dv8tion*` (JDA library)
- **Testing**: `org.testng*`, `org.mockito*` (TestNG and Mockito)
- **Database**: `com.h2database*` (H2 database)
- **Update Types**: Minor and patch updates grouped; major updates individual

## Bot Features

- **Commands**: `!counter status`, `!counter reset`
- **Game Logic**: Validates sequential counting, prevents same-user-twice, resets on errors
- **Visual Feedback**: 
  - ✅ Green check reaction on successful counts
  - ❌ Red X reaction + error message on failed counts
- **Startup Notifications**: Configurable startup messages sent to Discord channels on bot initialization
- **Multi-channel**: Each Discord channel maintains independent counter state
- **Persistence**: Counter states persist in database across bot restarts
- **Error Handling**: Graceful reaction failure handling with logging

## Configuration Options

### Startup Notifications
- **Property**: `discord.startup-notification.enabled` (default: true)
- **Message**: `discord.startup-notification.message` (customizable startup message)
- **Behavior**: Sends to first writable text channel per Discord server
- **Service**: `StartupNotificationService` handles channel discovery and messaging