package me.mariahernandez.cristalia

import androidx.annotation.OptIn // (1)
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color             // Colores
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi             // (1)
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

// Colores
private val Cyan = Color(0xFF00F0FF)
private val BgDark = Color(0xFF10141C)



@OptIn(UnstableApi::class)       // (1)
@Composable

//                          Lambda como párametro
fun CristaliaSplashScreen(onEnter: () -> Unit) {

    // Contexto de la aplicación
    val context = LocalContext.current

    // Estados
    var videoEnded by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Reproductor
    val player = remember {

        // A partir del contexto lo construye
        ExoPlayer.Builder(context).build().apply {

            val uri = Uri.parse("android.resource://${context.packageName}/raw/inicio") // Localiza el vídeo
            setMediaItem(MediaItem.fromUri(uri))                                                   // Indica qué video se va a reproducir
            repeatMode = Player.REPEAT_MODE_OFF                                                   // Evita que se repita el vídeo
            volume = 0f                                                                           // Sin sonido
            prepare()                                                                             // Prepara el video
            playWhenReady = true                                                                  // Cuando ya está preparado indica que comience a reproducirse

            // Detecta cuando termina el vídeo
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) videoEnded = true
                }
            })
        }
    }

    // Limpieza cuando la pantalla desaparace
    DisposableEffect(Unit) { onDispose { player.release() } }

    // Secuencia: video -> título -> botón
    LaunchedEffect(videoEnded) {
        if (videoEnded) {
            delay(900)          // espera a que el título termine de aparecer
            showButton = true
        }
    }

    // Aparición del título
    val titleAlpha by animateFloatAsState(
        targetValue = if (videoEnded) 1f else 0f,                    // 1f -> Visible         ;    0f -> Invisible
        animationSpec = tween(700), label = "title"
    )

    // Apararición del botón
    val buttonAlpha by animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        animationSpec = tween(600), label = "button"
    )

    // Visual
    Box(

        modifier = Modifier
            .fillMaxSize()                   // Ocupa toda la pantalla
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x66401060), Color.Transparent),
                    center = Offset(900f, 0f), radius = 900f
                )
            )

            .background(BgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()                     // Ocupa toda la caja
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))             // Espacio vacío flexible

            // Título
            Text(
                text = "Cristalia",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(titleAlpha)
            )

            Spacer(Modifier.height(48.dp))

            // Video dentro de una tarjeta
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x1A00F0FF),
                border = BorderStroke(1.dp, Color(0x3300F0FF)),
                modifier = Modifier.size(200.dp)
            ) {
                AndroidView(

                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp)),

                    // Crea la vista
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            this.player = player
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            setShutterBackgroundColor(android.graphics.Color.BLACK)
                        }
                    }
                )
            }

            Spacer(Modifier.height(72.dp))

            // Botón Entrar
            Button(
                onClick = onEnter,
                enabled = showButton,   // Sólo se muestra cuando showButton es true
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Cyan,
                    contentColor = Color.Black,
                    disabledContainerColor = Cyan,
                    disabledContentColor = Color.Black
                ),
                modifier = Modifier
                    .alpha(buttonAlpha)            // la animación de opacidad del botón.
                    .fillMaxWidth(0.7f)    // Ocupa el 70% del ancho
                    .height(48.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Cyan,
                        spotColor = Cyan
                    )
            ) {
                Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
}