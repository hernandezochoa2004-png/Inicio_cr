package me.mariahernandez.cristalia


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

/**
 * Guarda el reproductor y el estado de la pantalla de inicio.
 * Sobrevive a la rotación del celular, así que el video no se reinicia.
 */
class SplashViewModel(app: Application) : AndroidViewModel(app) {

    // ¿Ya terminó el video?
    var videoEnded by mutableStateOf(false)
        private set

    // ¿Ya toca mostrar el botón?
    var showButton by mutableStateOf(false)
        private set

    // Reproductor (se crea una sola vez mientras viva el ViewModel)
    val player: ExoPlayer = ExoPlayer.Builder(app).build().apply {
        val uri = "android.resource://${app.packageName}/raw/prueba".toUri()
        setMediaItem(MediaItem.fromUri(uri))
        repeatMode = Player.REPEAT_MODE_OFF
        volume = 0f
        prepare()
        playWhenReady = true
        addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) onVideoEnded()
            }
        })
    }

    // Secuencia: video termina -> título -> (900 ms) -> botón
    private fun onVideoEnded() {
        videoEnded = true
        viewModelScope.launch {
            delay(900.milliseconds)
            showButton = true
        }
    }

    // Se llama cuando el ViewModel se destruye de verdad (no al rotar)
    override fun onCleared() {
        player.release()
    }
}