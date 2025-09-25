package com.example.vendprokiosk

import kotlinx.serialization.Serializable // Added import

@Serializable // Added annotation
data class AdBanner(
    val adId: String,
    val imageUrl: String, // Assuming ad images will always come from a URL
    val description: String? = null, // For accessibility or if needed
    val actionUrl: String? = null, // Optional: if tapping the ad does something
    val displayTimeInSeconds: Int? = null // Optional: for rotating ads
)
