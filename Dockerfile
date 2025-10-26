# Minimal Alpine Linux with Java 25 JRE
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the pre-built executable Spring Boot JAR
COPY build/libs/crazy-counter-0.0.1-SNAPSHOT.jar app.jar

# Change ownership
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
