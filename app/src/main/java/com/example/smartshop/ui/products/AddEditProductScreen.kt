@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.smartshop.ui.products

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter // Added import
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.smartshop.data.AppDatabase
import com.example.smartshop.data.Product
import com.example.smartshop.data.ProductRepository
import com.example.smartshop.viewmodels.ProductViewModel
import com.example.smartshop.viewmodels.ProductViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.smartshop.utils.StorageService
import com.google.firebase.auth.FirebaseAuth // Added import

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun AddEditProductScreen(
    productId: Long?,
    onProductAdded: () -> Unit,
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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var currentFirestoreId by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }

    val storageService = remember { StorageService(FirebaseStorage.getInstance()) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val isEditing = productId != null

    LaunchedEffect(productId) {
        if (isEditing) {
            val product = productViewModel.getProductById(productId!!)
            product?.let {
                name = it.name
                priceText = it.price.toString()
                quantityText = it.quantity.toString()
                currentFirestoreId = it.firestoreId
                currentImageUrl = it.imageUrl // Load existing image URL
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Product" else "Add Product") },
                navigationIcon = {
                    IconButton(onClick = onProductAdded) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val price = priceText.toDoubleOrNull()
                            val quantity = quantityText.toIntOrNull()

                            if (name.isBlank() || price == null || quantity == null || price <= 0 || quantity < 0) {
                                snackbarHostState.showSnackbar("Please enter valid product details (price > 0, quantity >= 0).")
                            } else {
                                var uploadedImageUrl: String? = currentImageUrl
                                if (selectedImageUri != null) {
                                    uploadedImageUrl = storageService.uploadImage(selectedImageUri!!)
                                    if (uploadedImageUrl == null) {
                                        snackbarHostState.showSnackbar("Failed to upload image.")
                                        return@launch
                                    }
                                }

                                val product = (if (isEditing) {
                                    Product(
                                        id = productId!!,
                                        firestoreId = currentFirestoreId,
                                        name = name,
                                        price = price,
                                        quantity = quantity,
                                        imageUrl = uploadedImageUrl
                                    )
                                } else {
                                    Product(
                                        name = name,
                                        price = price,
                                        quantity = quantity,
                                        imageUrl = uploadedImageUrl
                                    )
                                })
                                if (isEditing) {
                                    productViewModel.updateProduct(product)
                                } else {
                                    productViewModel.insertProduct(product)
                                }
                                onProductAdded()
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Check, "Save Product")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val imagePainter = rememberAsyncImagePainter(
                model = selectedImageUri ?: currentImageUrl,
                error = rememberVectorPainter(image = Icons.Default.Image) // Default error image
            )
            Image(
                painter = imagePainter,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePicker.launch("image/*") },
                contentScale = ContentScale.Crop
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
    }
}
