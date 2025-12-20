package com.example.smartshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.Product
import com.example.smartshop.data.ProductRepository
import com.example.smartshop.data.AppDatabase
import com.example.smartshop.data.ProductDao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    val allProducts: Flow<List<Product>> = repository.getAllProducts()

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    suspend fun getProductById(id: Long): Product? {
        return repository.getProductById(id)
    }
}

class ProductViewModelFactory(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val applicationScope: CoroutineScope // Application-wide scope for background tasks
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(ProductRepository(productDao, firestore, firebaseAuth, applicationScope)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}