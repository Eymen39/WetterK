package com.eymen.weatherk

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ViewModelMain(private val orteDAO:OrteDAO,context:Context): ViewModel()
{
    private val _orte= MutableStateFlow<List<OrteEntity>>(emptyList())
    val orte :StateFlow<List<OrteEntity>> = _orte.asStateFlow()


    init {
        viewModelScope.launch {

            orteDAO.getAllOrte().collect{ orteDB->
                _orte.value=orteDB
            }

        }
    }


    fun addOrt(name:String,context: Context ) {
        viewModelScope.launch {
            val coordinates = getCoordinates(name, context)
            if (coordinates != null)
                orteDAO.insert(
                    OrteEntity(
                        0,
                        coordinates.name,
                        coordinates.latitude,
                        coordinates.longitude
                    )
                )

        }
    }
    fun getCoordinates(name:String, context: Context):Orte?{


            val geocoder= Geocoder(context, Locale.getDefault())
            val addressList= geocoder.getFromLocationName(name,1)

            val address= addressList?.getOrNull(0)
            if(address!=null){
                val longitude= address.longitude.toFloat()
                val latitude= address.latitude.toFloat()
            return    Orte(name, longitude =longitude.toFloat(), latitude=latitude.toFloat() )
            }else{
               return null
            }

        }




}