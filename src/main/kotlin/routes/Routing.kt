package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
    }

    routing {
        get("/") {
            call.respondText("Welcome to GyaanGo Backend!")
        }

        post("/signup") {
            try {
                val request = call.receive<UserRequest>()
                val auth = FirebaseAuth.getInstance()

                val user = auth.createUser(
                    UserRecord.CreateRequest()
                        .setEmail(request.email)
                        .setPassword(request.password)
                )
                call.respond(HttpStatusCode.Created, mapOf("message" to "User ${user.uid} created successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.localizedMessage))
            }
        }

        post("/login") {
            try {
                val request = call.receive<UserRequest>()
                val auth = FirebaseAuth.getInstance()

                val user = auth.getUserByEmail(request.email)
                call.respond(HttpStatusCode.OK, mapOf("message" to "User ${user.uid} logged in successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            }
        }
    }
}

@Serializable
data class UserRequest(val email: String, val password: String)