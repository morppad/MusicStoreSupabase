package com.example.musicstoretest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.musicstoretest.admin.AdminDashboard
import com.example.musicstoretest.auth.LoginScreen
import com.example.musicstoretest.auth.RegisterScreen
import com.example.musicstoretest.data.models.Product
import com.example.musicstoretest.data.models.CartItem
import com.example.musicstoretest.data.services.addProduct
import com.example.musicstoretest.data.services.addToCart
import com.example.musicstoretest.data.services.deleteProduct
import com.example.musicstoretest.data.services.diff
import com.example.musicstoretest.data.services.fetchProducts
import com.example.musicstoretest.data.services.updateProduct
import com.example.musicstoretest.ui.screens.AddEditProductScreen
import com.example.musicstoretest.ui.screens.CartScreen
import com.example.musicstoretest.ui.screens.ProductDetailsScreen
import com.example.musicstoretest.ui.screens.ProductsManagementScreen
import com.example.musicstoretest.ui.screens.GuestCatalogScreen
import com.example.musicstoretest.ui.screens.UserCatalogScreen

import com.example.musicstoretest.ui.theme.MusicStoreTestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Устанавливаем режим, чтобы контент не заходил под системные области
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            MusicStoreTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("main") }
    var userRole by remember { mutableStateOf<String?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    var currentUserId by remember { mutableStateOf<String?>(null) } // Сохраняем текущего пользователя

    LaunchedEffect(currentScreen) {
        if (currentScreen == "catalog" || currentScreen == "guestCatalog" || currentScreen == "manageProducts") {
            val loadedProducts = fetchProducts()
            Log.d("AppNavigation", "Fetched products: $loadedProducts")
            products = loadedProducts
        }
    }

    when (currentScreen) {
        "main" -> MainScreen(
            onLoginClick = { currentScreen = "login" },
            onRegisterClick = { currentScreen = "register" },
            onGuestLoginClick = { currentScreen = "guestCatalog" } // Гостевой вход
        )
        "login" -> LoginScreen(
            onLoginSuccess = { userId, role ->
                currentUserId = userId // Сохраняем userId
                userRole = role // Сохраняем роль
                currentScreen = if (role == "admin") "adminDashboard" else "catalog"
            },
            onBack = { currentScreen = "main" }
        )

        "register" -> RegisterScreen(
            onRegisterSuccess = { currentScreen = "main" },
            onBack = { currentScreen = "main" }
        )
        "catalog" -> UserCatalogScreen(
            products = products,
            onProductClick = { product ->
                selectedProduct = product
                currentScreen = "details"
            },
            onLogout = { currentScreen = "main" },
            onAddToCart = { product ->
                coroutineScope.launch {
                    val userId = currentUserId
                    if (userId != null) {
                        if (addToCart(userId, product.id)) {
                            Log.d("Cart", "Product added to cart: ${product.name}")
                        } else {
                            Log.e("Cart", "Failed to add product to cart")
                        }
                    } else {
                        Log.e("Cart", "User ID is null. Cannot add to cart.")
                    }
                }
            }
,
            onViewCart = {
                currentScreen = "cart"
            }
        )


        "guestCatalog" -> GuestCatalogScreen(
            products = products,
            onProductClick = { product ->
                selectedProduct = product
                currentScreen = "guestDetails"
            },
            onLogout = { currentScreen = "main" }
        )
        "details" -> selectedProduct?.let { product ->
            ProductDetailsScreen(
                product = product,
                onBack = { currentScreen = "catalog" }
            )
        } ?: run {
            currentScreen = "catalog"
        }
        "guestDetails" -> selectedProduct?.let { product ->
            ProductDetailsScreen(
                product = product,
                onBack = { currentScreen = "guestCatalog" }
            )
        } ?: run {
            currentScreen = "guestCatalog"
        }
        "adminDashboard" -> AdminDashboard(
            onManageProductsClick = { currentScreen = "manageProducts" },
            onManageUsersClick = { currentScreen = "manageUsers" },
            onManageOrdersClick = { currentScreen = "manageOrders" },
            onLogout = { currentScreen = "main" }
        )
        "manageProducts" -> ProductsManagementScreen(
            products = products,
            onAddProductClick = {
                selectedProduct = null
                currentScreen = "addEditProduct"
            },
            onEditProductClick = { product ->
                selectedProduct = product
                currentScreen = "addEditProduct"
            },
            onDeleteProductClick = { product ->
                coroutineScope.launch {
                    if (deleteProduct(product.id)) {
                        products = fetchProducts()
                    }
                }
            },
            onBack = { currentScreen = "adminDashboard" }
        )
        "addEditProduct" -> AddEditProductScreen(
            product = selectedProduct,
            onSave = { updatedProduct ->
                coroutineScope.launch {
                    if (selectedProduct != null) {
                        val updates = updatedProduct.diff(selectedProduct!!)
                        updateProduct(selectedProduct!!.id, updates)
                    } else {
                        addProduct(updatedProduct)
                    }
                    products = fetchProducts()
                    currentScreen = "manageProducts"
                }
            },
            onCancel = { currentScreen = "manageProducts" }
        )
        "cart" -> currentUserId?.let { userId ->
            CartScreen(
                userId = userId,
                onBack = { currentScreen = "catalog" }
            )
        }
    }
}

@Composable
fun MainScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Онлайн магазин музыкальных товаров",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вход")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Регистрация")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGuestLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти как гость")
        }
    }
}

