package com.example.smartshop.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first

class ProductRepository(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val applicationScope: CoroutineScope // Provide a CoroutineScope from the application
) {

    private val productsCollection = firestore.collection("products")

    init {
        // Observe changes from Firestore and sync to Room
        applicationScope.launch {
            firebaseAuth.addAuthStateListener { auth ->
                applicationScope.launch { // Launch a new coroutine for suspend calls inside the listener
                    if (auth.currentUser != null) {
                        getProductsFromFirestore().collect { firestoreProducts ->
                            val localProducts = productDao.getAllProducts().first() // Get current local products

                            // Identify products to delete from Room (exist locally but not in Firestore)
                            localProducts.forEach { localProduct ->
                                if (localProduct.firestoreId != null && firestoreProducts.none { it.firestoreId == localProduct.firestoreId }) {
                                    productDao.deleteProduct(localProduct)
                                }
                            }

                            // Identify products to add/update in Room (exist in Firestore)
                            firestoreProducts.forEach { firestoreProduct ->
                                val existingLocalProduct = localProducts.firstOrNull { it.firestoreId == firestoreProduct.firestoreId }
                                if (existingLocalProduct == null) {
                                    // Product exists in Firestore but not locally, insert it
                                    productDao.insertProduct(firestoreProduct)
                                } else if (existingLocalProduct != firestoreProduct.copy(id = existingLocalProduct.id)) { // Compare all fields except Room ID
                                    // Product exists both locally and in Firestore, and they differ, update it
                                    productDao.updateProduct(firestoreProduct.copy(id = existingLocalProduct.id))
                                }
                            }
                        }
                    } else {
                        // User logged out, clear local products that were synced from Firestore (optional, depending on requirements)
                        // productDao.deleteAllProducts() // You might want to implement this in ProductDao
                    }
                }
            }
        }
    }

    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
    }

    suspend fun getProductById(id: Long): Product? {
        return productDao.getProductById(id)
    }

    suspend fun insertProduct(product: Product) {
        val newProductId = productDao.insertProduct(product)
        val productWithRoomId = product.copy(id = newProductId)

        val firestoreId = addProductToFirestore(productWithRoomId)
        if (firestoreId != null) {
            val updatedProduct = productWithRoomId.copy(firestoreId = firestoreId)
            productDao.updateProduct(updatedProduct)
        }
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
        updateProductInFirestore(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
        deleteProductFromFirestore(product)
    }

    suspend fun addProductToFirestore(product: Product): String? {
        return try {
            if (product.firestoreId == null) {
                val docRef = productsCollection.add(product).await()
                docRef.id
            } else {
                productsCollection.document(product.firestoreId).set(product).await()
                product.firestoreId
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateProductInFirestore(product: Product): Boolean {
        return try {
            product.firestoreId?.let {
                productsCollection.document(it).set(product).await()
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteProductFromFirestore(product: Product): Boolean {
        return try {
            product.firestoreId?.let {
                productsCollection.document(it).delete().await()
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getProductsFromFirestore(): Flow<List<Product>> = callbackFlow {
        val subscription = productsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val products = snapshot.documents.mapNotNull { document ->
                    document.toObject(Product::class.java)?.copy(firestoreId = document.id)
                }
                trySend(products)
            }
        }
        awaitClose { subscription.remove() }
    }
}