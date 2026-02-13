import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.FencingRepository
import com.example.esgrimaapp.ui.arbitros.ArbitrosUIState
import com.example.esgrimaapp.ui.tiradores.TiradoresUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Ya no necesitamos pasarle el ID por constructor si lo lee del Repo
class ArbitroViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(ArbitrosUIState())
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            combine(
                FencingRepository.competiciones,
                FencingRepository.idCompeticionActiva,
                FencingRepository.usuariosGlobales,
                FencingRepository.inscripciones,
                FencingRepository.arbitrosInscritos
            ) { listaComp, idActivo, globales, inscritosTiradores, inscritosArbitros ->

                val compActiva = listaComp.find { it.id == idActivo }
                val armaRequerida = compActiva?.arma // Ejemplo: "Florete", "Espada" o "Sable"

                val tiradoresActuales = inscritosTiradores[idActivo] ?: emptyList()
                val arbitrosActuales = inscritosArbitros[idActivo] ?: emptyList()

                // FILTRO TRIPLE:
                // 1. Perfil: Debe ser árbitro.
                // 2. Especialidad: Debe tener el arma de la competición en su lista de especialidades.
                // 3. Exclusión: No debe estar ya inscrito (ni como tirador ni como árbitro).
                val disponiblesParaArbitrar = globales.filter { usuario ->
                    val tieneEspecialidad = usuario.especialidades.contains(armaRequerida)

                    usuario.esArbitro &&
                            tieneEspecialidad && // <--- VALIDACIÓN DE ARMA
                            tiradoresActuales.none { it.numeroFederacion == usuario.numeroFederacion } &&
                            arbitrosActuales.none { it.numeroFederacion == usuario.numeroFederacion }
                }

                ArbitrosUIState(
                    usuariosDisponibles = disponiblesParaArbitrar,
                    nombreCompeticionActiva = compActiva?.nombre,
                    hayCompeticionActiva = idActivo != null,
                    listaArbitrosInscritos = arbitrosActuales,
                    mostrarFormulario = _uiState.value.mostrarFormulario
                )
            }.collect { nuevoEstado ->
                _uiState.value = nuevoEstado
            }
        }
    }

    fun inscribirArbitro(usuario: Usuario) {
        val idActivo = FencingRepository.idCompeticionActiva.value
        if (idActivo != null) {
            FencingRepository.inscribirArbitro(idActivo, usuario)
            toggleSelector(false)
        }
    }

    fun removerArbitroDeCompeticion(numFede: String) {
        val idActivo = FencingRepository.idCompeticionActiva.value
        if (idActivo != null) {
            FencingRepository.desapuntarArbitro(idActivo, numFede)
        }
    }

    fun toggleSelector(mostrar: Boolean) {
        if (_uiState.value.hayCompeticionActiva) {
            _uiState.update { it.copy(mostrarFormulario = mostrar) }
        }
    }
}