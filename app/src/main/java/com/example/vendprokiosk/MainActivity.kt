package com.example.vendprokiosk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.vendprokiosk.ui.theme.MyKioskAppTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// UI-specific Product data class
data class UiProduct( // Renamed from Product
    val id: String,
    val name: String,
    val description: String, // Will use category from API for this
    val price: Double,
    val imageResId: Int,
    val isAgeRestricted: Boolean,
    val stock: Int
)

// Helper function to map imageIdentifier from API to a drawable resource ID
// IMPORTANT: Add your actual drawable resources (e.g., cola.png, chips.png) to res/drawable
// and ensure the names match these strings.
fun mapImageIdentifierToDrawable(identifier: String): Int {
    return when (identifier.lowercase()) {
        "cola" -> R.drawable.cola
        "chips" -> R.drawable.chips
        "water" -> R.drawable.water
        // Add more mappings for your product imageIds
        else -> R.drawable.placeholder_product // Fallback placeholder
    }
}

class MainActivity : ComponentActivity() {
    private val inactivityTimeout = 15000L // 15 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Removed prominent onCreate log
        installSplashScreen()
        setContent {
            MyKioskAppTheme {
                var showScreensaver by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                var inactivityJob by remember { mutableStateOf<Job?>(null) }

                var machineDetails by remember { mutableStateOf<ApiMachineDetails?>(null) }
                var isLoading by remember { mutableStateOf(true) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) { // Runs once when the composable enters the composition
                    isLoading = true
                    try {
                        // Fetch data for a specific machine ID
                        val details = RetrofitClient.instance.getMachineDetails("vm-001")
                        machineDetails = details
                        errorMessage = null
                        Log.d("MainActivity", "Successfully fetched machine details: $details") // Reverted to original tag
                    } catch (e: Exception) {
                        errorMessage = "Error fetching data: ${e.message}"
                        Log.e("MainActivity", "Error fetching machine details", e) // Reverted to original tag and message
                        machineDetails = null // Clear any old data
                    } finally {
                        isLoading = false
                    }
                }

                fun startInactivityTimer() {
                    inactivityJob?.cancel()
                    inactivityJob = scope.launch {
                        delay(inactivityTimeout)
                        if (isActive) {
                            showScreensaver = true
                        }
                    }
                }

                fun dismissScreensaverAndResetTimer() {
                    showScreensaver = false
                    startInactivityTimer()
                }

                LaunchedEffect(showScreensaver) {
                    if (!showScreensaver) {
                        startInactivityTimer()
                    } else {
                        inactivityJob?.cancel()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (isLoading) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Text("Loading machine data...")
                            }
                        } else if (errorMessage != null) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("$errorMessage") // Display the exact error message state
                            }
                        } else if (machineDetails != null) {
                            val productsForUi = machineDetails!!.inventory
                                .filter { it.enabled && it.stock > 0 } // Only show enabled items with stock
                                .map { apiItem ->
                                    UiProduct( // Renamed from Product
                                        id = apiItem.id,
                                        name = apiItem.name,
                                        description = apiItem.category, // Using category as description
                                        price = apiItem.price,
                                        imageResId = mapImageIdentifierToDrawable(apiItem.imageIdentifier),
                                        isAgeRestricted = apiItem.isAgeRestricted,
                                        stock = apiItem.stock
                                    )
                                }
                            VendScreen(products = productsForUi) // VendScreen now needs to accept List<UiProduct>
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No machine data available.")
                            }
                        }
                    }

                    if (showScreensaver) {
                        ScreensaverView(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(onPress = {
                                        dismissScreensaverAndResetTimer()
                                        tryAwaitRelease()
                                    })
                                }
                        )
                    }
                }
            }
        }
    }
}
