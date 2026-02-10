package com.example.uitesting

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume

class MainActivity : ComponentActivity() {

    private lateinit var locationClient: FusedLocationProviderClient

    // Mock data for allergens and forecasts
    val allergens = listOf(
        AllergenItem("Grass", 8.2f, Color(0xFF4CAF50), Icons.Outlined.Warning),
        AllergenItem("Tree", 6.5f, Color(0xFF2E7D32), Icons.Outlined.Warning),
        AllergenItem("Weed", 4.3f, Color(0xFF8BC34A), Icons.Outlined.Warning),
        AllergenItem("Ragweed", 3.1f, Color(0xFFFBC02D), Icons.Outlined.Warning)
    )

    val forecasts = listOf(
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

        locationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            // State to hold the location string
            var locationName by remember { mutableStateOf("Loading...") }
            var latLon by remember { mutableStateOf<List<Double>?>(null) }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions.values.all { it }
                if (granted) {
                    // Trigger a re-fetch if permissions are granted
                    locationName = "Refreshing..."
                }
            }

            LaunchedEffect(Unit) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    locationName = "Loading..."
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            // Fetch location and address when the app starts
            LaunchedEffect(locationName) {
                if (locationName == "Loading..." || locationName == "Refreshing...") {
                    val coords = getLatLon()
                    if (coords != null) {
                        latLon = coords
                        locationName = getLocationString(coords)
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

    private suspend fun getLocationString(latLon: List<Double>): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLon[0], latLon[1], 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    "${address.subAdminArea}, ${address.countryCode}"
                } else "Unknown"
            } catch (e: IOException) { "Error" }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLatLon(): List<Double>? = suspendCancellableCoroutine { cont ->
        val hasPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        val cts = CancellationTokenSource()
        locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(listOf(location.latitude, location.longitude))
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener { cont.resume(null) }

        cont.invokeOnCancellation { cts.cancel() }
    }
}