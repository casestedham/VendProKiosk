package com.example.vendprokiosk

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vendprokiosk.ui.theme.MyKioskAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@Composable
fun HorizontallyScrollingProductRow(
    products: List<UiProduct>, // Changed to UiProduct
    cardWidth: Dp = 349.dp,
    cardHeight: Dp = 668.dp,
    scrollSpeedFactor: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) {
        return
    }

    val lazyListState = rememberLazyListState()
    val isUserDragging by lazyListState.interactionSource.collectIsDraggedAsState()
    var isCardPressed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = products, key2 = lazyListState, key3 = scrollSpeedFactor) {
        if (products.size > 1) {
            while (isActive) {
                val pixelsToScroll = 1.5f * scrollSpeedFactor 

                if (isUserDragging || isCardPressed) {
                    delay(100L) 
                } else {
                    if (!lazyListState.isScrollInProgress) {
                        lazyListState.scrollBy(pixelsToScroll)
                    }
                    delay(16L) 
                }
            }
        }
    }

    LazyRow(
        state = lazyListState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        userScrollEnabled = true 
    ) {
        items(
            count = Int.MAX_VALUE,
            key = { index -> products[index % products.size].id + "_instance_" + (index / products.size) }
        ) { index ->
            val product = products[index % products.size]
            ProductCard(
                product = product,
                modifier = Modifier
                    .width(cardWidth)
                    .height(cardHeight) // Critical: Fixed height for each card
                    .pointerInput(Unit) { 
                        detectTapGestures(
                            onPress = {
                                isCardPressed = true
                                tryAwaitRelease() 
                                isCardPressed = false
                            }
                        )
                    }
            )
        }
    }
}

@Composable
fun VendScreen(products: List<UiProduct>, modifier: Modifier = Modifier) { // Changed to UiProduct
    val productCardHeight = 668.dp

    val productsForRow1: List<UiProduct> // Changed to UiProduct
    val productsForRow2: List<UiProduct> // Changed to UiProduct

    val midPoint = if (products.size >= 10) 5 else (products.size + 1) / 2
    productsForRow1 = products.take(midPoint)
    productsForRow2 = products.drop(midPoint).take(if (products.size >= 10) 5 else products.size - midPoint)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBannerAd(
            modifier = Modifier
                .fillMaxWidth()
                .height(530.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (productsForRow1.isNotEmpty()) {
            HorizontallyScrollingProductRow(
                products = productsForRow1,
                cardHeight = productCardHeight
            )
        }

        if (productsForRow2.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontallyScrollingProductRow(
                products = productsForRow2,
                cardHeight = productCardHeight
            )
        }

        if (products.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No products available.")
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TopBannerAd(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.peak_banner),
            contentDescription = "Advertisement Banner",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AgeVerificationDialog(
    productName: String,
    onDismiss: () -> Unit,
    onProceed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Age Verification Required")
        },
        text = {
            Text("This product ($productName) is age-restricted. You must be 21+ years of age. Please use the ID scanner on the machine to verify your age.")
        },
        confirmButton = {
            Button(
                onClick = {
                    onProceed()
                    onDismiss() // Dismiss after proceeding
                }
            ) {
                Text("Scan ID")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(product: UiProduct, modifier: Modifier = Modifier) { // Changed to UiProduct
    val buttonWidth = 247.dp * 0.8f // Calculate 20% less width
    var showAgeDialog by remember { mutableStateOf(false) }

    if (showAgeDialog) {
        AgeVerificationDialog(
            productName = product.name,
            onDismiss = { showAgeDialog = false },
            onProceed = {
                // TODO: Implement ID scanning logic
                println("ID scanning initiated for ${product.name}")
                // For now, after "Scan ID", we assume it passes and would proceed to payment.
                // In a real app, this would involve interaction with an ID scanner SDK.
                println("Proceeding to payment for ${product.name} after (simulated) ID scan.")
                 // TODO: Initiate Nayax/IDTech payment for age-restricted item
            }
        )
    }

    Card(
        modifier = modifier, // This modifier carries .width(cardWidth) and .height(cardHeight)
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Fills the fixed height of the Card
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // imageResId in UiProduct is Int, not Int?, so no null check needed
            Image(
                painter = painterResource(id = product.imageResId),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp), // Fixed height for image area
                contentScale = ContentScale.Fit
            )

            val nameFontSize = MaterialTheme.typography.titleMedium.fontSize * 1.9f // 5% reduction
            val descriptionFontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.9f // 5% reduction

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = nameFontSize
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                minLines = 2, // Ensure it always takes up space for 2 lines
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            if (product.description.isNotEmpty()) {
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = descriptionFontSize
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 3, 
                    minLines = 3, // Ensure it always takes up space for 3 lines
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            } else {
                 Text(
                    text = "", // Empty text
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = descriptionFontSize
                    ),
                    minLines = 3, // Occupy space for 3 lines
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                 )
            }

            Spacer(modifier = Modifier.weight(1f)) // This spacer pushes the following content to the bottom

            val reducedPriceFontSize = MaterialTheme.typography.titleMedium.fontSize * 1.5f

            Row(
                verticalAlignment = Alignment.Bottom, // Align Slot and Price/Button Column by their bottom edges
                horizontalArrangement = Arrangement.Center, // Center the [Slot] + [Price/Button Column] group
                // No explicit width for this Row, it will be centered by the parent Column
            ) {
                // SLOT NUMBER (Smaller Font)
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = Color.Black,
                    modifier = Modifier.padding(end = 12.dp) // Increased padding to move slot left
                ) {
                    Text(
                        text = product.id.removePrefix("p").padStart(2, '0'),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = reducedPriceFontSize // Slot number uses the base reduced size
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // COLUMN FOR PRICE (Larger Font) AND BUTTON
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(buttonWidth) // This column has the width of the button
                ) {
                    Text(
                        text = "$${"%.2f".format(product.price)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = reducedPriceFontSize * 1.1f // Price is 10% larger than reduced size
                        ),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth() // Price text fills width of this column and centers
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { 
                            if (product.isAgeRestricted) {
                                showAgeDialog = true
                            } else {
                                // TODO: Initiate Nayax/IDTech payment for non-age-restricted item
                                println("Proceeding directly to payment for non-age-restricted: ${product.name}")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(), // Button fills width of this column
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDB3D25),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "BUY",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1080, heightDp = 1920)
@Composable
fun VendScreenPreview() {
    val previewSampleProducts: List<UiProduct> = List(10) { i -> // Changed to UiProduct
        UiProduct( // Changed to UiProduct
            id = "p${i+1}", 
            name = "Sample Product ${i+1}", 
            description = if(i % 2 == 0) "Desc for product ${i+1} line1\nline2\nline3" else "Short desc ${i+1}", 
            price = 20.00 + i.toDouble(), 
            // imageUrl = "", // UiProduct doesn't have imageUrl
            imageResId = R.drawable.placeholder_product,
            isAgeRestricted = i % 3 == 0, // Make some products age restricted for preview
            stock = 10 // Added stock for UiProduct
        )
    }
    MyKioskAppTheme {
        VendScreen(products = previewSampleProducts)
    }
}

@Preview(showBackground = true)
@Composable
fun HorizontallyScrollingProductRowPreview() {
    val previewSampleProducts: List<UiProduct> = List(5) { i -> // Changed to UiProduct
        UiProduct( // Changed to UiProduct
            id = "p${i+1}", 
            name = "Scroll Product ${i+1}", 
            description = if(i % 2 == 0) "Desc for product ${i+1} line1\nline2\nline3" else "Short desc ${i+1}", 
            price = 20.00 + i.toDouble(), 
            // imageUrl = "", // UiProduct doesn't have imageUrl
            imageResId = R.drawable.placeholder_product,
            isAgeRestricted = i % 2 == 0, // Make some products age restricted for preview
            stock = 5 // Added stock for UiProduct
        )
    }
    MyKioskAppTheme {
        HorizontallyScrollingProductRow(products = previewSampleProducts)
    }
}

@Preview(showBackground = true, widthDp = 349, heightDp = 668)
@Composable
fun ProductCardPreview() {
    MyKioskAppTheme {
        ProductCard(
            product = UiProduct("p1", "Preview Product Name", "This is a detailed preview description...", 99.99, R.drawable.placeholder_product, isAgeRestricted = true, stock = 1), // Changed to UiProduct
            modifier = Modifier.width(349.dp).height(668.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 349, heightDp = 668)
@Composable
fun ProductCardAgeRestrictedPreview() { // New preview for age-restricted item
    MyKioskAppTheme {
        ProductCard(
            product = UiProduct("pAR", "Age Restricted Item", "Must be 21+ to purchase.", 25.99, R.drawable.placeholder_product, isAgeRestricted = true, stock = 2), // Changed to UiProduct
            modifier = Modifier.width(349.dp).height(668.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 349, heightDp = 668)
@Composable
fun ProductCardShortDescPreview() {
    MyKioskAppTheme {
        ProductCard(
            product = UiProduct("p2", "Short Name", "Short desc.", 19.99, R.drawable.placeholder_product, isAgeRestricted = false, stock = 3), // Changed to UiProduct
            modifier = Modifier.width(349.dp).height(668.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 349, heightDp = 668)
@Composable
fun ProductCardNoDescPreview() {
    MyKioskAppTheme {
        ProductCard(
            product = UiProduct("p3", "No Desc Product", "", 49.99, R.drawable.placeholder_product, isAgeRestricted = false, stock = 4), // Changed to UiProduct
            modifier = Modifier.width(349.dp).height(668.dp)
        )
    }
}
