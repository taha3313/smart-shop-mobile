package com.example.smartshop.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firestoreId: String? = null,
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imageUrl: String? = null
)
