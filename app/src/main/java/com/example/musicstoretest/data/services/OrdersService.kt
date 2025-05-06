package com.example.musicstoretest.data.services

import android.util.Log
import com.example.musicstoretest.data.models.CartItem
import com.example.musicstoretest.data.models.Order
import com.example.musicstoretest.data.models.OrderItem
import com.example.musicstoretest.data.models.Product
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.sql.Types.NULL
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
        val columns = Columns.raw("id, total_price, status, user_id, created_at, address") // Указываем нужные поля
        supabase.from("orders").select(columns) {
            filter {
                eq("user_id", userId)
            }
        }.decodeList<Order>()
    } catch (e: Exception) {
        Log.e("OrderService", "Error fetching orders for user $userId", e)
        emptyList()
    }
}

suspend fun placeOrder(userId: String, address: String): Boolean {
    return try {
        val cartItems = fetchCart(userId) // Получаем товары из корзины пользователя
        if (cartItems.isEmpty()) {
            Log.e("OrderService", "Корзина пуста. Оформление заказа невозможно.")
            return false
        }

        // Проверяем наличие каждого товара на складе
        for (cartItem in cartItems) {
            val product = supabase.from("products")
                .select()
                { filter { eq("id", cartItem.product_id) } }
                .decodeSingleOrNull<Product>()

            if (product == null || product.stock < cartItem.quantity) {
                Log.e("OrderService", "Недостаточно товара на складе: ${cartItem.product_id}")
                return false // Если товара недостаточно, отменяем заказ
            }
        }

        // Создание нового заказа
        val orderId = UUID.randomUUID().toString()
        val totalPrice = cartItems.sumOf { it.quantity * it.products.price }

        val order = Order(
            id = orderId,
            user_id = userId,
            total_price = totalPrice,
            address = address
        )

        supabase.from("orders").insert(order) // Вставляем заказ в таблицу orders

        // Создание записей order_items и обновление количества товара
        val orderItems = mutableListOf<OrderItem>()

        for (cartItem in cartItems) {
            orderItems.add(
                OrderItem(
                    id = UUID.randomUUID().toString(),
                    order_id = orderId,
                    product_id = cartItem.product_id,
                    quantity = cartItem.quantity,
                    price = cartItem.products.price
                )
            )

            // Обновляем количество товара в таблице products
            supabase.from("products").update(
                {
                    set("stock", cartItem.products.stock - cartItem.quantity)
                }
            ) {
                filter {
                    eq("id", cartItem.product_id)
                }
            }
        }

        // Вставляем товары заказа в таблицу order_items
        supabase.from("order_items").insert(orderItems)

        // Удаляем товары из корзины
        supabase.from("carts").delete {
            filter { eq("user_id", userId) }
        }

        Log.d("OrderService", "Заказ успешно оформлен: $orderId")
        true
    } catch (e: Exception) {
        Log.e("OrderService", "Ошибка при оформлении заказа для пользователя $userId", e)
        false
    }
}


