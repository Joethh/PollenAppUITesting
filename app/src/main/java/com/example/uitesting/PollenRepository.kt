package com.example.uitesting

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.uitesting.ui.elements.AllergenItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class DailyForecast(
    val dayOfWeek: String,
    val allergens: List<AllergenItem>
)

class PollenRepository {

    private val api = RetroFitInstance().apiInterface

    private data class AllergenConfig(
        val name: String,
        val values: List<Float?>
    )

    //A forecast of the MAX forecasted levels each day.
    suspend fun getFourDayPollenForecast(): List<DailyForecast> {
        return try {
            val response = api.getHourlyPollen()
            if (response.isSuccessful && response.body() != null) {
                val hourlyData = response.body()!!.hourly

                val allergens = listOf(
                    AllergenConfig("Alder", hourlyData.alderPollen),
                    AllergenConfig("Birch", hourlyData.birchPollen),
                    AllergenConfig("Grass", hourlyData.grassPollen),
                    AllergenConfig("Mugwort", hourlyData.mugwortPollen),
                    AllergenConfig("Olive", hourlyData.olivePollen),
                    AllergenConfig("Ragweed", hourlyData.ragweedPollen)
                )

                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val dailyData = hourlyData.time.mapIndexed { index, timeString ->
                    // Open-Meteo typically uses local date time format in the hourly array
                    val dateTime = LocalDateTime.parse(timeString, formatter)
                    val day = dateTime.toLocalDate()
                    val allergenValues = allergens.map { allergen ->
                        allergen.values.getOrElse(index) { 0f } ?: 0f
                    }
                    day to allergenValues
                }.groupBy({ it.first }) { it.second }

                dailyData.entries.take(4).map { (date, dailyReadings) ->
                    val dayOfWeek = date.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() }

                    val maxAllergenValues = allergens.mapIndexed { allergenIndex, _ ->
                        dailyReadings.maxOfOrNull { it[allergenIndex] } ?: 0f
                    }

                    val allergenItems = allergens.mapIndexed { index, allergenConfig ->
                        val score = maxAllergenValues[index]
                        AllergenItem(
                            name = allergenConfig.name,
                            score = score,
                            color = pollenColor(score),
                            icon = pollenIcon(score)
                        )
                    }
                    DailyForecast(dayOfWeek, allergenItems)
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    //Get the pollen levels for the current hour.
    suspend fun getCurrentPollenLevels(): List<AllergenItem> {
        return try {
            val response = api.getHourlyPollen()
            if (response.isSuccessful && response.body() != null) {
                val hourlyData = response.body()!!.hourly
                
                // Find the index for the current hour
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val now = LocalDateTime.now()
                var currentIndex = 0
                for (i in hourlyData.time.indices) {
                    val time = LocalDateTime.parse(hourlyData.time[i], formatter)
                    if (time.isAfter(now)) break
                    currentIndex = i
                }

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
                        .getOrElse(currentIndex) { 0f } ?: 0f

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
