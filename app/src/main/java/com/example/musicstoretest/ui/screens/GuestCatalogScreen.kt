package com.example.musicstoretest.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.musicstoretest.data.models.Product
import com.example.musicstoretest.data.services.fetchProducts
import com.example.musicstoretest.ui.components.ProductImage

@Composable
fun GuestCatalogScreen(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Отступы между товарами
            ) {
                items(products, key = { it.id }) { product ->
                    GuestProductCard(product = product, onProductClick = onProductClick)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("К главному экрану")
        }
    }
}

@Composable
fun GuestProductCard(product: Product, onProductClick: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ProductImage(
                imageUrl = product.image_url ?: "",
                contentDescription = "Изображение ${product.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f) // Соотношение сторон изображения
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium, // Увеличенный шрифт
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Цена: ${product.price} руб.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
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
        }
    }
}



