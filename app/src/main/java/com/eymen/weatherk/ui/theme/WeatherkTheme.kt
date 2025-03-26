package com.eymen.weatherk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // Beispiel Primärfarbe (Lila)
    secondary = Color(0xFF03DAC6), // Beispiel Sekundärfarbe (Türkis)
    background = Color(0xFFffffff), // Weiß als Hintergrund
    surface = Color(0xFFFFFFFF), // Weiß als Oberflächenfarbe
    onPrimary = Color.White, // Farbe des Textes auf der Primärfarbe
    onSecondary = Color.Black, // Farbe des Textes auf der Sekundärfarbe
    onBackground = Color.Black, // Farbe des Textes auf dem Hintergrund
    onSurface = Color.Black, // Farbe des Textes auf der Oberfläche
    // Weitere Farben kannst du nach Belieben hinzufügen
)



val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), // Beispiel Primärfarbe für Dark Theme (helles Lila)
    secondary = Color(0xFF03DAC6), // Sekundärfarbe bleibt gleich
    background = Color(0xFF121212), // Dunkler Hintergrund
    surface = Color(0xFF121212), // Dunkle Oberfläche
    onPrimary = Color.Black, // Textfarbe auf Primärfarbe (Schwarz für besseren Kontrast)
    onSecondary = Color.Black, // Textfarbe auf Sekundärfarbe
    onBackground = Color.White, // Textfarbe auf Hintergrund
    onSurface = Color.White, // Textfarbe auf Oberfläche
)
@Composable
fun WeatherKTheme(content:@Composable () -> Unit){
    MaterialTheme (){
        content()
    }
}