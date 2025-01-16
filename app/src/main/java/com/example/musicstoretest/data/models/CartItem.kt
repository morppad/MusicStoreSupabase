package com.example.musicstoretest.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: String,
    val user_id: String,
    val product_id: String,
    val quantity: Int,
    val added_at: String,
    val products: Product // Вложенный объект Product
)
