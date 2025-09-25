package com.example.vendprokiosk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiMachineDetails(
    val id: String,
    val name: String,
    val location: String,
    val slots: Int,
    val inventory: List<ApiInventoryItem>
)

@Serializable
data class ApiInventoryItem(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    @SerialName("imageId") // Maps JSON 'imageId' to this field
    val imageIdentifier: String, // e.g., "cola", "chips" - we'll map this to a drawable later
    @SerialName("ageRestriction")
    val ageRestrictionValue: Int, // Assuming 0 for no restriction, 1 (or other) for restriction
    val enabled: Boolean,
    val stock: Int
) {
    // Helper property to convert ageRestrictionValue to a Boolean
    val isAgeRestricted: Boolean
        get() = ageRestrictionValue != 0
}
