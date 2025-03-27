package com.eymen.weatherk

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Orte")
class OrteEntity(
    @PrimaryKey(autoGenerate = true)val id: Long=0,
    val name:String,
    val latitude:Float,
    val longitude:Float) {
    fun getOrte(): Orte {


        return Orte(name, longitude,latitude)
    }
}