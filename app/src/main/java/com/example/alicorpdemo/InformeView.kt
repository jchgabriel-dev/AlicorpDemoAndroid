package com.example.alicorpdemo

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.alicorpdemo.database.Informe
import com.example.alicorpdemo.database.Piso

@Composable
fun InformeScreen(camaraId: Int, viewModel: InformeViewModel, viewModelCamara: CamaraViewModel , navController: NavController) {
    val informes by viewModel.obtenerInformesPorCamara(camaraId).observeAsState(emptyList())
    val camara by viewModelCamara.obtenerCamaraPorId(camaraId).observeAsState()

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Column(modifier = Modifier.padding(0.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical =  0.dp , horizontal = 0.dp)
                .background(Color.Red)
        ) {
            camara?.let {
                Text(
                    text = "Camara - ${it.nombre}",
                    color = Color.White,
                    modifier = Modifier.padding(20.dp)
                )
            } ?: run {
                Text(
                    text = "Cargando piso...",
                    color = Color.White,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical =  8.dp , horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Button(
                onClick = { navController.popBackStack() }, // Regresa a la pantalla anterior
                modifier = Modifier.padding(top = 2.dp, start = 4.dp,),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = "Volver",
                    color = Color.White,
                )
            }

            Button(
                onClick = {
                    camara?.let {
                        navController.navigate("crearInforme/${it.id}")
                    } ?: run {
                        Log.e("CamaraScreen", "El piso no está disponible")
                    }
                },
                modifier = Modifier.padding(top = 2.dp, start = 4.dp,),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(
                    text = "Agregar Informe",
                    color = Color.White,
                )
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(
                text = "Listado de Informes",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )

            Button(
                onClick = { navController.navigate("updateCamara/${camara?.id}") },
                modifier = Modifier.padding(top = 2.dp, start = 4.dp,),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = "Camara",
                    color = Color.White,
                )
            }


        }


        if (informes.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(330.dp)
                    .fillMaxWidth()
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay informes disponibles",
                    color = Color.Black,

                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(informes) { informe ->
                    InformeItem(informe = informe, navController = navController)
                }
            }
        }


    }
}


@Composable
fun InformeItem(informe: Informe, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray)
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("updateInforme/${informe.id}")

            }
    ) {
        Text(
            text = "Fecha: ${informe.fecha}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(text = "Autor: ${informe.autor}")
    }
}