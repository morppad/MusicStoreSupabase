package com.example.musicstoretest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.musicstoretest.data.models.Product

@Composable
fun ProductDetailsScreen(
    product: Product,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Детали товара",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Изображение товара
        Image(
            painter = rememberAsyncImagePainter(product.image_url),
            contentDescription = "Изображение товара ${product.name}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit // Изменено для предотвращения обрезания изображения
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Детали товара
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Наименование: ${product.name}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Цена: ${product.price} руб.",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Описание: ${product.description ?: "Описание отсутствует"}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = if (product.stock > 0) "В наличии: ${product.stock}" else "Нет в наличии",
                style = MaterialTheme.typography.bodyLarge,
                color = if (product.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка Назад
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Назад", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
