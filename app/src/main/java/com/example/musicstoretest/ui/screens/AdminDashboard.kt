package com.example.musicstoretest.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminDashboard(
    onManageProductsClick: () -> Unit,
    onManageUsersClick: () -> Unit,
    onManageOrdersClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // Центрируем контент по вертикали
        horizontalAlignment = Alignment.CenterHorizontally // Центрируем по горизонтали
    ) {
        Text(
            text = "Панель администратора",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onManageProductsClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.small // Устанавливаем общий стиль для кнопок
        ) {
            Text("Управление товарами")
        }

        Button(
            onClick = onManageUsersClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Управление пользователями")
        }

        Button(
            onClick = onManageOrdersClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Управление заказами")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Отступ перед кнопкой "Выход"

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Выйти")
        }
    }
}
