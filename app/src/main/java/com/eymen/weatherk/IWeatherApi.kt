package com.eymen.weatherk

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherApi {


    @GET("weather")
    fun getWeatherData(
        @Query("lat")latitude:Float,
        @Query("lon")longitude:Float,
        @Query("appid")apiKey:String
    ): Response<WeatherData>


}