package me.mariahernandez.cristalia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import me.mariahernandez.cristalia.inicio.CristaliaSplashScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            // Se agrega de ultimo
            CristaliaSplashScreen(

                // Lambda que entra como parámetro en CristaliaSplashScreen      (Creo que aqui puedes empezar y unir el inicio con el menu sebas)
                onEnter = {
                Toast.makeText(this, "¡Entraste!", Toast.LENGTH_SHORT).show()
                }

            )
        }
    }
}

