package com.example.musicstoretest.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.Address
import java.sql.Timestamp

@Serializable
data class Order(
    val id: String,
    val user_id: String,
    val total_price: Double,
    val address: String?,
    val status: String = "В обработке",
    val created_at: String? = null
)

@Serializable
data class OrderItem(
    val id: String,
    val order_id: String,
    val product_id: String,
    val quantity: Int,
    val price: Double,
    @SerialName("products") val product: Product? = null
)
