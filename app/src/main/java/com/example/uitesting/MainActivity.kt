package com.example.uitesting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            val allergens = listOf(
                AllergenItem(
                    "Grass",
                    8.2f,
                    Color(0xFF4CAF50),
                    Icons.Outlined.Warning
                ),
                AllergenItem(
                    "Tree",
                    6.5f,
                    Color(0xFF2E7D32),
                    Icons.Outlined.Warning
                ),
                AllergenItem(
                    "Weed",
                    4.3f,
                    Color(0xFF8BC34A),
                    Icons.Outlined.Warning
                ),
                AllergenItem(
                    "Ragweed",
                    3.1f,
                    Color(0xFFFBC02D),
                    Icons.Outlined.Warning
                )
            )

            val forecasts = listOf(
                Forecast(
                    "Fri",
                    "June",
                    18,
                    6.8f,
                    "High",
                    Icons.Outlined.Warning
                ),
                Forecast(
                    "Sat",
                    "June",
                    19,
                    6.6f,
                    "High",
                    Icons.Outlined.Warning
                ),
                Forecast(
                    "Sun",
                    "June",
                    20,
                    7.8f,
                    "High",
                    Icons.Outlined.Warning
                ),
                Forecast(
                    "Mon",
                    "June",
                    21,
                    5.6f,
                    "Medium",
                    Icons.Outlined.Warning
                ),
                Forecast(
                    "Tue",
                    "June",
                    22,
                    2.0f,
                    "Low",
                    Icons.Outlined.Warning
                ),
                Forecast(
                    "Wed",
                    "June",
                    23,
                    3.4f,
                    "Low",
                    Icons.Outlined.Warning
                )
            )

            AppTheme(dynamicColor = false) {
                MainScreen(forecasts, allergens, Modifier.padding(16.dp))
            }
        }
    }
}