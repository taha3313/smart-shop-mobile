package com.example.smartshop.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartshop.data.AppDatabase
import com.example.smartshop.data.ProductRepository
import com.example.smartshop.viewmodels.ProductViewModel
import com.example.smartshop.viewmodels.ProductViewModelFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartshop.ui.theme.SmartShopTheme

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.rememberCoroutineScope // Added for clarity

// ... (other imports)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        val products by productViewModel.allProducts.collectAsState(initial = emptyList())

        val totalProducts = products.size
        val totalStockValue = products.sumOf { it.price * it.quantity }

        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Products:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = totalProducts.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Stock Value:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${String.format("%.2f", totalStockValue)}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            // TODO: Add simple bar chart or pie chart visualization here
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    SmartShopTheme {
        StatisticsScreen(onNavigateBack = {})
    }
}
