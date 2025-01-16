package com.example.musicstoretest.admin

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
            .padding(16.dp)
    ) {
        Text(
            "Admin Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(onClick = onManageProductsClick) {
            Text("Manage Products")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onManageUsersClick) {
            Text("Manage Users")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onManageOrdersClick) {
            Text("Manage Orders")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}
