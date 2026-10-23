package com.example.arogyagrid.data.remote

data class NetworkSupplyItem(
    val _id: String,
    val sku: String,
    val name: String,
    val category: String,
    val unit: String,
    val currentStock: Int,
    val minimumStockLevel: Int,
    val lastUpdated: String? = null
)

data class SupplyTransactionRequest(
    val transactionType: String,
    val quantity: Int
)

data class SupplyTransactionResponse(
    val message: String,
    val newStock: Int
)
