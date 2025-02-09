package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.Serializable
import io.github.cdimascio.dotenv.dotenv
import okhttp3.*
import org.json.JSONObject

val dotenv = dotenv()
val GOOGLE_CLIENT_ID = dotenv["GOOGLE_CLIENT_ID"]

fun Application.configureAuthRoutes() {
    routing {
        post("/oauth/google") {
            val request = call.receive<OAuthRequest>()

            val httpClient = OkHttpClient()
            val googleRequest = Request.Builder()
                .url("https://oauth2.googleapis.com/tokeninfo?id_token=${request.idToken}")
                .build()

            val response = httpClient.newCall(googleRequest).execute()
            if (!response.isSuccessful) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid Google Token"))
                return@post
            }

            val responseBody = response.body?.string()
            val jsonObject = JSONObject(responseBody!!)
            val email = jsonObject.getString("email")

            val auth = FirebaseAuth.getInstance()
            val user = try {
                auth.getUserByEmail(email)
            } catch (e: Exception) {
                auth.createUser(com.google.firebase.auth.UserRecord.CreateRequest().setEmail(email))
            }

            call.respond(HttpStatusCode.OK, mapOf("message" to "Login successful", "userId" to user.uid))
        }
    }
}

@Serializable
data class OAuthRequest(val idToken: String)
