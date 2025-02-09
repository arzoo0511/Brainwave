package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val email: String,
    val password: String
)