package com.example.musicstoretest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.musicstoretest.data.models.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    userId: String,
    cartItems: List<CartItem>,
    onBack: () -> Unit,
    onPlaceOrder: (String) -> Unit,
    onRemoveItem: (CartItem) -> Unit
) {
    val context = LocalContext.current
    var showPaymentScreen by remember { mutableStateOf(false) }
    val orderTotal by remember(cartItems) {
        derivedStateOf {
            cartItems.sumOf { it.quantity * it.products.price }.toDouble()
        }
    }
    var deliveryAddress by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Корзина") },
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
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Ваша корзина пуста",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cartItems) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onRemove = { onRemoveItem(cartItem) }
                            )
                        }
                    }

                    CartSummary(cartItems = cartItems, onPlaceOrder = onPlaceOrder)
                }
            }
        }
    }


@Composable
fun CartItemCard(cartItem: CartItem, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(cartItem.products.image_url),
                contentDescription = "Изображение товара",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.products.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Количество: ${cartItem.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = "Цена: ${cartItem.products.price} ₽",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onRemove,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Удалить", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

@Composable
fun CartSummary(cartItems: List<CartItem>, onPlaceOrder: (String) -> Unit) {
    val totalPrice = cartItems.sumOf { it.quantity * it.products.price }
    var showAddressDialog by remember { mutableStateOf(false) }

    if (showAddressDialog) {
        AddressDialog(
            onConfirm = { address ->
                showAddressDialog = false
                onPlaceOrder(address)
            },
            onDismiss = { showAddressDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Итого:",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "$totalPrice ₽",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showAddressDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Оформить заказ", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun AddressDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Введите адрес доставки") },
        text = {
            Column {
                Text("Пожалуйста, укажите адрес для доставки заказа.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Адрес") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(address) }, shape = MaterialTheme.shapes.small) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, shape = MaterialTheme.shapes.small) {
                Text("Отмена")
            }
        }
    )
}
