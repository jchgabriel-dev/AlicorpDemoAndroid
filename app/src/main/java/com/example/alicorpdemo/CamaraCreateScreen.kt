package com.example.alicorpdemo

import PisoViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.alicorpdemo.components.ActionButton
import com.example.alicorpdemo.components.HeaderText
import com.example.alicorpdemo.components.InputCreate
import com.example.alicorpdemo.components.MainColumn
import com.example.alicorpdemo.components.OptionButton
import com.example.alicorpdemo.components.SecondaryButton
import com.example.alicorpdemo.components.TextDialog
import com.example.alicorpdemo.database.Camara
import com.example.alicorpdemo.database.Piso
import java.io.File
import java.io.InputStream

@Composable
fun CamaraCreateScreen(
    pisoId: Int,
    viewModel: CamaraViewModel,
    viewModelPiso: PisoViewModel,
    navController: NavController,
    context: Context
) {
    val camaras by viewModel.obtenerCamarasPorPiso(pisoId).observeAsState(emptyList())
    val piso by viewModelPiso.obtenerPisoPorId(pisoId).observeAsState()

    var marcador by remember { mutableStateOf<PointF?>(null) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var nombreError by remember { mutableStateOf(false) }
    var codigoError by remember { mutableStateOf(false) }
    var descripcionError by remember { mutableStateOf(false) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val qrCode = intent?.getStringExtra("SCAN_RESULT") ?: ""
            if (qrCode.isNotEmpty()) {
                codigo = qrCode
                Toast.makeText(context, "Código QR escaneado: $codigo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No se escaneó ningún código válido", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun launchQRCodeScanner() {
        val intent = Intent(context, com.journeyapps.barcodescanner.CaptureActivity::class.java)
        launcher.launch(intent)
    }


    MainColumn() {
        HeaderText(texto = piso?.nombre ?: "Piso no encontrado", navController = navController)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            OptionButton(
                icon = painterResource(id = R.drawable.baseline_add_a_photo_24),
                buttonColor = Color.Red,
                onClick = {
                    if (marcador == null) {
                        showErrorDialog = true
                    } else {
                        isDialogVisible = true
                    }
                }
            )

            OptionButton(
                icon = painterResource(id = R.drawable.baseline_add_a_photo_24),
                buttonColor = Color.Gray,
                onClick = {
                    launchQRCodeScanner()
                }
            )




            ActionButton(
                text = "Escanear código",
                onClick = {
                    launchQRCodeScanner()
                }
            )
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
                                color = Color.Black.copy(alpha = 0.2f),
                                radius = 30f,
                                center = Offset(
                                    position.x + offsetX,
                                    position.y + offsetY
                                )                             )
                            drawCircle(
                                color = Color.White,
                                radius = 23f,
                                center = Offset(
                                    position.x + offsetX,
                                    position.y + offsetY
                                )                                ,
                                style = Stroke(width = 4f)
                            )

                            drawCircle(
                                color = Color.Blue,
                                radius = 20f,
                                center = Offset(
                                    position.x + offsetX,
                                    position.y + offsetY
                                )                            )
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
                containerColor = Color.White,
                onDismissRequest = {
                    isDialogVisible = false
                },
                title = { TextDialog(text = "Datos de la camara") },
                text = {
                    Column {
                        InputCreate(value = nombre,
                            onValueChange = {
                                nombre = it
                                nombreError = it.isEmpty()
                            },
                            label = "Nombre",
                            isError = nombreError
                        )

                        InputCreate(value = codigo,
                            onValueChange = {
                                codigo = it
                                codigoError = it.isEmpty()
                            },
                            label = "Codigo",
                            isError = codigoError
                        )

                        InputCreate(value = descripcion,
                            onValueChange = {
                                descripcion = it
                                descripcionError = it.isEmpty()
                            },
                            label = "Descripcion",
                            isError = descripcionError
                        )
                    }
                },
                confirmButton = {
                    ActionButton(
                        text = "Crear equipo",
                        onClick = {
                            val hasError = nombre.isEmpty() || codigo.isEmpty() || descripcion.isEmpty()

                            if (hasError) {
                                nombreError = nombre.isEmpty()
                                codigoError = codigo.isEmpty()
                                descripcionError = descripcion.isEmpty()
                            } else {
                                marcador?.let {
                                    val newCamara = Camara(
                                        nombre = nombre,
                                        codigo = codigo,
                                        descripcion = descripcion,
                                        pisoId = pisoId,
                                        latitud = it.x,
                                        longitud = it.y
                                    )
                                    viewModel.agregarCamara(newCamara)
                                    navController.popBackStack()
                                }
                                isDialogVisible = false
                            }
                        },
                    )


                },
                dismissButton = {
                    SecondaryButton(
                        text = "Cancelar",
                        onClick = {
                            isDialogVisible = false
                        },

                    )
                }
            )
        }

        if (showErrorDialog) {
            AlertDialog(
                containerColor = Color.White,
                onDismissRequest = { showErrorDialog = false },
                title = { TextDialog(text = "Error") },
                text = { Text(text = "Por favor, selecciona un marcador primero.", color = Color.Black) },
                confirmButton = {
                    SecondaryButton(
                        text = "Aceptar",
                        onClick = {
                            showErrorDialog = false
                        },
                        )
                }
            )
        }
    }
}



