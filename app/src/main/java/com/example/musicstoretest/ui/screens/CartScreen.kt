package com.example.musicstoretest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicstoretest.data.models.CartItem
import com.example.musicstoretest.data.services.fetchCart
import com.example.musicstoretest.data.services.removeFromCart
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    userId: String, // Идентификатор пользователя
    onBack: () -> Unit // Колбэк для возвращения назад
) {
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        cartItems = fetchCart(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Cart",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems, key = { it.id }) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.products.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text("x${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        coroutineScope.launch {
                            removeFromCart(userId, item.product_id)
                            cartItems = fetchCart(userId) // Обновляем корзину
                        }
                    }) {
                        Text("Remove")
                    }
                }
            }
        }

        Button(
            onClick = onBack, // Используем переданный колбэк
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}