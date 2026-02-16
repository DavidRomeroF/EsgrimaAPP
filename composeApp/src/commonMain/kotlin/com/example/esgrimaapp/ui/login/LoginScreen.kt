package com.example.esgrimaapp.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import esgrimaapp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import esgrimaapp.composeapp.generated.resources.logo_app
import com.example.aprendepalabras.ui.theme.Principal
import com.example.esgrimaapp.ui.MainScaffoldScreen

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()

        // Lógica sencilla: Variables de estado para capturar lo que escribe el usuario
        var usuario by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var error by remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Principal
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_app),
                    contentDescription = "App logo",
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = "EsgrimaApp",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.Black
                )

                // Card de Login con la lógica de validación
                LoginCard(
                    usuarioValue = usuario,
                    onUsuarioChange = { usuario = it },
                    passwordValue = password,
                    onPasswordChange = { password = it },
                    onLoginAction = {
                        // LOGICA SENCILLA: Si es admin y 1234, entra
                        if (usuario == "admin" && password == "1234") {
                            error = false
                            navigator.replaceAll(MainScaffoldScreen()) // Navega al Dashboard
                        } else {
                            error = true
                        }
                    }
                )

                if (error) {
                    Text("Usuario o contraseña incorrectos", color = Color.Red, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "© 2026 EsgrimaAPP. Gestión de competiciones moderna y eficiente",
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun LoginCard(
    usuarioValue: String,
    onUsuarioChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    onLoginAction: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.width(350.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Text("Usuario", fontWeight = FontWeight.Bold)
                // CONECTADO AL ESTADO
                CustomOutlinedTextField(
                    value = usuarioValue,
                    onValueChange = onUsuarioChange,
                    placeholder = "admin",
                    icon = Icons.Outlined.AccountCircle
                )

                Text("Contraseña", fontWeight = FontWeight.Bold)
                // CONECTADO AL ESTADO
                CustomOutlinedTextField(
                    value = passwordValue,
                    onValueChange = onPasswordChange,
                    placeholder = "1234",
                    icon = Icons.Outlined.Lock,
                    isPassword = true
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CheckboxWithText()
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontWeight = FontWeight.Bold,
                    color = Principal,
                    textAlign = TextAlign.End,
                    modifier = Modifier.clickable { /*TODO*/ }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onLoginAction() }, // EJECUTA LA LÓGICA
                    colors = ButtonDefaults.buttonColors(containerColor = Principal),
                    shape = RoundedCornerShape(10.dp)
                ){
                    Text(
                        text = "Acceder",
                        fontWeight = FontWeight.Bold,
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "¿Primera vez?",
                        color = Color.Black
                    )
                    Text(
                        text = "Registrate aquí",
                        fontWeight = FontWeight.Bold,
                        color = Principal,
                        modifier = Modifier.clickable {  }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,              // <--- Nuevo
    onValueChange: (String) -> Unit, // <--- Nuevo
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,           // <--- Usa el valor de fuera
        onValueChange = onValueChange, // <--- Avisa hacia fuera
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        ),
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Gray,
            unfocusedIndicatorColor = Color.LightGray,
            unfocusedPlaceholderColor = Color.LightGray,
            focusedLeadingIconColor = Color.Gray,
            unfocusedLeadingIconColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTrailingIconColor = Color.Gray,
            unfocusedTrailingIconColor = Color.LightGray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Gray
        )
    )
}

@Composable
fun CheckboxWithText() {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.clickable {
            isChecked = !isChecked
            /*TODO*/
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Gray,
                uncheckedColor = Color.Gray
            )
        )
        Text(
            text = "Recordarme",
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}

