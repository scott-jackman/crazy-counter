import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation(libs.bundles.spring.boot)
    
    // Kotlin
    implementation(libs.bundles.kotlin)
    
    // Discord
    implementation(libs.jda)
    
    // Database
    runtimeOnly(libs.h2)
    
    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(group = "org.junit.jupiter")
        exclude(group = "org.junit.vintage")
        exclude(group = "junit")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = libs.versions.java.get()
    }
}

tasks.withType<Test> {
    useTestNG()
}