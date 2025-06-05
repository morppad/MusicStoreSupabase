package com.example.musicstoretest.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.musicstoretest.data.models.Product
import com.example.musicstoretest.ui.components.ProductImage
import kotlinx.coroutines.launch

@Composable
fun UserCatalogScreen(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onLogout: () -> Unit,
    onAddToCart: (Product, (Boolean) -> Unit) -> Unit,
    onViewCart: () -> Unit,
    onViewOrderHistory: () -> Unit,
    onBack: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope() // Скоуп для выполнения операций
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Подтверждение выхода") },
            text = { Text("Вы уверены, что хотите выйти?") },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onBack()
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showExitDialog = false },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
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
            Button(
                onClick = onViewCart,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Корзина")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onViewOrderHistory,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small
            ) {
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
                    onAddToCart = {
                        onAddToCart(product) { success ->
                            if (!success) {
                                errorMessage = null
                                coroutineScope.launch {
                                    Toast.makeText(context, "Товара нет в наличии", Toast.LENGTH_SHORT).show()
                                }
                                //errorMessage = "Недостаточно товара на складе"
                            } else {
                                errorMessage = null
                            }
                        }
                    },
                    onProductClick = onProductClick
                )
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            )
        }
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
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(0.dp), // Убираем тень
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Прозрачный фон
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            // Product image
            ProductImage(
                imageUrl = product.image_url ?: "",
                contentDescription = "Image of ${product.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product name
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Product price
            Text(
                text = "${product.price} ₽",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Stock status
            Text(
                text = if (product.stock > 0) "Есть в наличии" else "Нет в наличии",
                style = MaterialTheme.typography.bodySmall,
                color = if (product.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Add to cart button
            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Добавить в корзину")
            }
        }
    }
}
