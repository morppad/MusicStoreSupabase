package com.example.musicstoretest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicstoretest.data.models.Product
import com.example.musicstoretest.ui.components.AppTopBar
import com.example.musicstoretest.ui.components.ProductImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsManagementScreen(
    products: List<Product>,
    onAddProductClick: () -> Unit,
    onEditProductClick: (Product) -> Unit,
    onDeleteProductClick: (Product) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Управление товарами",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 8.dp) // Центровка текста по вертикали
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()) // Только отступ сверху
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = onAddProductClick,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Добавить товар")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp) // Пространство между товарами
            ) {
                items(products, key = { it.id }) { product ->
                    ProductRow(
                        product = product,
                        onEditProductClick = onEditProductClick,
                        onDeleteProductClick = onDeleteProductClick
                    )
                }
            }
        }
    }
}


@Composable
fun ProductRow(
    product: Product,
    onEditProductClick: (Product) -> Unit,
    onDeleteProductClick: (Product) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!product.image_url.isNullOrEmpty()) {
            ProductImage(
                imageUrl = product.image_url,
                contentDescription = "Изображение товара",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1, // Обрезаем длинные названия
                overflow = TextOverflow.Ellipsis // Показываем троеточие для обрезанных строк
            )
            Text(
                text = "Цена: ${product.price} руб.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "На складе: ${product.stock}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        IconButton(
            onClick = { onEditProductClick(product) },
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit, // Иконка карандаша
                contentDescription = "Изменить товар",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = { onDeleteProductClick(product) },
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete, // Иконка корзины
                contentDescription = "Удалить товар",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
