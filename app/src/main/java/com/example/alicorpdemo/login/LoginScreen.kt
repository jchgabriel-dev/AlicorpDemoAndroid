package com.example.alicorpdemo.login

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alicorpdemo.PisoItem
import com.example.alicorpdemo.R
import com.example.alicorpdemo.components.ActionButton
import com.example.alicorpdemo.components.HeaderText
import com.example.alicorpdemo.components.InputCreate
import com.example.alicorpdemo.components.InputPassword
import com.example.alicorpdemo.components.LongButton
import com.example.alicorpdemo.components.MainColumn
import com.example.alicorpdemo.components.TextTitle
import com.example.alicorpdemo.guardarImagenEnAlmacenamientoInterno
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID


@Composable
fun LoginScreen(viewModel: UsuarioViewModel, navController: NavController, context: Context) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf(false) }
    var contrasenaError by remember { mutableStateOf(false) }

    var loginError by remember { mutableStateOf(false) }

    val usuarioLiveData = viewModel.login(usuario, contrasena).observeAsState()

    MainColumn() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
                .background(Color.Red, shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
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
                text = "INICIO DE SESION",
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
                label = "Nombre",
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
                text = "Iniciar sesión",
                buttonColor = Color.Black,

                onClick = {
                    if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                        if (usuarioLiveData.value != null && usuarioLiveData.value?.contrasena == contrasena) {
                            navController.navigate("pisoList")
                        } else {
                            loginError = true
                        }
                    } else {
                        nombreError = usuario.isEmpty()
                        contrasenaError = contrasena.isEmpty()
                    }
                },
            )



            if (loginError) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center,

                ) {
                    Text(
                        text = "Usuario o contraseña incorrectos",
                        color = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ActionButton(
                    text = "Registrarse",
                    onClick = { navController.navigate("signup")}
                )
            }
        }
    }
}