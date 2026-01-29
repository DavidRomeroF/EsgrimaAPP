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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import esgrimaapp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import esgrimaapp.composeapp.generated.resources.logo_app
import com.example.aprendepalabras.ui.theme.Principal

@Composable
fun LoginScreen(navController: NavController) {
    val scrollState = rememberScrollState()

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
            Text(
                text = "Sistema de Gestión de Competiciones",
                fontSize = 18.sp,
                color = Color.Black
            )
            LoginCard(navController)
            Text(
                text = "© 2026 EsgrimaAPP. Gestión de competiciones moderna y eficiente",
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun LoginCard(
    navController: NavController
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Usuario",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                CustomOutlinedTextField(
                    placeholder = "Tu usuario",
                    icon = Icons.Outlined.AccountCircle
                )
                Text(
                    text = "Contraseña",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                CustomOutlinedTextField(
                    placeholder = "Tu contraseña",
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
                    onClick = { navController.navigate("main") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Principal,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Acceder",
                        fontWeight = FontWeight.Bold,
                    )
                }
                /*
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
                        color = Color.LightGray
                    )
                    Text(
                        text = "Registrate aquí",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5C7654),
                        modifier = Modifier.clickable { /*TODO*/ }
                    )
                }
                */
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }


    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
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
            // Color del borde
            focusedIndicatorColor = Color.Gray,
            unfocusedIndicatorColor = Color.LightGray,
            // Color del placeholder
            unfocusedPlaceholderColor = Color.LightGray,
            // Color de icono principal
            focusedLeadingIconColor = Color.Gray,
            unfocusedLeadingIconColor = Color.LightGray,
            // Color de fondo
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            // Color del icono secundario
            focusedTrailingIconColor = Color.Gray,
            unfocusedTrailingIconColor = Color.LightGray,
            // Color del texto
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


@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    val navController = rememberNavController()

    LoginScreen(navController)
}
