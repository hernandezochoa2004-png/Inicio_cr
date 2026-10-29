package me.mariahernandez.cristalia


import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color              // (1)
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView


// Colores (1)
private val Cyan = Color(0xFF00F0FF)
private val BgDark = Color(0xFF10141C)

@OptIn(UnstableApi::class)
@Composable



//                            lambda1                                 lambda2                         entran como parámetros
fun CristaliaSplashScreen(  onEnter: () -> Unit,         viewModel: SplashViewModel = viewModel() ) {

    // Estado y reproductor vienen del ViewModel (sobreviven a la rotación)
    val player = viewModel.player
    val videoEnded = viewModel.videoEnded
    val showButton = viewModel.showButton

    // ¿Está el celular en horizontal?
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Pausa el video al salir de la app y lo continúa al volver
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> player.pause()
                Lifecycle.Event.ON_START -> if (!viewModel.videoEnded) player.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Animaciones de opacidad: 0f = invisible, 1f = visible      TitleAlpha y buttonAlpha se utilizaran más abajo
    val titleAlpha by animateFloatAsState(
        targetValue = if (videoEnded) 1f else 0f,
        animationSpec = tween(700), label = "title"
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        animationSpec = tween(600), label = "button"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark) // 1.º: fondo oscuro
            .background(        // 2.º: brillo morado encima
                Brush.radialGradient(
                    colors = listOf(Color(0x66401060), Color.Transparent),
                    center = Offset(900f, 0f), radius = 900f
                )
            )
    ) {
        if (isLandscape) {
            // HORIZONTAL: video a la izquierda, título y botón a la derecha
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                VideoCard(player = player, size = 180.dp, videoEnded = videoEnded)
                Spacer(Modifier.width(48.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TitleText(alpha = titleAlpha)
                    Spacer(Modifier.height(32.dp))
                    EnterButton(
                        onClick = onEnter,
                        enabled = showButton,
                        alpha = buttonAlpha,
                        modifier = Modifier.width(220.dp)
                    )
                }
            }
        } else {
            // VERTICAL: todo en columna, centrado
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(1f))
                TitleText(alpha = titleAlpha)
                Spacer(Modifier.height(48.dp))
                VideoCard(player = player, size = 200.dp, videoEnded = videoEnded)
                Spacer(Modifier.height(72.dp))
                EnterButton(
                    onClick = onEnter,
                    enabled = showButton,
                    alpha = buttonAlpha,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

// Título (reserva su espacio, solo cambia la opacidad)
@Composable
private fun TitleText(alpha: Float) {
    Text(
        text = "Cristalia",
        color = Color.White,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.alpha(alpha)  // alpha -> nivel de opacidad que viene de la variable de arriba y se le da los valores de la función
    )
}

// Tarjeta: video mientras corre, imagen del último cuadro cuando termina
@OptIn(UnstableApi::class)
@Composable
private fun VideoCard(player: ExoPlayer, size: Dp, videoEnded: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x1A00F0FF),
        border = BorderStroke(1.dp, Color(0x3300F0FF)),
        modifier = Modifier.size(size)
    ) {
        if (videoEnded) {
            // Imagen fija: se ve igual aunque rotes o vuelvas a la pantalla
            Image(
                painter = painterResource(R.drawable.ultimo_cuadro),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        } else {
            AndroidView(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
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
    }
}

// Botón Entrar
@Composable
private fun EnterButton(
    onClick: () -> Unit,
    enabled: Boolean,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Cyan,
            contentColor = Color.Black,
            disabledContainerColor = Cyan,
            disabledContentColor = Color.Black
        ),
        modifier = modifier
            .alpha(alpha)
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
}