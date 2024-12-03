package com.example.alicorpdemo

import PisoViewModel
import android.graphics.PointF
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alicorpdemo.database.Piso
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alicorpdemo.database.Camara
import kotlin.math.pow


@Composable
fun CamaraScreen(pisoId: Int, viewModel: CamaraViewModel, viewModelPiso: PisoViewModel , navController: NavController) {
    val camaras by viewModel.obtenerCamarasPorPiso(pisoId).observeAsState(emptyList())
    val piso by viewModelPiso.obtenerPisoPorId(pisoId).observeAsState()

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Column(modifier = Modifier.padding(0.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .background(Color.Red)
        ) {
            piso?.let {
                Text(
                    text = "Piso - ${it.nombre}",
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
                .padding(16.dp),
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
                    piso?.let {
                        navController.navigate("crearCamara/${it.id}")
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
                    text = "Agregar Camara",
                    color = Color.White,
                )
            }

        }



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Gray)
                .clipToBounds()
        ) {
            piso?.let {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, _, _ ->
                                offsetX += pan.x
                                offsetY += pan.y
                            }
                        }

                ) {
                    Image(
                        painter = rememberImagePainter(it.imagen),
                        contentDescription = "Imagen del Piso",
                        modifier = Modifier
                            .graphicsLayer(
                                translationX = offsetX,
                                translationY = offsetY
                            )
                            .fillMaxSize()
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures { tapOffset ->
                                    // Detectar si se hizo clic cerca de un marcador
                                    camaras.forEach { camara ->
                                        val marcadorX = camara.latitud + offsetX
                                        val marcadorY = camara.longitud + offsetY
                                        val distancia = kotlin.math.sqrt(
                                            (tapOffset.x - marcadorX).pow(2) + (tapOffset.y - marcadorY).pow(2)
                                        )

                                        if (distancia <= 20f) { // Radio del círculo
                                            navController.navigate("detalleInformes/${camara.id}")
                                        }
                                    }
                                }
                            }
                    )  {
                        camaras.forEach { camara ->
                            val marcadorX = camara.latitud
                            val marcadorY = camara.longitud

                            // Dibujar sombra
                            drawCircle(
                                color = Color.Black.copy(alpha = 0.2f), // Sombra negra con transparencia
                                radius = 30f, // Radio de la sombra un poco mayor que el círculo
                                center = Offset(marcadorX + offsetX, marcadorY + offsetY)
                            )

                            // Dibujar borde
                            drawCircle(
                                color = Color.White, // Color del borde
                                radius = 23f, // Radio del borde un poco mayor que el círculo rojo
                                center = Offset(marcadorX + offsetX, marcadorY + offsetY),
                                style = Stroke(width = 4f) // Grosor del borde
                            )

                            // Dibujar círculo relleno
                            drawCircle(
                                color = Color.Red, // Color del relleno
                                radius = 20f, // Radio del círculo
                                center = Offset(marcadorX + offsetX, marcadorY + offsetY)
                            )
                        }
                    }

                }
            } ?: run {
                Text(
                    text = "Cargando imagen...",
                    color = Color.White,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }


    }
}


