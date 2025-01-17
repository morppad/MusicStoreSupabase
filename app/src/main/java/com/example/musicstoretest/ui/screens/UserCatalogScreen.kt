package com.example.musicstoretest.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicstoretest.data.models.Product
import com.example.musicstoretest.ui.components.ProductImage

@Composable
fun UserCatalogScreen(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onLogout: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onViewCart: () -> Unit,
    onViewOrderHistory: () -> Unit // Новый параметр для просмотра истории заказов
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Каталог",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onViewCart, modifier = Modifier.weight(1f)) {
                Text("Просмотреть корзину")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onViewOrderHistory, modifier = Modifier.weight(1f)) {
                Text("История заказов")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    onAddToCart = onAddToCart,
                    onProductClick = onProductClick
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showLogoutConfirmation = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выйти")
        }
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("Подтверждение выхода") },
            text = { Text("Вы уверены что хотите выйти?") },
            confirmButton = {
                Button(onClick = {
                    showLogoutConfirmation = false
                    onLogout()
                }) {
                    Text("Да")
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutConfirmation = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: (Product) -> Unit,
    onProductClick: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) }
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp) // Указываем elevation через CardDefaults
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ProductImage(
                imageUrl = product.image_url ?: "",
                contentDescription = "Image of ${product.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Цена: ${product.price} руб.",
                style = MaterialTheme.typography.bodyLarge
            )

            if (product.stock > 0) {
                Text(
                    text = "В наличии: ${product.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Нет в наличии",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить в корзину")
            }
        }
    }
}

