package com.eymen.weatherk

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val orteDAO: OrteDAO,private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ViewModelMain::class.java)) {
            ViewModelMain(orteDAO, context) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}