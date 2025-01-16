package com.example.musicstoretest.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val password: String,
    val name: String,
    val role: String
)
