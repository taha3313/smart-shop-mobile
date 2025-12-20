package com.example.smartshop.ui.products

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import android.net.Uri // New import
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description // New import
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartshop.data.AppDatabase
import com.example.smartshop.data.ProductRepository
import com.example.smartshop.viewmodels.ProductViewModel
import com.example.smartshop.viewmodels.ProductViewModelFactory
import com.example.smartshop.utils.ProductExporter // New import

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.rememberCoroutineScope // Added for clarity

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.smartshop.data.Product
import androidx.compose.material3.Button // Import Button
import androidx.compose.material3.MaterialTheme

// ... (other imports)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onAddProduct: () -> Unit,
    onProductClick: (Long) -> Unit,
    onStatsClick: () -> Unit,
    modifier: Modifier = Modifier,
    productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(
            AppDatabase.getDatabase(LocalContext.current).productDao(),
            FirebaseFirestore.getInstance(),
            FirebaseAuth.getInstance(),
            rememberCoroutineScope() // Pass the application scope
        )
    )
) {
    val context = LocalContext.current
    val products by productViewModel.allProducts.collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let {
            if (ProductExporter.exportProductsToCsv(context, products, it)) {
                Toast.makeText(context, "Products exported to CSV successfully!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Failed to export products to CSV.", Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartShop Products") },
                actions = {
                    IconButton(onClick = onStatsClick) {
                        Icon(Icons.Filled.Info, "View Statistics")
                    }
                    IconButton(onClick = {
                        if (products.isNotEmpty()) {
                            exportLauncher.launch("products_export_${System.currentTimeMillis()}.csv")
                        } else {
                            Toast.makeText(context, "No products to export.", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Filled.Description, "Export Products to CSV")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Filled.Add, "Add new product")
            }
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            if (products.isEmpty()) {
                Text("No products yet. Click the + button to add one!", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    items(products) { product ->
                        ProductListItem(
                            product = product,
                            onProductClick = onProductClick,
                            onDeleteClick = {
                                productToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    productToDelete = null
                },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete '${productToDelete?.name}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            productToDelete?.let { productViewModel.deleteProduct(it) }
                            showDeleteDialog = false
                            productToDelete = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            productToDelete = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
