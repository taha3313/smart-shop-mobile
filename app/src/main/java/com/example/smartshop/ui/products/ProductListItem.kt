package com.example.smartshop.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartshop.data.Product
import com.example.smartshop.ui.theme.SmartShopTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
fun ProductListItem(
    product: Product,
    onProductClick: (Long) -> Unit,
    onDeleteClick: (Product) -> Unit, // New parameter
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onProductClick(product.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = product.imageUrl,
                    error = rememberVectorPainter(image = Icons.Default.Image) // Default error image
                ),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Quantity: ${product.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "$${String.format("%.2f", product.price)}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = { onDeleteClick(product) }) { // Delete button
                Icon(Icons.Filled.Delete, "Delete Product")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListItemPreview() {
    SmartShopTheme {
        ProductListItem(
            product = Product(1, null, "Sample Product", 19.99, 50, imageUrl = "https://example.com/image.jpg"),
            onProductClick = {},
            onDeleteClick = {} // Provide an empty lambda for the preview
        )
    }
}
