package com.example.uitesting

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.uitesting.ui.elements.AllergenItem

class PollenRepository {

    private val api = RetroFitInstance().apiInterface

    private data class AllergenConfig(
        val name: String,
        val values: List<Float?>
    )

    suspend fun getCurrentPollenLevels(): List<AllergenItem> {
        return try {
            val response = api.getHourlyPollen()
            if (response.isSuccessful && response.body() != null) {
                val hourlyData = response.body()!!.hourly
                val latestTimeIndex = hourlyData.time.size - 1

                val allergens = listOf(
                    AllergenConfig("Alder", hourlyData.alderPollen),
                    AllergenConfig("Birch", hourlyData.birchPollen),
                    AllergenConfig("Grass", hourlyData.grassPollen),
                    AllergenConfig("Mugwort", hourlyData.mugwortPollen),
                    AllergenConfig("Olive", hourlyData.olivePollen),
                    AllergenConfig("Ragweed", hourlyData.ragweedPollen)
                )

                allergens.map { allergen ->
                    val score = allergen.values
                        .getOrElse(latestTimeIndex) { 0f } ?: 0f

                    AllergenItem(
                        name = allergen.name,
                        score = score,
                        color = pollenColor(score),
                        icon = pollenIcon(score)
                    )
                }
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun pollenColor(score: Float): Color {
        val clamped = score.coerceIn(0f, 10f)
        val fraction = clamped / 10f

        val red = (255 * fraction).toInt()
        val green = (255 * (1f - fraction)).toInt()

        return Color(red, green, 0)
    }


    private fun pollenIcon(score: Float): ImageVector {
        return when {
            score < 3f -> Icons.Outlined.SentimentSatisfiedAlt
            score < 7f -> Icons.Outlined.SentimentNeutral
            else -> Icons.Outlined.SentimentVeryDissatisfied
        }
    }




}
