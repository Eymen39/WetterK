package com.eymen.weatherk

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eymen.weatherk.ui.theme.WeatherKTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.CoroutineContext

lateinit var dataBase: OrteDataBase
class MainActivity : AppCompatActivity() {

    private lateinit var orteDAO: OrteDAO
    private lateinit var usedPlace:FoundPlace
    private lateinit var viewModel:ViewModelMain
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name="LastLocation")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                WeatherKTheme(){
                    ShowWeather(viewModel)
                    LocationDrawer(savedOrteFlow = viewModel.orte, viewModelMain = viewModel) { location-> Log.d("info", location.placeName)}



                }
        }


        dataBase=OrteDataBase.getDataBase(this)
        orteDAO=dataBase.OrteDAO()
        viewModel= ViewModelProvider(this,MainViewModelFactory(orteDAO,this))
            .get(ViewModelMain::class.java)

    }

    suspend fun saveLocation(context:Context,location:FoundPlace){
        val dataStore=context.dataStore
        val location_key = stringPreferencesKey("Last")

        dataStore.edit { prefrences-> prefrences[location_key]=location.placeName+";"+location.countryName+";"+location.lat+";"+location.lon }
    }
    fun getLastLocation(context: Context): Flow<String> {
        val dataStore=context.dataStore
        val location_key= stringPreferencesKey("Last")
        return dataStore.data.map { preferences-> preferences[location_key]?: "Noch keinen Ort hinzugefügt" }
    }
}



    @Composable
fun ShowWeather(viewModelMain: ViewModelMain){
val foundPlace by viewModelMain.selectedPlace.collectAsState()

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ){
            Column (horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center){
                Text(foundPlace.placeName)
                Text(foundPlace.countryName)
                Text(foundPlace.lat.toString())
                Text(foundPlace.lon.toString()) }
        }




}

@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    WeatherKTheme(){
      //  LocationDrawer() { }
    }

}

@SuppressLint("SuspiciousIndentation")
@Composable
fun LocationDrawer(
    savedOrteFlow:StateFlow<List<OrteEntity>>,viewModelMain: ViewModelMain,
    onLocationSelected: (FoundPlace) -> Unit //damit akann ich für jeden aufruf dieser Funktion
) {
    val context = LocalContext.current
    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val savedOrte by savedOrteFlow.collectAsState()
    val newLocationList by viewModelMain.newFoundPlace.collectAsState()

    var selectedOrt by remember {mutableStateOf<FoundPlace?>(null)}
    LaunchedEffect(selectedOrt) {
    selectedOrt?.let{
        ort->
    }
}
    var showDialog by remember { mutableStateOf(false) }
    var showNewLocationInput by remember { mutableStateOf(false) }
    var newLocationName by remember { mutableStateOf("") }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column {
                    Row {
                        Text(
                            "Wähle einen Ort",
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold
                        )



                        IconButton(onClick = {
                            showNewLocationInput = true

                        }, modifier = Modifier.align(Alignment.Bottom).weight(1f)) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                        if (showNewLocationInput) {
                            OutlinedTextField(value = newLocationName,
                                onValueChange = { newLocationName = it },
                                label = { Text("Geben sie einen neuen Ort ein") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { IconButton(onClick = {
                                    if(newLocationName.isNotEmpty())
                                        viewModelMain.getCoordinates(newLocationName,context)
                                    showDialog=true
                                }){
                                    Icon(Icons.Default.Check, contentDescription = "Hinzufügen")
                                }
                                })
                        }

                }




                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(savedOrte){location->
                        ListItem(
                            headlineContent = { Text(location.name) },
                            supportingContent = { Text("Lat: ${location.latitude}, Lon: ${location.longitude}") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLocationSelected(location.getOrte())
                                    coroutineScope.launch { drawerState.close() }
                                    viewModelMain.selectPlace(location.getOrte())

                                })
                        HorizontalDivider()

                    }
                }



                }
        },
        drawerState = drawerState
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }){
                Icon(Icons.Default.Menu,null)
            }
        }
    }
    if(showDialog){
        AlertDialog(onDismissRequest = {showDialog=false},
            title ={Text("Ort auswählen")},
            text={

                LazyColumn{items(newLocationList){
                    item ->
                    ListItem(headlineContent = { Text(text = item.placeName) },
                        supportingContent = {Text(text = item.countryName)},
                        modifier = Modifier.clickable { viewModelMain.addOrt(newLocationList[newLocationList.indexOf(item)] )
                        showDialog=false
                        })
                } }

            },
            confirmButton = {

            }

            )
    }
}



