package com.example.musicstoretest.data.models

// PaymentModels.kt
data class PaymentRequest(
    val amount: Amount,
    val payment_method_data: PaymentMethodData,
    val confirmation: Confirmation
)

data class Amount(val value: String, val currency: String = "RUB")
data class PaymentMethodData(val type: String = "bank_card")
data class Confirmation(val type: String = "redirect", val return_url: String = "yourapp://payment_result")