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
import com.example.musicstoretest.data.models.Order
import com.example.musicstoretest.data.models.OrderItem
import com.example.musicstoretest.data.models.User
import com.example.musicstoretest.data.services.addProduct
import com.example.musicstoretest.data.services.addToCart
import com.example.musicstoretest.data.services.addUser
import com.example.musicstoretest.data.services.deleteProduct
import com.example.musicstoretest.data.services.deleteUser
import com.example.musicstoretest.data.services.diff
import com.example.musicstoretest.data.services.fetchCart
import com.example.musicstoretest.data.services.fetchOrders
import com.example.musicstoretest.data.services.fetchProducts
import com.example.musicstoretest.data.services.fetchUsers
import com.example.musicstoretest.data.services.placeOrder
import com.example.musicstoretest.data.services.removeCartItemSafely
import com.example.musicstoretest.data.services.supabase
import com.example.musicstoretest.data.services.toUpdateRequest
import com.example.musicstoretest.data.services.updateProduct
import com.example.musicstoretest.data.services.updateUser
import com.example.musicstoretest.ui.screens.AddEditProductScreen
import com.example.musicstoretest.ui.screens.AddEditUserScreen
import com.example.musicstoretest.ui.screens.CartScreen
import com.example.musicstoretest.ui.screens.ProductDetailsScreen
import com.example.musicstoretest.ui.screens.ProductsManagementScreen
import com.example.musicstoretest.ui.screens.GuestCatalogScreen
import com.example.musicstoretest.ui.screens.OrderConfirmationScreen
import com.example.musicstoretest.ui.screens.OrderHistoryScreen
import com.example.musicstoretest.ui.screens.UserCatalogScreen
import com.example.musicstoretest.ui.screens.UsersManagementScreen

import com.example.musicstoretest.ui.theme.MusicStoreTestTheme
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.util.UUID

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
    var currentScreen by remember { mutableStateOf("main") } // Текущий экран
    var userRole by remember { mutableStateOf<String?>(null) } // Роль текущего пользователя
    var selectedProduct by remember { mutableStateOf<Product?>(null) } // Выбранный продукт
    var products by remember { mutableStateOf<List<Product>>(emptyList()) } // Список всех продуктов
    var currentUserId by remember { mutableStateOf<String?>(null) } // ID текущего пользователя
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) } // Товары в корзине
    var users by remember { mutableStateOf<List<User>>(emptyList()) } // Список пользователей
    var selectedUser by remember { mutableStateOf<User?>(null) } // Выбранный пользователь
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) } // Список заказов
    val coroutineScope = rememberCoroutineScope() // Скоуп для выполнения операций

    // Helper function to fetch products
    LaunchedEffect(currentScreen) {
        if (currentScreen in listOf("catalog", "guestCatalog", "manageProducts")) {
            products = fetchProducts()
        }
    }

    suspend fun fetchOrdersSafely(userId: String): List<Order> = try {
        fetchOrders(userId)
    } catch (e: Exception) {
        Log.e("OrderService", "Error fetching orders", e)
        emptyList()
    }

    suspend fun placeOrderSafely(userId: String, address: String): Boolean {
        return try {
            val orderId = UUID.randomUUID().toString()
            val totalPrice = cartItems.sumOf { it.quantity * it.products.price }

            val order = Order(
                id = orderId,
                user_id = userId,
                total_price = totalPrice,
                address = address // Добавляем адрес
            )

            supabase.from("orders").insert(order)

            val orderItems = cartItems.map { cartItem ->
                OrderItem(
                    id = UUID.randomUUID().toString(),
                    order_id = orderId,
                    product_id = cartItem.product_id,
                    quantity = cartItem.quantity,
                    price = cartItem.products.price
                )
            }

            supabase.from("order_items").insert(orderItems)

            supabase.from("carts").delete {
                filter { eq("user_id", userId) }
            }

            Log.d("OrderService", "Order placed successfully: $orderId")
            true
        } catch (e: Exception) {
            Log.e("OrderService", "Error placing order", e)
            false
        }
    }


    when (currentScreen) {
        "main" -> MainScreen(
            onLoginClick = { currentScreen = "login" },
            onRegisterClick = { currentScreen = "register" },
            onGuestLoginClick = { currentScreen = "guestCatalog" }
        )
        "login" -> LoginScreen(
            onLoginSuccess = { userId, role ->
                currentUserId = userId
                userRole = role
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
                    currentUserId?.let {
                        if (!addToCart(it, product.id)) {
                            Log.e("Cart", "Failed to add product to cart")
                        }
                    } ?: Log.e("Cart", "User ID is null")
                }
            },
            onViewCart = { currentScreen = "cart" },
            onViewOrderHistory = { currentScreen = "orderHistory" }
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
        }
        "guestDetails" -> selectedProduct?.let { product ->
            ProductDetailsScreen(
                product = product,
                onBack = { currentScreen = "guestCatalog" }
            )
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
            LaunchedEffect(userId) {
                cartItems = fetchCart(userId) // Загружаем корзину пользователя
            }
            CartScreen(
                userId = userId,
                cartItems = cartItems, // Передаём список товаров
                onBack = { currentScreen = "catalog" },
                onPlaceOrder = { address -> // Передаём адрес в placeOrderSafely
                    coroutineScope.launch {
                        if (placeOrderSafely(userId, address)) {
                            cartItems = emptyList()
                            currentScreen = "orderConfirmation"
                        }
                    }
                },
                onRemoveItem = { cartItem -> // Передаём логику удаления
                    coroutineScope.launch {
                        if (removeCartItemSafely(cartItem)) {
                            cartItems = fetchCart(userId) // Обновляем корзину после удаления
                        }
                    }
                }
            )
        }


        "orderHistory" -> {
            LaunchedEffect(currentUserId) {
                currentUserId?.let {
                    orders = fetchOrdersSafely(it)
                }
            }
            OrderHistoryScreen(
                orders = orders,
                onBack = { currentScreen = "catalog" }
            )
        }
        "orderConfirmation" -> OrderConfirmationScreen(
            onBackToCatalog = { currentScreen = "catalog" }
        )
        "manageUsers" -> {

            LaunchedEffect(Unit) {
                users = fetchUsers() // Загружаем список пользователей
            }

            UsersManagementScreen(
                users = users,
                onAddUserClick = {
                    selectedUser = null // Сбрасываем выбранного пользователя при добавлении нового
                    currentScreen = "addEditUser"
                },
                onEditUserClick = { user ->
                    selectedUser = user // Устанавливаем выбранного пользователя для редактирования
                    currentScreen = "addEditUser"
                },
                onDeleteUserClick = { user ->
                    coroutineScope.launch {
                        if (deleteUser(user.id)) {
                            users = fetchUsers() // Обновляем список после удаления
                        }
                    }
                },
                onBack = { currentScreen = "adminDashboard" }
            )
        }
        "addEditUser" -> {
            if (selectedUser == null) {
                AddEditUserScreen(
                    user = null, // Для создания нового пользователя
                    onSave = { updatedUser ->
                        coroutineScope.launch {
                            addUser(updatedUser) // Добавляем нового пользователя
                            val users = fetchUsers() // Обновляем список пользователей
                            currentScreen = "manageUsers"
                        }
                    },
                    onCancel = { currentScreen = "manageUsers" }
                )
            } else {
                AddEditUserScreen(
                    user = selectedUser, // Передаём выбранного пользователя для редактирования
                    onSave = { updatedUser ->
                        coroutineScope.launch {
                            updateUser(updatedUser.id, updatedUser.toUpdateRequest()) // Преобразуем User в UpdateUserRequest
                            val users = fetchUsers() // Обновляем список пользователей
                            currentScreen = "manageUsers"
                        }
                    },
                    onCancel = { currentScreen = "manageUsers" }
                )
            }
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

