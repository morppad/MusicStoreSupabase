package com.example.musicstoretest.data.services

import android.util.Log
import com.example.musicstoretest.data.models.CartItem
import com.example.musicstoretest.data.models.Order
import com.example.musicstoretest.data.models.OrderItem
import io.github.jan.supabase.postgrest.from
import java.util.UUID

suspend fun fetchOrdersSafely(userId: String): List<Order> {
    return try {
        supabase.from("orders").select() {
            filter { eq("user_id", userId) }
        }.decodeList<Order>()
    } catch (e: Exception) {
        Log.e("OrderService", "Error fetching orders for user $userId", e)
        emptyList()
    }
}
suspend fun fetchOrders(userId: String): List<Order> {
    return try {
        supabase.from("orders").select() {
            filter { eq("user_id", userId) }
        }.decodeList<Order>()
    } catch (e: Exception) {
        Log.e("OrderService", "Error fetching orders for user $userId", e)
        emptyList()
    }
}
suspend fun placeOrder(userId: String, address: String): Boolean {
    return try {
        val cartItems = fetchCart(userId) // Получение товаров из корзины пользователя
        if (cartItems.isEmpty()) {
            Log.e("OrderService", "Cart is empty. Cannot place order.")
            return false
        }

        // Генерация нового заказа
        val orderId = UUID.randomUUID().toString()
        val totalPrice = cartItems.sumOf { it.quantity * it.products.price }

        val order = Order(
            id = orderId,
            user_id = userId,
            total_price = totalPrice,
            address = address // Сохраняем адрес в заказе
        )

        supabase.from("orders").insert(order) // Вставляем заказ в таблицу orders

        // Создаём записи для order_items
        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                id = UUID.randomUUID().toString(),
                order_id = orderId,
                product_id = cartItem.product_id,
                quantity = cartItem.quantity,
                price = cartItem.products.price
            )
        }

        supabase.from("order_items").insert(orderItems) // Вставляем товары заказа в таблицу order_items

        // Удаляем товары из корзины
        supabase.from("carts").delete {
            filter { eq("user_id", userId) }
        }

        Log.d("OrderService", "Order placed successfully: $orderId")
        true
    } catch (e: Exception) {
        Log.e("OrderService", "Error placing order for user $userId", e)
        false
    }
}

