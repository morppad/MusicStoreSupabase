package com.example.musicstoretest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ProductImage(imageUrl: String?, contentDescription: String, modifier: Modifier = Modifier) {
    if (!imageUrl.isNullOrEmpty()) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        Text(
            text = "No Image",
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier
        )
    }
}
