package com.example.uitesting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.uitesting.ui.elements.AllergenItem
import com.example.uitesting.ui.elements.Forecast

class MainActivity : ComponentActivity() {

    // Mock data for allergens and forecasts
    private val allergens = listOf(
        AllergenItem("Grass", 8.2f, Color(0xFF4CAF50), Icons.Outlined.Warning),
        AllergenItem("Tree", 6.5f, Color(0xFF2E7D32), Icons.Outlined.Warning),
        AllergenItem("Weed", 4.3f, Color(0xFF8BC34A), Icons.Outlined.Warning),
        AllergenItem("Ragweed", 3.1f, Color(0xFFFBC02D), Icons.Outlined.Warning)
    )

    private val forecasts = listOf(
        Forecast("Fri", "June", 18, 6.8f, "High", Icons.Outlined.Warning),
        Forecast("Sat", "June", 19, 6.6f, "High", Icons.Outlined.Warning),
        Forecast("Sun", "June", 20, 7.8f, "High", Icons.Outlined.Warning),
        Forecast("Mon", "June", 21, 5.6f, "Medium", Icons.Outlined.Warning),
        Forecast("Tue", "June", 22, 2.0f, "Low", Icons.Outlined.Warning),
        Forecast("Wed", "June", 23, 3.4f, "Low", Icons.Outlined.Warning)
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val locationManager = LocationManager(this)

        setContent {
            var locationName by remember { mutableStateOf("Loading...") }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions.values.all { it }
                if (granted) {
                    locationName = "Refreshing..."
                }
            }

            //Check for location permission and req if needed
            LaunchedEffect(Unit) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            LaunchedEffect(locationName) {
                if (locationName == "Loading..." || locationName == "Refreshing...") {
                    val latLon = locationManager.getLatLon()
                    if (latLon != null) {
                        locationName = locationManager.getLocationString(latLon)
                    } else {
                        locationName = "Permission Denied / Unknown"
                    }
                }
            }

            AppTheme(dynamicColor = false) {
                MainScreen(forecasts, allergens, locationName, Modifier.padding(16.dp))
            }
        }
    }
}
