package com.example.uitesting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uitesting.ui.elements.AllergenBreakdownCard
import com.example.uitesting.ui.elements.AllergenItem
import com.example.uitesting.ui.elements.DataDisplayCard
import com.example.uitesting.ui.elements.Forecast
import com.example.uitesting.ui.elements.ForecastCard
import com.example.uitesting.ui.elements.Header
import com.example.uitesting.ui.elements.SensitivityAlertCard
import com.example.uitesting.ui.elements.UserSensitivityInputCard

@Composable
fun MainScreen(
    forecasts: List<Forecast>,
    allergens: List<AllergenItem>,
    location: String,
    particulates: Float,
    modifier: Modifier = Modifier
) {
    Scaffold { padding ->
        Box {
            Header(location, particulates)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = padding
            ) {
                item { Spacer(Modifier.height(220.dp)) }

                item {
                    SensitivityAlertCard(
                        rating = "High",
                        description = "Sensitivity levels are high today. Take precautions if you're spending time outdoors.",
                        modifier = modifier
                    )
                }

                item { AllergenBreakdownCard(allergens, modifier) }

                item {
                    UserSensitivityInputCard(
                        modifier = modifier,
                        onNavigateToFeedback = { /* Handle navigation */ }
                    ) 
                }

                item { ForecastCard(forecasts, modifier) }

                item { DataDisplayCard() }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MainScreenPreview() {
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
        MainScreen(forecasts, allergens, "Swansea, UK", 2.7f, Modifier.padding(16.dp))
    }
}
