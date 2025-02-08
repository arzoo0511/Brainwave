package com.example


import com.example.routes.configureChapterRoutes
import com.example.routes.configureGradeRoutes
import com.example.routes.configureRouting
import com.example.routes.configureSubjectRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun main() {
    try {
        FirebaseInit.initialize()
        embeddedServer(Netty, port = 8082, host = "0.0.0.0") {
            module()
        }.start(wait = true)
    } catch (e: Exception) {
        println("Server failed to start: ${e.localizedMessage}")
    }
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureRouting()
    configureChapterRoutes()
    configureSubjectRoutes()
    configureGradeRoutes()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}