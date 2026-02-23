package com.example.uitesting

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("https://air-quality-api.open-meteo.com/v1/air-quality?latitude=52.52&longitude=13.41&hourly=alder_pollen,birch_pollen,grass_pollen,mugwort_pollen,ragweed_pollen,olive_pollen,pm2_5&current=european_aqi&timezone=auto")
    suspend fun getHourlyPollen(): Response<OpenMeteoResponse>
}