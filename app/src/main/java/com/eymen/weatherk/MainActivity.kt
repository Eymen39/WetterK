package com.eymen.weatherk

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.eymen.weatherk.ui.theme.WeatherKTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.CoroutineContext

val locations = ArrayList<Orte>(listOf(
    Orte("Berlin", 52.52f, 13.405f),
    Orte("Paris", 48.8566f, 2.3522f),
    Orte("New York", 40.7128f, -74.0060f)
))
lateinit var dataBase: OrteDataBase
class MainActivity : AppCompatActivity() {

    private lateinit var orteDAO: OrteDAO
    private lateinit var usedPlace:Orte

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                WeatherKTheme(){
                    Greeting("Eymen")
                    LocationDrawer(locations) { location-> Log.d("info", location.name)}


                }
        }


        dataBase=OrteDataBase.getDataBase(this)
        orteDAO=dataBase.OrteDAO()

        lifecycleScope.launch {

            orteDAO.insert(OrteEntity(name=locations[1].name, latitude = locations[1].latitude, longitude = locations[1].longitude))

        }

    }
}



    @Composable
fun Greeting(name:String) {
        Column(Modifier
            .fillMaxSize()
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )

        {
            Text(
                text = "Hello $name",
                modifier = Modifier.background(Color.Red)


            )
            Text(
                text = "here",
                color = Color.Black
            )

        }
    }

@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    WeatherKTheme(){
        Greeting("Eymen")
        LocationDrawer(locations) { location-> }
    }

}

/*@Composable
fun SavedLocationDrawer()
{
    val drawerState= rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope= rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { Text("hey") }
        }

    ) {

        Scaffold(
            floatingActionButton= {
                ExtendedFloatingActionButton(
                    text = { Text("Show Drawer")},
                    icon={ Icon(Icons.Filled.Add,contentDescription="") },
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if(isClosed)open()else close()
                            }
                        }
                    })



        }
        )


    }
}

*/
@Composable
fun LocationDrawer(
    savedOrte:ArrayList<Orte>,
    onLocationSelected: (Orte) -> Unit
) {
    val context = LocalContext.current
    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedOrt by remember {mutableStateOf<Orte?>(null)}
LaunchedEffect(selectedOrt) {
    selectedOrt?.let{
        ort->
        Log.d("LocationDrawer", "Neuer Ort ist: ${ort.name}")
    }
}
    var showDialog by remember { mutableStateOf(false) }
    var newOrteName by remember { mutableStateOf("") }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Row {Text("Wähle einen Ort", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))


                    IconButton(onClick = {showDialog=true}, modifier = Modifier.align(Alignment.Bottom)){
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                    }






                savedOrte.forEach { location ->
                    ListItem(
                        headlineContent = { Text(location.name) },
                        supportingContent = { Text("Lat: ${location.latitude}, Lon: ${location.longitude}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLocationSelected(location)
                                coroutineScope.launch { drawerState.close() }
                            }

                    )
                    HorizontalDivider()
                }
            }
        },
        drawerState = drawerState
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { coroutineScope.launch { drawerState.open() } }) {
                Text("Menü öffnen")
            }
        }
    }
    if(showDialog){
        AlertDialog(onDismissRequest = {showDialog=false},
            title ={Text("Ort hinzufügen")},
            text={
                TextField(value = newOrteName,
                    onValueChange = {newOrteName =it},
                    label={ Text("Ortsname") }
                )

            },
            confirmButton = { Button(onClick = {
                if(newOrteName.isNotEmpty()){
                    insertinCache(newOrteName, locations,context)
                    insertOrt(newOrteName,context)
                }else{
                    Toast.makeText(context,"Geben sie einen validen Ort ein",Toast.LENGTH_SHORT).show()
                }

            })
            { Text("Speichern")}
            }
            )
    }
}
suspend fun getCoordinates(name:String, context: Context):Orte?{
    return withContext(Dispatchers.IO){

        val geocoder= Geocoder(context, Locale.getDefault())
        val addressList= geocoder.getFromLocationName(name,1)

        val address= addressList?.getOrNull(0)
        if(address!=null){
            val longitude= address.longitude.toFloat()
            val latitude= address.latitude.toFloat()
            Orte(name, longitude =longitude.toFloat(), latitude=latitude.toFloat() )
        }else{
            null
        }

    }

}
fun insertOrt(name:String,context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val coordinates = getCoordinates(name, context)
        if(coordinates!=null)
        dataBase.OrteDAO().insert(OrteEntity(0,coordinates.name,coordinates.latitude,coordinates.longitude))
    }
}
fun insertinCache(name:String,savedOrte:ArrayList<Orte>,context: Context){
    CoroutineScope(Dispatchers.Default).launch {
        val coordinates= getCoordinates(name,context)
        if(coordinates!=null){
            savedOrte.add(coordinates)

        }
    }

}
