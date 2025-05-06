package com.example.musicstoretest.data.services

import android.util.Log
import com.example.musicstoretest.data.models.Order
import com.example.musicstoretest.data.models.OrderItem
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns


suspend fun fetchOrdersAdmin(): List<Order> {
    return try {
        supabase.from("orders").select()
            .decodeList<Order>()
    } catch (e: Exception) {
        Log.e("OrderService", "Error fetching orders", e)
        emptyList()
    }
}
suspend fun fetchOrderItems(orderId: String): List<OrderItem> {
    return try {
        val columns = Columns.raw("*, products!product_id(*)")
        val rawData = supabase.from("order_items").select(columns)
        Log.d("OrderService", "Raw response: ${rawData.data}")

        val result = supabase.from("order_items").select(columns) {
            filter {
                eq("order_id", orderId)
            }
        }.decodeList<OrderItem>()
        Log.d("OrderService", "Raw data: $result")
        Log.d("OrderService", "Fetched order items: $result")
        result
    } catch (e: Exception) {
        Log.e("OrderService", "Error fetching order items for order $orderId", e)
        emptyList()
    }
}



    suspend fun updateOrder(orderId: String, updateRequest: String): Boolean {
        return try {
            supabase.from("orders").update(updateRequest) {
                filter { eq("id", orderId) }
            }
            Log.d("OrderService", "Order updated successfully: $orderId")
            true
        } catch (e: Exception) {
            Log.e("OrderService", "Error updating order $orderId", e)
            false
        }
    }


suspend fun deleteOrder(orderId: String): Boolean {
    return try {
        supabase.from("orders").delete {
            filter { eq("id", orderId) }
        }
        Log.d("OrderService", "Order deleted successfully: $orderId")
        true
    } catch (e: Exception) {
        Log.e("OrderService", "Error deleting order $orderId", e)
        false
    }
}
