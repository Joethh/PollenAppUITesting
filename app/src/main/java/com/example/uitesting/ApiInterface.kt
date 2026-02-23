package com.example.uitesting

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("v1/air-quality?hourly=alder_pollen,birch_pollen,grass_pollen,mugwort_pollen,ragweed_pollen,olive_pollen,pm2_5&current=european_aqi&timezone=auto")
    suspend fun getHourlyPollen(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<OpenMeteoResponse>
}
