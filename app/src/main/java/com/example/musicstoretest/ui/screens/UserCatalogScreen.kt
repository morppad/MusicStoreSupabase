package com.example.musicstoretest.ui.screens

import android.util.Log
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
import com.example.musicstoretest.data.models.Product
import com.example.musicstoretest.ui.components.ProductImage

@Composable
fun UserCatalogScreen(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onLogout: () -> Unit,
    onAddToCart: (Product) -> Unit, // Новый параметр
    onViewCart: () -> Unit // Новый параметр
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Catalog",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onViewCart, // Переход к экрану корзины
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Cart")
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(products, key = { it.id }) { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProductImage(
                        imageUrl = product.image_url ?: "",
                        contentDescription = "Image of ${product.name}",
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(product.name, style = MaterialTheme.typography.bodyLarge)
                        Text("Price: ${product.price}", style = MaterialTheme.typography.bodyMedium)
                    }
                    Button(onClick = {
                        Log.d("Cart", "Add to Cart clicked for product: ${product.name}")
                        onAddToCart(product)
                    }) {
                        Text("Add to Cart")
                    }


                }
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun ProductCard(product: Product, onProductClick: (Product) -> Unit) {
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


