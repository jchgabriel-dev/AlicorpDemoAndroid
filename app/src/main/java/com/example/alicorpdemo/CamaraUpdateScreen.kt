package com.example.alicorpdemo
import PisoViewModel
import android.content.Context
import android.graphics.PointF
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.alicorpdemo.database.Camara
import java.io.File
import java.io.InputStream


@Composable
fun CamaraUpdateScreen(
    camaraId: Int, // ID de la cámara a actualizar
    viewModel: CamaraViewModel,
    viewModelPiso: PisoViewModel,
    navController: NavController,
    context: Context
) {
    val camara by viewModel.obtenerCamaraPorId(camaraId).observeAsState()
    val piso by viewModelPiso.obtenerPisoPorId(camara?.pisoId ?: 0).observeAsState()

    var marcador by remember { mutableStateOf<PointF?>(null) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    LaunchedEffect(camara) {
        camara?.let {
            nombre = it.nombre
            codigo = it.codigo
            descripcion = it.descripcion
            marcador = if (it.latitud != 0f && it.longitud != 0f) {
                PointF(it.latitud, it.longitud)
            } else {
                null
            }
        }
    }

    var nombreError by remember { mutableStateOf(false) }
    var codigoError by remember { mutableStateOf(false) }
    var descripcionError by remember { mutableStateOf(false) }


    Column(modifier = Modifier.padding(0.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text(text = "Regresar", color = Color.White,)
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                onClick = {
                    if (marcador == null) {
                        showErrorDialog = true
                    } else {
                        isDialogVisible = true
                    }
                }
            ) {
                Text(text = "Guardar camara", color = Color.White,)
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
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val imageX = (offset.x - offsetX)
                                val imageY = (offset.y - offsetY)

                                marcador = PointF(imageX, imageY)
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

                    marcador?.let { position ->
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color.Black.copy(alpha = 0.2f), // Sombra negra con transparencia
                                radius = 30f, // Radio de la sombra un poco mayor que el círculo
                                center = Offset(
                                    position.x + offsetX,
                                    position.y + offsetY
                                )
                            )
                            drawCircle(
                                color = Color.White, // Color del borde
                                radius = 23f, // Radio del borde un poco mayor que el círculo rojo
                                center = Offset(
                                    position.x + offsetX,
                                    position.y + offsetY
                                ),
                                style = Stroke(width = 4f) // Grosor del borde
                            )

                            drawCircle(
                                color = Color.Blue, // Color del relleno
                                radius = 20f, // Radio del círculo
                                center = Offset(
                                    position.x + offsetX,
                                    position.y + offsetY
                                )
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

        if (isDialogVisible) {
            AlertDialog(
                onDismissRequest = {
                    isDialogVisible = false
                },
                title = { Text(text = "Actualizar Datos de la Cámara") },
                text = {
                    Column {
                        // Campo de texto para el nombre
                        TextField(
                            value = nombre,
                            onValueChange = {
                                nombre = it
                                nombreError = it.isEmpty() // Actualiza el error si está vacío
                            },
                            label = { Text("Nombre", color = Color.Black) },
                            modifier = Modifier.fillMaxWidth(),
                            isError = nombreError
                        )
                        if (nombreError) {
                            Text("Este campo es obligatorio", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        // Campo de texto para el código
                        TextField(
                            value = codigo,
                            onValueChange = {
                                codigo = it
                                codigoError = it.isEmpty() // Actualiza el error si está vacío
                            },
                            label = { Text("Código", color = Color.Black) },
                            modifier = Modifier.fillMaxWidth(),
                            isError = codigoError
                        )
                        if (codigoError) {
                            Text("Este campo es obligatorio", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }

                        // Campo de texto para la descripción
                        TextField(
                            value = descripcion,
                            onValueChange = {
                                descripcion = it
                                descripcionError = it.isEmpty() // Actualiza el error si está vacío
                            },
                            label = { Text("Descripción", color = Color.Black) },
                            modifier = Modifier.fillMaxWidth(),
                            isError = descripcionError
                        )
                        if (descripcionError) {
                            Text("Este campo es obligatorio", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Validar campos antes de guardar
                            val hasError = nombre.isEmpty() || codigo.isEmpty() || descripcion.isEmpty()

                            if (hasError) {
                                // Actualizar los estados de error para mostrar los campos inválidos
                                nombreError = nombre.isEmpty()
                                codigoError = codigo.isEmpty()
                                descripcionError = descripcion.isEmpty()
                            } else {
                                marcador?.let {
                                    val updatedCamara = Camara(
                                        id = camaraId, // ID de la cámara existente
                                        nombre = nombre,
                                        codigo = codigo,
                                        descripcion = descripcion,
                                        pisoId = camara?.pisoId ?: 0,
                                        latitud = it.x,
                                        longitud = it.y
                                    )
                                    viewModel.actualizarCamara(updatedCamara)
                                    navController.popBackStack()
                                }
                                isDialogVisible = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Actualizar", color = Color.White,)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            isDialogVisible = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text("Cancelar" , color = Color.White, )
                    }
                }
            )
        }

        // Dialogo de error si no se selecciona un marcador
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text("Por favor, selecciona un marcador primero.") },
                confirmButton = {
                    Button(onClick = { showErrorDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}
