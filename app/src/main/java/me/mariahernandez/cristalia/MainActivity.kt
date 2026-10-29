package me.mariahernandez.cristalia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.mariahernandez.cristalia.ui.theme.CRISTALIATheme

import android.content.Intent
import android.widget.Toast



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

