package me.mariahernandez.cristalia.inicio

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/* Guarda el reproductor y el estado de la pantalla de inicio.
    Sobrevive a la rotación del celular, así que el video no se reinicia.

*/

//                     parametro                   clase padre
class SplashViewModel(app: Application) : AndroidViewModel(app) {

    // ¿Ya terminó el video? (1)
    var videoEnded by mutableStateOf(false)
        private set

    // ¿Ya toca mostrar el botón? (1)
    var showButton by mutableStateOf(false)
        private set

    // Reproductor (se crea una sola vez mientras viva el ViewModel) (2)
    val player: ExoPlayer = ExoPlayer.Builder(app).build().apply {

        val uri = "android.resource://${app.packageName}/raw/prueba".toUri()       // Localiza el vídeo
        setMediaItem(MediaItem.fromUri(uri))                                       // Le entrega ese vídeo al reproductor
        repeatMode = Player.REPEAT_MODE_OFF                                        // Evita que el vídeo se repita
        volume = 0f                                                                // Quita sonido
        prepare()                                                                  // Prepara el video
        playWhenReady = true                                                       // Cuando está listo, arranca
        addListener(object : Player.Listener {                            // Deja a un vigilante atento a los cambios de estado
            override fun onPlaybackStateChanged(state: Int) {                      // El reproductor la llama cada vez que cambia de estado
                if (state == Player.STATE_ENDED) onVideoEnded()                    // Cuando el video termina, ejecuta la función que mostrará el botón después del título
            }
        })
    }

    // Secuencia: video termina -> título -> (900 ms) -> botón
    private fun onVideoEnded() {
        videoEnded = true
        viewModelScope.launch {                               // Permite hacer esperar una tarea sin frenar la app
            delay(900.milliseconds)                 // delay evita que en los 900 ms se congele la pantalla y pueda mostrarse el titulo de Cristalia
            showButton = true
        }
    }

    // Se llama cuando el ViewModel se destruye de verdad (no al rotar)
    override fun onCleared() {
        player.release()                                                 //   Libera el reproductor
    }
}



/*
El ViewModel guarda datos y lógica.
Pero lo que se ve en pantalla lo dibuja el PlayerView, una vista que Android destruye y recrea al rotar.
Ese problema de dibujo está fuera del alcance de un ViewModel.
 */

