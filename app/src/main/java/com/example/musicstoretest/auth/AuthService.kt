package com.example.musicstoretest.auth

import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.musicstoretest.data.models.User
import com.example.musicstoretest.data.services.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

suspend fun loginUser(
    email: String,
    password: String,
    onSuccess: (String, String) -> Unit, // userId и role
    onError: (String) -> Unit
) {
    try {
        val users = supabase.from("users")
            .select()
            .decodeList<User>()

        val user = users.find { it.email == email }
        if (user == null) {
            onError("User not found")
            return
        }

        val hashedPassword = user.password
        val role = user.role
        val userId = user.id

        val result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword)
        if (result.verified) {
            onSuccess(userId, role) // Возвращаем userId и role
        } else {
            onError("Invalid password")
        }
    } catch (e: Exception) {
        onError(e.message ?: "Login failed")
    }
}

suspend fun registerUser(email: String, password: String, name: String) {
    try {
        val existingUsers = supabase.from("users")
            .select(Columns.raw("id")) {
                filter { eq("email", email) }
            }
            .decodeList<Map<String, String>>() // можно декодировать в любой простой тип

        if (existingUsers.isNotEmpty()) {
            throw IllegalArgumentException("Пользователь с такой почтой уже существует")
        }

        val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())

        supabase.from("users").insert(
            mapOf(
                "email" to email,
                "name" to name,
                "password" to hashedPassword,
                "role" to "customer"
            )
        )

        Log.d("AuthService", "User registered successfully")
    } catch (e: Exception) {
        Log.e("AuthService", "Registration failed", e)
        throw e
    }
}


