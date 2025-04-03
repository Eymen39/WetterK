package com.eymen.weatherk

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [OrteEntity::class], version = 3, exportSchema = true)
abstract class OrteDataBase :RoomDatabase(){
    abstract fun OrteDAO(): OrteDAO

    companion object {
        @Volatile
        private var INSTANCE: OrteDataBase? = null


        fun getDataBase(context: Context): OrteDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrteDataBase::class.java,
                    "OrteDataBase"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }


        }
    }
}