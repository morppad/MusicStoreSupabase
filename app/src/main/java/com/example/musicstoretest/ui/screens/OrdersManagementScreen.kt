package com.example.musicstoretest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicstoretest.data.models.Order
import com.example.musicstoretest.data.models.UpdateOrderRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderManagementScreen(
    orders: List<Order>,
    onUpdateOrder: (String, String) -> Unit,
    onViewOrderItems: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление заказами", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                OrderCard(
                    order = order,
                    onUpdateOrder = { orderId, newStatus ->
                        onUpdateOrder(orderId, newStatus)
                    },
                    onViewOrderItems = onViewOrderItems
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onUpdateOrder: (String, String) -> Unit,
    onViewOrderItems: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(order.status) }
    val statuses = listOf("Processing", "Completed", "Cancelled")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "ID заказа: ${order.id}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Общая цена: ${order.total_price} руб.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Адрес доставки: ${order.address ?: "Не указан"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onViewOrderItems(order.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp), // Высота кнопки для выравнивания
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Товары")
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp) // Высота для выравнивания с кнопкой
                ) {
                    Button(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(text = "Статус: $selectedStatus", maxLines = 1)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    selectedStatus = status
                                    expanded = false
                                    onUpdateOrder(order.id, status)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


