package com.example.esgrimaapp.ui.usuarios

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.aprendepalabras.ui.theme.CartaTiradores
import com.example.esgrimaapp.data.Usuario
import esgrimaapp.composeapp.generated.resources.Res
import esgrimaapp.composeapp.generated.resources.database
import org.jetbrains.compose.resources.painterResource

class UsuariosScreen : Screen {
    @Composable
    override fun Content() {
        // En KMP con Voyager, esto es lo que evita el crash del Factory
        val viewModel = rememberScreenModel { UsuariosViewModel() }

        // Pasamos el estado recolectado para que el Layout sea "puro"
        val uiState by viewModel.uiState.collectAsState()

        // El Scaffold asegura que el fondo y el área de contenido se gestionen bien
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                UsuarioLayout(uiState, viewModel)
            }
        }
    }
}

@Composable
fun UsuarioLayout(
    uiState: UsuarioUIState,
    viewModel: UsuariosViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // Importante para que ocupe toda la pantalla
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Añade espacio entre bloques automáticamente
    ) {
        // 1. Encabezado
        item {
            Column {
                Text(
                    text = "Base de Datos de Usuarios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("Gestiona las personas que pueden participar como tiradores y/o árbitros")
            }
        }

        // 2. Botón de acción (Nuevo usuario)
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                OutlinedButton(
                    onClick = { viewModel.toggleFormulario() },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CartaTiradores),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CartaTiradores,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Nuevo Usuario",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item {
            TextoInformativo()
        }

        // 3. Formulario (Solo si está activo)
        item {
            FormularioCreacionUsuarios(uiState.mostrarFormulario, uiState, viewModel)
        }

        item {
            SeccionListaUsuarios(
                usuarios = uiState.listaUsuarios,
                onEliminar = { id -> viewModel.eliminarUsuario(id) },
                uiState = uiState,
                viewModel = viewModel
            )
        }
    }
}
@Composable
fun TextoInformativo(){
    Card(
        colors = CardDefaults.cardColors(
            // He usado un azul muy claro similar al de la captura
            containerColor = Color(0xFFF0F7FF)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 30.dp),
        border = BorderStroke(1.dp, Color(0xFFD0E4FF)), // Borde azul suave
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fila del Título con Icono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.database),
                    contentDescription = null,
                    tint = Color(0xFF0D47A1) // Azul oscuro
                )
                Text(
                    text = "Información importante:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Lista de puntos (basado en tu captura)
            val puntos = listOf(
                "Cada persona se registra una sola vez con su número de federado único",
                "Una persona puede ser tirador, árbitro, o ambos en diferentes competiciones",
                "No puede participar como tirador y árbitro en la misma competición",
                "Marca \"Puede ser árbitro\" y selecciona especialidades si la persona está capacitada para arbitrar"
            )

            puntos.forEach { punto ->
                Row(
                    modifier = Modifier.padding(start = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("•", color = Color(0xFF0D47A1), fontWeight = FontWeight.Black)
                    Text(
                        text = punto,
                        color = Color(0xFF1565C0), // Azul intermedio
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun FormularioCreacionUsuarios(mostrar: Boolean, uiState: UsuarioUIState,viewModel: UsuariosViewModel){

    if(mostrar){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                // Añadimos el fondo y el borde aquí
                .background(
                    color = Color.White, // O un color gris muy suave
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant, // Borde sutil
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(20.dp), // Padding interno para que el contenido no pegue al borde
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nueva Persona",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .heightIn(max = 2000.dp) // Dale suficiente margen para el formulario
                    .fillMaxWidth()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    userScrollEnabled = false
                ) {
                    item {
                        CampoTexto(
                            "Nombre Completo",
                            uiState.nombre
                        ) { viewModel.onNombreChange(it) }
                    }
                    item {
                        CampoTexto(
                            "Número de Federado",
                            uiState.numeroFederacion
                        ) { viewModel.onNumeroFederacionChange(it) }
                    }
                    item {
                        CampoTexto(
                            "Club",
                            uiState.club
                        ) { viewModel.onClubChange(it) }
                    }
                    item {
                        CampoTexto(
                            "Contraseña",
                            uiState.contrasenya
                        ) { viewModel.onContrasenyaChange(it) }                    }
                    item {
                        CampoTexto(
                            "Repetir Contraseña",
                            uiState.repContrasenya
                        ) { viewModel.onRepContrasenyaChange(it) }                    }
                }
            }
            PuedeSerArbitro(uiState,viewModel)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End) // Botones a la derecha
            ) {
                TextButton(
                    onClick = { viewModel.toggleFormulario()
                        viewModel.limpiarFormulario()},
                    modifier = Modifier
                        .width(150.dp) // Puedes ajustar el ancho o quitarlo para que ocupe solo el texto
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Gray // Esto pone el texto en gris
                    )
                ) {
                    Text(
                        text = "Cancelar",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.crearNuevoUsuario() },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CartaTiradores),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CartaTiradores,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Crear Usuario",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
@Composable
fun ContenedorCampo(label: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
        content()
    }
}
@Composable
fun CampoTexto(label: String, value: String, onValueChange: (String) -> Unit) {
    ContenedorCampo(label) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Escribe aquí...") },
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
    }
}

@Composable
fun PuedeSerArbitro(uiState: UsuarioUIState,viewModel: UsuariosViewModel){
    // ... después del Box que contiene el LazyVerticalGrid

// Sección Checkbox "Puede ser árbitro"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { viewModel.onEsArbitroChange(!uiState.esArbitro) }
    ) {
        Checkbox(
            checked = uiState.esArbitro,
            onCheckedChange = { viewModel.onEsArbitroChange(it) },
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6200EE))
        )
        Icon(
            imageVector = Icons.Default.Shield, // Tu icono de escudo
            contentDescription = null,
            modifier = Modifier.size(20.dp).padding(horizontal = 4.dp),
            tint = Color(0xFF2196F3)
        )
        Text("Puede ser árbitro", style = MaterialTheme.typography.bodyLarge)
    }

// Sección de Especialidades (Solo visible si esArbitro es true)
    AnimatedVisibility(visible = uiState.esArbitro) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Especialidades de Arbitraje",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Espada", "Florete", "Sable").forEach { espec ->
                    val seleccionada = uiState.especialidades.contains(espec)

                    // Botón tipo "Filter Chip" manual
                    Surface(
                        modifier = Modifier.clickable { viewModel.onEspecialidadToggle(espec) },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (seleccionada) Color(0xFF6200EE) else Color.LightGray),
                        color = if (seleccionada) Color(0xFFF3E5F5) else Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (seleccionada) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp).padding(end = 4.dp),
                                    tint = Color(0xFF6200EE)
                                )
                            }
                            Text(espec, color = if (seleccionada) Color(0xFF6200EE) else Color.Black)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun SeccionListaUsuarios(
    usuarios: List<Usuario>,
    onEliminar: (String) -> Unit,
    uiState: UsuarioUIState,
    viewModel: UsuariosViewModel
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val esMovil = maxWidth < 750.dp

        if (esMovil) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (usuarios.isEmpty()) {
                    MensajeListaVacia()
                } else {
                    usuarios.forEach { usuario ->
                        UsuarioCardMovil(
                            usuario = usuario,
                            onEliminar = onEliminar,
                            onEditar = { viewModel.iniciarEdicion(usuario) }
                        )
                    }
                }
            }

            // Si estamos editando en móvil, mostramos el formulario por encima
            if (uiState.idUsuarioEditando != null) {
                FormularioEdicionMovil(uiState, viewModel)
            }

        } else {
            // MODO ESCRITORIO (Tu tabla profesional que ya tenemos)
            TablaUsuarios(
                usuarios = usuarios,
                onEliminar = onEliminar,
                onEditar = { viewModel.iniciarEdicion(it) },
                uiState = uiState,
                viewModel = viewModel
            )
        }
    }
}
@Composable
fun TablaUsuarios(
    usuarios: List<Usuario>,
    onEliminar: (String) -> Unit,
    onEditar: (Usuario) -> Unit,
    uiState: UsuarioUIState,
    viewModel: UsuariosViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        // CABECERA DE LA TABLA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFF8FAFC),
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Nombre",
                Modifier.weight(2f),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
            Text(
                "Club",
                Modifier.weight(2f),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
            Text(
                "Nº Federado",
                Modifier.weight(1.5f),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
            Text(
                "Roles",
                Modifier.weight(1.5f),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
            Text(
                "Especialidades",
                Modifier.weight(2f),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
            Text(
                "Acciones",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }

        Divider(color = Color(0xFFE0E0E0))

        if (usuarios.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(Res.drawable.database),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF94A3B8)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("No hay personas registradas en la base de datos", fontWeight = FontWeight.Medium)
                    Text("Añade la primera persona para comenzar", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            usuarios.forEach { usuario ->
                // COMPARACIÓN CLAVE: Verificamos si este usuario es el que se está editando
                val esEditando = uiState.idUsuarioEditando == usuario.numeroFederacion

                FilaUsuario(
                    usuario = usuario,
                    esEditando = esEditando, // Pasamos el booleano calculado
                    onEliminar = onEliminar,
                    onEditar = onEditar,
                    uiState = uiState,
                    viewModel = viewModel
                )
                Divider(color = Color(0xFFF1F5F9))
            }
        }
    }
}

@Composable
fun UsuarioCardMovil(
    usuario: Usuario,
    onEliminar: (String) -> Unit,
    onEditar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("${usuario.club} • Fed: ${usuario.numeroFederacion}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)

                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagRol("Tirador", Color(0xFFDBEAFE), Color(0xFF2563EB), Icons.Default.Person)
                    if (usuario.esArbitro) {
                        TagRol("Árbitro", Color(0xFFF3E8FF), Color(0xFF9333EA), Icons.Default.Shield)
                    }
                }
            }

            // Acciones rápidas
            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, "Editar", tint = Color(0xFF2563EB))
                }
                IconButton(onClick = { onEliminar(usuario.numeroFederacion) }) {
                    Icon(Icons.Default.Delete, "Borrar", tint = Color(0xFFEF4444))
                }
            }
        }
    }
}

@Composable
fun FormularioEdicionMovil(uiState: UsuarioUIState, viewModel: UsuariosViewModel) {
    // Usamos un Dialog o un Box que ocupe toda la pantalla para que sea cómodo
    Dialog(onDismissRequest = { viewModel.cancelarEdicion() }) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Editar Persona", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                CampoTexto("Nombre Completo", uiState.editNombre) { viewModel.onEditNombre(it) }
                CampoTexto("Club", uiState.editClub) { viewModel.onEditClub(it) }

                Text("Nº Federado: ${uiState.idUsuarioEditando}", color = Color.Gray)

                Divider()

                // LOGICA DE ÁRBITRO (Igual que en el formulario de creación)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = uiState.editEsArbitro, onCheckedChange = { viewModel.onEditEsArbitro(it) })
                    Text("Es Árbitro")
                }

                if (uiState.editEsArbitro) {
                    Text("Especialidades:", fontWeight = FontWeight.Bold)
                    // Usamos horizontalArrangement en lugar de mainAxisSpacing
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Espada", "Florete", "Sable").forEach { esp ->
                            val seleccionada = uiState.editEspecialidades.contains(esp)

                            // Usamos un diseño de Chip manual para mayor control
                            Surface(
                                onClick = { viewModel.onEditEspecialidadToggle(esp) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (seleccionada) Color(0xFFF3E8FF) else Color.White,
                                border = BorderStroke(1.dp, if (seleccionada) Color(0xFF9333EA) else Color.LightGray)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (seleccionada) {
                                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = Color(0xFF9333EA))
                                        Spacer(Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = esp,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (seleccionada) Color(0xFF9333EA) else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { viewModel.cancelarEdicion() }) { Text("Cancelar") }
                    Button(
                        onClick = { viewModel.guardarEdicion(uiState.idUsuarioEditando!!) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}
@Composable
fun MensajeListaVacia() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(Res.drawable.database),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF94A3B8)
            )
            Spacer(Modifier.height(16.dp))
            Text("No hay personas registradas", fontWeight = FontWeight.Medium)
            Text("Añade la primera para comenzar", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}
@Composable
fun FilaUsuario(
    usuario: Usuario,
    esEditando: Boolean,
    onEliminar: (String) -> Unit,
    onEditar: (Usuario) -> Unit,
    uiState: UsuarioUIState,
    viewModel: UsuariosViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (esEditando) {
            // --- MODO EDICIÓN (Inspirado en image_7b7851.png) ---
            TextFieldCelda(value = uiState.editNombre, onValueChange = { viewModel.onEditNombre(it) }, Modifier.weight(2f))
            TextFieldCelda(value = uiState.editClub, onValueChange = { viewModel.onEditClub(it) }, Modifier.weight(2f))

            // El Nº Federado se mantiene estático por seguridad de integridad
            Text(usuario.numeroFederacion, Modifier.weight(1.5f), color = Color.Gray)

            // Columna de Roles y Especialidades combinada
            Row(Modifier.weight(3.5f), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.editEsArbitro,
                    onCheckedChange = { viewModel.onEditEsArbitro(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF9333EA))
                )
                Text("Árbitro", style = MaterialTheme.typography.bodyMedium)

                if (uiState.editEsArbitro) {
                    Spacer(Modifier.width(12.dp))
                    listOf("Espada", "Florete", "Sable").forEach { esp ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = uiState.editEspecialidades.contains(esp),
                                onCheckedChange = { viewModel.onEditEspecialidadToggle(esp) }
                            )
                            Text(esp, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Botones de acción de edición
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
                IconButton(onClick = { viewModel.guardarEdicion(usuario.numeroFederacion) }) {
                    Icon(Icons.Default.Save, "Guardar", tint = Color(0xFF166534))
                }
                IconButton(onClick = { viewModel.cancelarEdicion() }) {
                    Icon(Icons.Default.Close, "Cancelar", tint = Color(0xFF64748B))
                }
            }
        } else {
            // --- MODO VISTA (Inspirado en image_7b7072.png) ---
            Text(usuario.nombre, Modifier.weight(2f), fontWeight = FontWeight.Bold)
            Text(usuario.club, Modifier.weight(2f), color = Color(0xFF64748B))
            Text(usuario.numeroFederacion, Modifier.weight(1.5f), color = Color(0xFF64748B))

            // Celda de Roles con Tags
            Row(Modifier.weight(1.5f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TagRol("Tirador", Color(0xFFDBEAFE), Color(0xFF2563EB), Icons.Default.Person)
                if (usuario.esArbitro) {
                    TagRol("Árbitro", Color(0xFFF3E8FF), Color(0xFF9333EA), Icons.Default.Shield)
                }
            }

            // Celda de Especialidades
            Row(Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (usuario.esArbitro && usuario.especialidades.isNotEmpty()) {
                    usuario.especialidades.forEach { esp ->
                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                            Text(esp, Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall, color = Color(0xFF166534))
                        }
                    }
                } else {
                    Text("-", color = Color.LightGray)
                }
            }

            // Botones de acción normales
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
                IconButton(onClick = { onEditar(usuario) }) {
                    Icon(Icons.Default.Edit, "Editar", tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = { onEliminar(usuario.numeroFederacion) }) {
                    Icon(Icons.Default.Delete, "Borrar", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun TagRol(texto: String, fondo: Color, contenido: Color, icono: ImageVector) {
    Surface(color = fondo, shape = RoundedCornerShape(4.dp)) {
        Row(
            Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icono, null, modifier = Modifier.size(12.dp), tint = contenido)
            Text(texto, style = MaterialTheme.typography.labelSmall, color = contenido, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TextFieldCelda(value: String, onValueChange: (String) -> Unit, modifier: Modifier) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.padding(4.dp).border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)).padding(8.dp),
        singleLine = true
    )
}