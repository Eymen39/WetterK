package com.eymen.weatherk

import android.content.Context
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
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

class ViewModelMain(private val orteDAO:OrteDAO): ViewModel()
{
    private val _orte= MutableStateFlow<List<OrteEntity>>(emptyList())
    val orte :StateFlow<List<OrteEntity>> = _orte.asStateFlow()

    private val _newFoundPlaces= MutableStateFlow<List<FoundPlace>>(emptyList())
    val newFoundPlace :StateFlow<List<FoundPlace>> = _newFoundPlaces.asStateFlow()





    init {
        viewModelScope.launch {

            orteDAO.getAllOrte().collect{ orteDB->
                _orte.value=orteDB
            }

        }
    }


    fun addOrt(newPlace:FoundPlace ) {
        viewModelScope.launch {
            if (newPlace != null)
                orteDAO.insert(
                    OrteEntity(
                        newPlace.placeName,
                        newPlace.countryName,
                        newPlace.lat,
                        newPlace.lon
                    )
                )

        }
    }
    fun getCoordinates(name:String, context: Context){


            val geocoder= Geocoder(context, Locale.getDefault())
            val addressList= geocoder.getFromLocationName(name,5)

            //val address= addressList?.getOrNull(0)
            if(!addressList.isNullOrEmpty()){
                for (address in addressList){
                    val placeForList=FoundPlace(address.featureName,address.countryName,address.longitude.toFloat(), address.latitude.toFloat())
                    _newFoundPlaces.value += placeForList
                }
                //val longitude= address.longitude.toFloat()
                //val latitude= address.latitude.toFloat()
               //Orte(name, longitude =longitude.toFloat(), latitude=latitude.toFloat() )
            }

        }




}