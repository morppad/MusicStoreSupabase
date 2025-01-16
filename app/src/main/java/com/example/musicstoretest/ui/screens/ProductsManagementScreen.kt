package com.example.musicstoretest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicstoretest.data.models.Product
import com.example.musicstoretest.ui.components.ProductImage

@Composable
fun ProductsManagementScreen(
    products: List<Product>,
    onAddProductClick: () -> Unit,
    onEditProductClick: (Product) -> Unit,
    onDeleteProductClick: (Product) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Products Management",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(onClick = onAddProductClick, modifier = Modifier.fillMaxWidth()) {
            Text("Add Product")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(products, key = { it.id }) { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!product.image_url.isNullOrEmpty()) {
                        ProductImage(
                            imageUrl = product.image_url,
                            contentDescription = "Product Image",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(product.name, style = MaterialTheme.typography.bodyLarge)
                        Text("Price: ${product.price}", style = MaterialTheme.typography.bodySmall)
                        Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick = { onEditProductClick(product) }) {
                        Text("Edit")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onDeleteProductClick(product) }) {
                        Text("Delete")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}