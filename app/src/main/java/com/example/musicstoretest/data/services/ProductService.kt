package com.example.musicstoretest.data.services
import android.content.Context
import java.io.File
import android.util.Log
import androidx.compose.runtime.Composable
import com.example.musicstoretest.data.models.Product
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import io.ktor.websocket.Frame
import java.io.InputStream
import java.util.Locale.filter


fun getCurrentTimestamp(): String {
    return Instant.now()
        .atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT) // Формат: 2025-01-01T10:00:00Z
}

suspend fun fetchProducts(): List<Product> {
    return try {
        val products = supabase.from("products")
            .select()
            .decodeList<Product>()
        Log.d("FetchProducts", "Loaded products: $products")
        products
    } catch (e: Exception) {
        Log.e("FetchProducts", "Error fetching products", e)
        emptyList()
    }
}

suspend fun uploadImage(filePath: String, fileName: String): String? {
    return try {
        val file = File(filePath)
        val response = supabase.storage.from("product-images").upload(fileName, file.readBytes())
        // Возвращаем URL изображения
        supabase.storage.from("product-images").publicUrl(fileName)
    } catch (e: Exception) {
        Log.e("StorageService", "Error uploading image", e)
        null
    }
}
suspend fun addProduct(product: Product): Boolean {
    return try {
        supabase.from("products").insert(product)
        true
    } catch (e: Exception) {
        Log.e("ProductService", "Error adding product", e)
        false
    }
}

suspend fun updateProduct(productId: String, updates: Map<String, Any?>): Boolean {
    return try {
        val sanitizedUpdates = updates.toMutableMap()
        sanitizedUpdates["updated_at"] = getCurrentTimestamp()

        val jsonObject = buildJsonObject {
            sanitizedUpdates.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                    null -> put(key, null as String?)
                    else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
                }
            }
        }

        supabase.from("products").update(Json.encodeToJsonElement(jsonObject)) {
            filter { eq("id", productId) }
        }
        true
    } catch (e: Exception) {
        Log.e("ProductService", "Error updating product", e)
        false
    }
}

fun Product.diff(original: Product): Map<String, Any?> {
    val updates = mutableMapOf<String, Any?>()

    if (name != original.name) updates["name"] = name
    if (price != original.price) updates["price"] = price
    if (description != original.description) updates["description"] = description
    if (image_url != original.image_url) updates["image_url"] = image_url
    if (stock != original.stock) updates["stock"] = stock

    if (updates.isNotEmpty()) {
        updates["updated_at"] = System.currentTimeMillis()
    }

    return updates
}



suspend fun deleteProduct(productId: String): Boolean {
    return try {
        supabase.from("products").delete {
            filter { eq("id", productId) }
        }
        true
    } catch (e: Exception) {
        Log.e("ProductService", "Error deleting product", e)
        false
    }
}


@Composable
fun selectImage(onImageSelected: (Uri?) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        onImageSelected(uri)
    }

    Button(onClick = {
        launcher.launch("image/*")
    }) {
        Frame.Text("Select Image")
    }
}

suspend fun uploadImage(uri: Uri, context: Context, fileName: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        if (bytes != null) {
            supabase.storage.from("product-images").upload(fileName, bytes)
            supabase.storage.from("product-images").publicUrl(fileName)
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("StorageService", "Error uploading image", e)
        null
    }
}

