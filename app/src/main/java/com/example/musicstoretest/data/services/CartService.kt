package com.example.musicstoretest.data.services

import android.util.Log
import com.example.musicstoretest.data.models.CartItem
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

suspend fun addToCart(userId: String, productId: String, quantity: Int = 1): Boolean {
    return try {
        // Формируем данные для вставки
        val data = mapOf(
            "user_id" to userId,
            "product_id" to productId,
            "quantity" to quantity
        )

        supabase.from("carts").insert(data)
        Log.d("CartService", "Product added to cart successfully: $data")
        true
    } catch (e: Exception) {
        Log.e("CartService", "Error adding product to cart", e)
        false
    }
}


suspend fun removeFromCart(userId: String, productId: String): Boolean {
    return try {
        supabase.from("carts").delete {
            filter {
                eq("user_id", userId)
                eq("product_id", productId)
            }
        }
        true
    } catch (e: Exception) {
        Log.e("CartService", "Error removing from cart", e)
        false
    }
}

suspend fun updateCartItem(userId: String, productId: String, quantity: Int): Boolean {
    return try {
        supabase.from("carts").update(
            mapOf("quantity" to quantity)
        ) { filter {
            eq("user_id", userId)
            eq("product_id", productId)
            }
        }
        true
    } catch (e: Exception) {
        Log.e("CartService", "Error updating cart item", e)
        false
    }
}

suspend fun fetchCart(userId: String): List<CartItem> {
    return try {
        val columns = Columns.raw("*, products(*)") // Указываем связанные данные
        supabase.from("carts").select(columns) {
            filter {
                eq("user_id", userId) // Фильтруем по user_id
            }
        }.decodeList<CartItem>() // Декодируем в список объектов CartItem
    } catch (e: Exception) {
        Log.e("CartService", "Error fetching cart", e)
        emptyList() // Возвращаем пустой список в случае ошибки
    }
}
