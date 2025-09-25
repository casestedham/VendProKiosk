package com.example.vendprokiosk

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.vendprokiosk.ui.theme.MyKioskAppTheme

@Composable
fun ScreensaverView(modifier: Modifier = Modifier) { // Removed 'ads' parameter
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.joeyscreensaver), // Static image
            contentDescription = "Screensaver", // Generic content description
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Or your preferred ContentScale
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScreensaverViewPreview() {
    MyKioskAppTheme {
        Scaffold {
            ScreensaverView(
                modifier = Modifier.padding(it)
            )
        }
    }
}

// Preview for the updated ScreensaverView (previously empty state preview)
@Preview(name = "Screensaver Static Preview", showBackground = true, showSystemUi = true)
@Composable
fun ScreensaverViewStaticPreview() {
    MyKioskAppTheme {
        Scaffold {
            ScreensaverView(
                modifier = Modifier.padding(it)
            )
        }
    }
}
