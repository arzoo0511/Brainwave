package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import io.github.cdimascio.dotenv.dotenv
import kotlinx.serialization.Serializable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

fun Application.configureRouting() {
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
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
                val env = dotenv { directory = "."; ignoreIfMissing = true }
                val apiKey = env["FIREBASE_API_KEY"] ?: throw Exception("Firebase API Key not found in .env")

                val client = OkHttpClient()

                val jsonPayload = """
                    {
                        "email": "${request.email}",
                        "password": "${request.password}",
                        "returnSecureToken": true
                    }
                """.trimIndent()

                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = jsonPayload.toRequestBody(mediaType)

                val firebaseRequest = Request.Builder()
                    .url("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = client.newCall(firebaseRequest).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                    return@post
                }

                val jsonResponse = JSONObject(responseBody)
                val idToken = jsonResponse.getString("idToken")

                call.respond(HttpStatusCode.OK, mapOf("message" to "Login successful", "token" to idToken))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.localizedMessage))
            }
        }

        post("/logout") {
            try {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Logout successful"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Logout failed"))
            }
        }

        post("/google-login") {
            try {
                val request = call.receive<GoogleAuthRequest>()
                val auth = FirebaseAuth.getInstance()

                // Verify the Google ID token
                val decodedToken = auth.verifyIdToken(request.idToken)
                val uid = decodedToken.uid

                // Generate Firebase custom token
                val firebaseToken = auth.createCustomToken(uid)

                call.respond(HttpStatusCode.OK, mapOf("message" to "Google login successful", "token" to firebaseToken))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid Google token"))
            }
        }


    }
}

@Serializable
data class UserRequest(val email: String, val password: String)
@Serializable
data class GoogleAuthRequest(val idToken: String)