package com.example.alicorpdemo.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alicorpdemo.R
import com.example.alicorpdemo.components.ActionButton
import com.example.alicorpdemo.components.InputCreate
import com.example.alicorpdemo.components.InputPassword
import com.example.alicorpdemo.components.LongButton
import com.example.alicorpdemo.components.MainColumn
import com.example.alicorpdemo.database.Usuario

@Composable
fun SignupScreen(viewModel: UsuarioViewModel, navController: NavController, context: Context) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf(false) }
    var contrasenaError by remember { mutableStateOf(false) }

    var registroError by remember { mutableStateOf(false) }

    val usuarioLiveData = viewModel.obtenerUsuarioPorUsername(usuario).observeAsState()

    MainColumn() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
                .background(Color.Black, shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .height(350.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.alicorp),
                contentDescription = "Imagen cuadrada",
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "REGISTRO DE USUARIO",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 10.dp),
                fontFamily = FontFamily.SansSerif,
            )
        }

        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            InputCreate(value = usuario,
                onValueChange = {
                    usuario = it
                    nombreError = it.isEmpty()
                },
                label = "Nombre de Usuario",
                isError = nombreError
            )

            InputPassword(value = contrasena,
                onValueChange = {
                    contrasena = it
                    contrasenaError = it.isEmpty()
                },
                label = "Contraseña",
                isError = contrasenaError
            )
            Spacer(modifier = Modifier.height(20.dp))

            LongButton(
                text = "Registrar Usuario",
                buttonColor = Color.Black,
                onClick = {
                    if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                        if (usuarioLiveData.value == null) {
                            viewModel.registrarUsuario(Usuario(username = usuario, contrasena = contrasena))
                            navController.popBackStack()
                        } else {
                            registroError = true
                        }
                    } else {
                        nombreError = usuario.isEmpty()
                        contrasenaError = contrasena.isEmpty()
                    }
                }
            )

            if (registroError) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "El usuario ya existe",
                        color = Color.Red
                    )
                }

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ActionButton(
                    text = "Iniciar sesion",
                    onClick = { navController.popBackStack()}
                )
            }
        }
    }

}
