package com.example.musicstoretest.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val name: String,
    val email: String,
    val role: String,
    val password: String
)
