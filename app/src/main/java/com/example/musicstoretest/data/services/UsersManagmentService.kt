package com.example.musicstoretest.data.services

import android.util.Log
import com.example.musicstoretest.data.models.User
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

suspend fun fetchUsers(): List<User> {
    return try {
        supabase.from("users").select().decodeList()
    } catch (e: Exception) {
        Log.e("UserService", "Error fetching users", e)
        emptyList()
    }
}
suspend fun addUser(user: User): Boolean {
    return try {
        supabase.from("users").insert(user)
        Log.d("UserService", "User added successfully: $user")
        true
    } catch (e: Exception) {
        Log.e("UserService", "Error adding user", e)
        false
    }
}
@Serializable
data class UpdateUserRequest(
    val name: String,
    val email: String,
    val role: String,
    val password: String
)

suspend fun updateUser(userId: String, updatedUser: UpdateUserRequest): Boolean {
    return try {
        supabase.from("users").update(updatedUser) {
            filter { eq("id", userId) }
        }
        Log.d("UserService", "User updated successfully: $userId")
        true
    } catch (e: Exception) {
        Log.e("UserService", "Error updating user $userId", e)
        false
    }
}


suspend fun deleteUser(userId: String): Boolean {
    return try {
        supabase.from("users").delete {
            filter { eq("id", userId) }
        }
        Log.d("UserService", "User deleted successfully: $userId")
        true
    } catch (e: Exception) {
        Log.e("UserService", "Error deleting user $userId", e)
        false
    }
}
fun User.toUpdateRequest(): UpdateUserRequest {
    return UpdateUserRequest(
        name = this.name,
        email = this.email,
        role = this.role,
        password = this.password
    )
}

