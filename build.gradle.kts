plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    application
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server Dependencies
    implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-server-auth:2.3.4")
    implementation("io.ktor:ktor-server-call-logging:2.3.4")
    implementation("io.ktor:ktor-server-cors:2.3.4")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
    implementation("io.ktor:ktor-server-config-yaml:2.3.4")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.5")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-admin:9.2.0")

    // Ktor HTTP Client (Replaces khttp)
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-client-json:2.3.4")

    // Environment Variables Support
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // JSON Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("com.squareup.okhttp3:okhttp:4.9.3") // OkHttp for HTTP requests
    implementation("org.json:json:20210307") // JSON handling
    // Testing
    testImplementation("io.ktor:ktor-server-test-host:2.3.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.20")
}
