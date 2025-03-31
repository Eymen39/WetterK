package com.eymen.weatherk

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Orte")
data class OrteEntity(
    @PrimaryKey val name:String,
    val countryname:String,
    val latitude:Float,
    val longitude:Float) {
    fun getOrte(): FoundPlace {


        return FoundPlace(name,countryname, longitude,latitude)
    }
}