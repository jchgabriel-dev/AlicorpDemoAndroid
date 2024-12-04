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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.alicorpdemo.components.ActionButton
import com.example.alicorpdemo.components.HeaderText
import com.example.alicorpdemo.components.InputCreate
import com.example.alicorpdemo.components.InputShow
import com.example.alicorpdemo.components.MainColumn
import com.example.alicorpdemo.components.OptionButton
import com.example.alicorpdemo.components.SecondaryButton
import com.example.alicorpdemo.components.TextDialog
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

    var nombreError by remember { mutableStateOf(false) }
    var codigoError by remember { mutableStateOf(false) }
    var descripcionError by remember { mutableStateOf(false) }

    MainColumn() {
        HeaderText(texto = "Edicion de equipos", navController = navController)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.DarkGray)
                .clipToBounds()
        ) {

            Box(modifier = Modifier.padding(8.dp).zIndex(1f).align(Alignment.TopEnd)) {
                Row(
                    modifier = Modifier
                        .background(Color(0x80FFFFFF), shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .padding(vertical = 5.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    OptionButton(
                        icon = painterResource(id = R.drawable.baseline_mode_edit_24),
                        buttonColor = Color.Red,
                        onClick = {
                            if (marcador == null || codigo.isEmpty()) {
                                showErrorDialog = true
                            } else {
                                isDialogVisible = true
                            }
                        }
                    )

                    OptionButton(
                        icon = painterResource(id = R.drawable.baseline_add_a_photo_24),
                        buttonColor = Color.DarkGray,
                        onClick = {
                            launchQRCodeScanner()
                        }
                    )


                    InputShow(
                        label = "Codigo",
                        value = codigo,
                        onValueChange = {
                            codigo = it
                        },
                    )
                }
            }


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
                                radius = 35f,
                                center = Offset(
                                    position.x + offsetX,
                                    position.y + offsetY
                                )                             )
                            drawCircle(
                                color = Color.White,
                                radius = 28f,
                                center = Offset(
                                    position.x + offsetX,
                                    position.y + offsetY
                                )                                ,
                                style = Stroke(width = 4f)
                            )

                            drawCircle(
                                color = Color.Red,
                                radius = 25f,
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
                containerColor = Color.White,
                onDismissRequest = {
                    isDialogVisible = false
                },
                title = { TextDialog(text = "Datos del equipo") },
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
                        text = "Guardar equipo",
                        onClick = {
                            val hasError = nombre.isEmpty() || codigo.isEmpty() || descripcion.isEmpty()

                            if (hasError) {
                                nombreError = nombre.isEmpty()
                                descripcionError = descripcion.isEmpty()
                            } else {
                                marcador?.let {
                                    val updatedCamara = Camara(
                                        id = camaraId,
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
            val messageError = mutableListOf<String>()

            if (codigo.isEmpty())
                messageError.add("Por favor, escanee el código")

            if (marcador == null)
                messageError.add("Por favor, selecciona un marcador primero")

            AlertDialog(
                containerColor = Color.White,
                onDismissRequest = { showErrorDialog = false },
                title = { TextDialog(text = "Error") },
                text = {
                    // Combina los mensajes en un único texto separado por saltos de línea
                    Text(
                        text = messageError.joinToString(separator = "\n"),
                        color = Color.Black
                    )
                },
                confirmButton = {
                    SecondaryButton(
                        text = "Aceptar",
                        onClick = {
                            showErrorDialog = false
                        }
                    )
                }
            )
        }
    }




}
