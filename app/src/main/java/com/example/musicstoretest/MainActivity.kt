package com.example.musicstoretest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.musicstoretest.data.models.UpdateOrderRequest
import com.example.musicstoretest.data.models.User
import com.example.musicstoretest.data.services.addProduct
import com.example.musicstoretest.data.services.addToCart
import com.example.musicstoretest.data.services.addUser
import com.example.musicstoretest.data.services.deleteProduct
import com.example.musicstoretest.data.services.deleteUser
import com.example.musicstoretest.data.services.diff
import com.example.musicstoretest.data.services.fetchCart
import com.example.musicstoretest.data.services.fetchOrderItems
import com.example.musicstoretest.data.services.fetchOrders
import com.example.musicstoretest.data.services.fetchOrdersAdmin
import com.example.musicstoretest.data.services.fetchProducts
import com.example.musicstoretest.data.services.fetchUsers
import com.example.musicstoretest.data.services.placeOrder
import com.example.musicstoretest.data.services.removeCartItemSafely
import com.example.musicstoretest.data.services.supabase
import com.example.musicstoretest.data.services.toUpdateRequest
import com.example.musicstoretest.data.services.updateOrder
import com.example.musicstoretest.data.services.updateProduct
import com.example.musicstoretest.data.services.updateUser
import com.example.musicstoretest.ui.screens.AddEditProductScreen
import com.example.musicstoretest.ui.screens.AddEditUserScreen
import com.example.musicstoretest.ui.screens.CartScreen
import com.example.musicstoretest.ui.screens.ProductDetailsScreen
import com.example.musicstoretest.ui.screens.ProductsManagementScreen
import com.example.musicstoretest.ui.screens.GuestCatalogScreen
import com.example.musicstoretest.ui.screens.OrderConfirmationScreen
import com.example.musicstoretest.ui.screens.OrderDetailsScreen
import com.example.musicstoretest.ui.screens.OrderHistoryScreen
import com.example.musicstoretest.ui.screens.OrderManagementScreen
import com.example.musicstoretest.ui.screens.UserCatalogScreen
import com.example.musicstoretest.ui.screens.UsersManagementScreen

import com.example.musicstoretest.ui.theme.MyAppTheme
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Устанавливаем режим, чтобы контент не заходил под системные области
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            MyAppTheme {
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
    val selectedOrder by remember { mutableStateOf<Order?>(null) }
    var selectedOrderItems by remember { mutableStateOf<List<OrderItem>?>(null) }
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
            onAddToCart = { product, callback ->
                coroutineScope.launch {
                    currentUserId?.let { userId ->
                        val result = addToCart(userId, product.id)
                        callback(result)
                    }
                }
            },
            onViewCart = { currentScreen = "cart" },
            onViewOrderHistory = { currentScreen = "orderHistory" },
            onBack = { currentScreen = "main" }

        )
        "guestCatalog" -> GuestCatalogScreen(
            products = products,
            onProductClick = { product ->
                selectedProduct = product
                currentScreen = "guestDetails"
            },
            onBack = { currentScreen = "main" }

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
            onLogout = { currentScreen = "main" },
            onBack = { currentScreen = "main" }
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
            onCancel = { currentScreen = "manageProducts" },
            onBack = { currentScreen = "manageProducts" }
        )
        "cart" -> currentUserId?.let { userId ->
            LaunchedEffect(userId) {
                cartItems = fetchCart(userId) // Загружаем корзину пользователя
            }
            CartScreen(
                userId = userId,
                cartItems = cartItems,
                onBack = { currentScreen = "catalog" },
                onPlaceOrder = { address ->
                    coroutineScope.launch {
                        val success = placeOrder(userId, address)
                        if (success) {
                            cartItems = emptyList() // Очищаем локальную корзину
                            currentScreen = "orderConfirmation"
                        }
                    }
                },
                onRemoveItem = { cartItem ->
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
                    onCancel = { currentScreen = "manageUsers" },
                    onBack = { currentScreen = "manageUsers" }
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
                    onCancel = { currentScreen = "manageUsers" },
                    onBack = { currentScreen = "manageUsers" }
                )
            }
        }
        "manageOrders" -> {


            LaunchedEffect(Unit) {
                orders = fetchOrdersAdmin()
            }

            if (selectedOrderItems != null) {
                // Экран просмотра товаров в заказе
                OrderDetailsScreen(
                    orderItems = selectedOrderItems ?: emptyList(),
                    onBack = { selectedOrderItems = null }
                )
            } else {
                // Основной экран управления заказами
                OrderManagementScreen(
                    orders = orders,
                    onUpdateOrder = { orderId, newStatus ->
                        coroutineScope.launch {
                            val updateRequestJson = Json.encodeToString(
                                UpdateOrderRequest.serializer(),
                                UpdateOrderRequest(status = newStatus, address = null)
                            )
                            val success = updateOrder(orderId, updateRequestJson)
                            if (success) {
                                orders = fetchOrdersAdmin() // Обновляем список заказов
                            }
                        }
                    },
                    onViewOrderItems = { orderId -> // Исправлено: параметр переименован
                        coroutineScope.launch {
                            selectedOrderItems = fetchOrderItems(orderId)
                        }
                    },
                    onBack = { currentScreen = "adminDashboard" },
                )
            }
        }


        "orderDetails" -> selectedOrder?.let { order ->
            var orderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }

            LaunchedEffect(order.id) {
                orderItems = fetchOrderItems(order.id)
            }

            OrderDetailsScreen(
                orderItems = selectedOrderItems ?: emptyList(),
                onBack = { currentScreen = "manageOrders" }
            )

        } ?: run {
            currentScreen = "manageOrders"
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
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Вход")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text("Регистрация")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGuestLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text("Войти как гость")
        }
    }
}