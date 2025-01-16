package com.example.musicstoretest.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,             // UUID - уникальный идентификатор товара
    val name: String,           // Название товара
    val price: Double,          // Цена товара
    val description: String?,   // Описание товара (может быть null)
    val image_url: String?,     // URL изображения товара (может быть null)
    val stock: Int,             // Количество на складе
    val created_at: String?,    // Дата создания записи
    val updated_at: String?     // Дата последнего обновления записи
)