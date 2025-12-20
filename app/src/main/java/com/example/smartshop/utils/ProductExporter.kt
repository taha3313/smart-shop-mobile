package com.example.smartshop.utils

import android.content.Context
import android.net.Uri
import com.example.smartshop.data.Product
import java.io.OutputStreamWriter

object ProductExporter {

    fun exportProductsToCsv(context: Context, products: List<Product>, uri: Uri): Boolean {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    // Write CSV header
                    writer.append("ID,Name,Price,Quantity\n")

                    // Write product data
                    products.forEach { product ->
                        writer.append("${product.id},${product.name},${product.price},${product.quantity}\n")
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}

