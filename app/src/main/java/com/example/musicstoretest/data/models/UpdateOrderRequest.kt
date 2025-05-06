package com.example.musicstoretest.data.models;

import kotlinx.serialization.Serializable;

@Serializable
data class UpdateOrderRequest(
        val address: String?,
        val status: String
)