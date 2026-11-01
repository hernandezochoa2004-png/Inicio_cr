package me.mariahernandez.cristalia.celdaunitaria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Arreglo errores
import me.mariahernandez.cristalia.SessionRepository
import me.mariahernandez.cristalia.SessionRepositoryFalso

/** Estado que sobrevive a la rotación de pantalla. */
data class CeldaUnitariaUiState(
    val tipoSeleccionado: TipoCelda = TipoCelda.FCC
)

/** Eventos de "una sola vez" (navegar, descargar). */
sealed interface EventoUi {
    data object IrALogin : EventoUi
    data class Descargar(val archivoGlb: String) : EventoUi
}

class CeldaUnitariaViewModel(
    private val session: SessionRepository = SessionRepositoryFalso
) : ViewModel() {

    private val _uiState = MutableStateFlow(CeldaUnitariaUiState())
    val uiState: StateFlow<CeldaUnitariaUiState> = _uiState.asStateFlow()

    private val _eventos = Channel<EventoUi>(Channel.BUFFERED)
    val eventos = _eventos.receiveAsFlow()

    fun seleccionarTipo(tipo: TipoCelda) {
        _uiState.update { it.copy(tipoSeleccionado = tipo) }
    }

    /** Si hay sesión descarga; si no, manda al login. */
    fun onDescargarClick(archivoGlb: String) {
        viewModelScope.launch {
            if (session.estaLogueado()) {
                _eventos.send(EventoUi.Descargar(archivoGlb))
            } else {
                _eventos.send(EventoUi.IrALogin)
            }
        }
    }
}