package com.example.musicstoretest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.Nullable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicstoretest.data.models.Product
import com.example.musicstoretest.ui.components.ProductImage
import com.google.android.material.motion.MaterialBackHandler

@Composable
fun GuestCatalogScreen(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onBack: () -> Unit
) {
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
            .padding(horizontal = 8.dp, vertical = 0.dp)

    ) {
        Text(
            text = "Каталог товаров",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (products.isEmpty()) {
            Text(
                text = "Каталог временно недоступен",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products, key = { it.id }) { product ->
                    GuestProductCard(product = product, onProductClick = onProductClick)
                }
            }
        }
    }
}

@Composable
fun GuestProductCard(product: Product, onProductClick: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onProductClick(product) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Прозрачный фон

        )

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)

        ) {
            ProductImage(
                imageUrl = product.image_url ?: "",
                contentDescription = "Изображение ${product.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                textAlign = TextAlign.Center, // Выравнивание текста по центру
                text = product.name,
                style = MaterialTheme.typography.headlineSmall, // Крупный шрифт для названия
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "${product.price} ₽",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = if (product.stock > 0) "Есть в наличии" else "Нет в наличии",
                style = MaterialTheme.typography.bodySmall,
                color = if (product.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )

        }
    }
}