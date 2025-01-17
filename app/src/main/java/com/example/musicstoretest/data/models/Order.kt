package com.example.musicstoretest.data.models

import kotlinx.serialization.Serializable
import okhttp3.Address

@Serializable
data class Order(
    val id: String,
    val user_id: String,
    val total_price: Double,
    val status: String = "Processing", // Значение по умолчанию
    val address: String?
)

@Serializable
data class OrderItem(
    val id: String,
    val order_id: String,
    val product_id: String,
    val quantity: Int,
    val price: Double
)
