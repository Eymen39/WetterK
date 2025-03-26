package com.eymen.weatherk

data class WeatherData(val name:String, val main:Main,val clouds:Clouds){


    data class Main(val temp:Float,val humidity:Int)

    data class Clouds(val all:Int)



}
