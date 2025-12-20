package com.example.smartshop.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageService(private val storage: FirebaseStorage) {

    private val imagesRef = storage.reference.child("product_images")

    suspend fun uploadImage(imageUri: Uri): String? {
        return try {
            val uuid = UUID.randomUUID().toString()
            val imageRef = imagesRef.child("$uuid.jpg")
            val uploadTask = imageRef.putFile(imageUri).await()
            uploadTask.storage.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteImage(imageUrl: String): Boolean {
        return try {
            storage.getReferenceFromUrl(imageUrl).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
