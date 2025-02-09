package com.example.Utils
import com.google.firebase.auth.FirebaseAuth

object FirebaseUtil {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createUser(email: String, password: String): String {
        val user = auth.createUser(
            com.google.firebase.auth.UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
        )
        return user.uid
    }

    fun getUserByEmail(email: String): String {
        val user = auth.getUserByEmail(email)
        return user.uid
    }

    fun revokeToken(uid: String) {
        auth.revokeRefreshTokens(uid)
    }
}