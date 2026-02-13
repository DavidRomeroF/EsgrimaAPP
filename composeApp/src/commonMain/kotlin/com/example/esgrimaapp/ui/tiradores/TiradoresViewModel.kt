import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.FencingRepository
import com.example.esgrimaapp.ui.tiradores.TiradoresUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Ya no necesitamos pasarle el ID por constructor si lo lee del Repo
class TiradoresViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(TiradoresUIState())
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            combine(
                FencingRepository.competiciones,
                FencingRepository.idCompeticionActiva,
                FencingRepository.usuariosGlobales,
                FencingRepository.inscripciones
            ) { listaComp, idActivo, usuarios, inscripciones ->
                val compActiva = listaComp.find { it.id == idActivo }

                TiradoresUIState(
                    usuariosDisponibles = usuarios,
                    nombreCompeticionActiva = compActiva?.nombre,
                    hayCompeticionActiva = idActivo != null,
                    listaTiradoresInscritos = inscripciones[idActivo] ?: emptyList(),
                    mostrarFormulario = _uiState.value.mostrarFormulario // Mantener el estado del diálogo
                )
            }.collect { nuevoEstado ->
                _uiState.value = nuevoEstado
            }
        }
    }

    fun inscribirTirador(usuario: Usuario) {
        // Obtenemos el ID activo directamente del repositorio
        val idActivo = FencingRepository.idCompeticionActiva.value
        if (idActivo != null) {
            FencingRepository.inscribirTirador(idActivo, usuario)
            toggleSelector(false)
        }
    }

    fun removerTiradorDeCompeticion(numFede: String) {
        val idActivo = FencingRepository.idCompeticionActiva.value
        if (idActivo != null) {
            FencingRepository.desapuntarTirador(idActivo, numFede)
        }
    }

    fun toggleSelector(mostrar: Boolean) {
        // Solo permitimos abrir si realmente hay una competición activa
        if (_uiState.value.hayCompeticionActiva) {
            _uiState.update { it.copy(mostrarFormulario = mostrar) }
        }
    }
}