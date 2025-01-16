package com.example.musicstoretest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicstoretest.data.models.Product

@Composable
fun ProductDetailsScreen(product: Product, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Детали товара",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Наименование: ${product.name}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
        Text(text = "Цена: ${product.price} руб.", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        Text(text = "Описание: ${product.description ?: "No description available"}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
        Text(text = "В наличии: ${product.stock}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}
