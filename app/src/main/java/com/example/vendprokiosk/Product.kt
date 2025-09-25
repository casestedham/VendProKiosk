package com.example.vendprokiosk

import kotlinx.serialization.Serializable // Added import

@Serializable // Added annotation
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,      // Primary image source from backend
    val imageResId: Int? = null, // Ensure default for properties not always present in JSON
    val isAgeRestricted: Boolean = false,
    val category: String? = null, // e.g., "Snacks", "Drinks"
    val stockQuantity: Int? = null  // Current stock, if provided by backend
)
