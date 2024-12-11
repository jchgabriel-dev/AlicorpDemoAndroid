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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.alicorpdemo.components.HeaderText
import com.example.alicorpdemo.components.InputShow
import com.example.alicorpdemo.components.MainColumn
import com.example.alicorpdemo.components.OptionButton
import com.example.alicorpdemo.components.SecondaryButton
import com.example.alicorpdemo.components.TextDetail
import com.example.alicorpdemo.components.TextDetailOne
import com.example.alicorpdemo.components.TextDialog
import com.example.alicorpdemo.database.Camara
import kotlin.math.pow

import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamaraScreen(pisoId: Int, viewModel: CamaraViewModel, viewModelPiso: PisoViewModel , navController: NavController, context: Context) {

    var listaCamaras by remember { mutableStateOf(emptyList<Camara>()) }
    var showModalCamaras by remember { mutableStateOf(false) }

    val camaras by viewModel.obtenerCamarasPorPiso(pisoId).observeAsState(emptyList())
    val piso by viewModelPiso.obtenerPisoPorId(pisoId).observeAsState()

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var selectedCamara by remember { mutableStateOf<Camara?>(null) }
    var codigo by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val qrCode = intent?.getStringExtra("SCAN_RESULT") ?: ""
            if (qrCode.isNotEmpty()) {
                viewModel.obtenerCamaraPorCodigo(qrCode, pisoId).observeForever { camara ->
                    if (camara != null) {
                        selectedCamara = camara
                        showBottomSheet = true
                    } else {
                        Toast.makeText(context, "No se encontró dicho equipo en este piso", Toast.LENGTH_SHORT).show()
                    }
                }

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.DarkGray)
                .clipToBounds()
        ) {

            Row(modifier = Modifier.zIndex(1f)) {
                Box(modifier = Modifier.padding(8.dp).zIndex(1f).weight(1f)) {
                    Row(
                        modifier = Modifier
                            .background(Color(0x80FFFFFF), shape = RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .padding(vertical = 5.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        OptionButton(
                            icon = painterResource(id = R.drawable.baseline_youtube_searched_for_24),
                            buttonColor = Color.Red,
                            onClick = {

                                if (codigo.isBlank()) {
                                    Toast.makeText(context, "Por favor, ingrese el código", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.buscarCamarasPorCodigo(codigo).observeForever { resultado ->
                                        listaCamaras = resultado
                                        showModalCamaras = true
                                    }
                                }
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

                Box(modifier = Modifier.padding(8.dp).zIndex(1f)) {
                    Column(
                        modifier = Modifier
                            .background(Color(0x80FFFFFF), shape = RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(40.dp))
                            .padding(vertical = 15.dp, horizontal = 5.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    )
                    {
                        OptionButton(
                            icon = painterResource(id = R.drawable.baseline_add_circle_24),
                            buttonColor = Color.Red,
                            onClick = {
                                piso?.let {
                                    navController.navigate("crearCamara/${it.id}")
                                } ?: run {
                                    Log.e("CamaraScreen", "El piso no está disponible")
                                }
                            },
                        )

                        OptionButton(
                            icon = painterResource(id = R.drawable.baseline_add_a_photo_24),
                            buttonColor = Color.DarkGray,
                            onClick = {
                                launchQRCodeScanner()
                            }
                        )

                    }

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
                                    camaras.forEach { camara ->
                                        val marcadorX = camara.latitud + offsetX
                                        val marcadorY = camara.longitud + offsetY
                                        val distancia = kotlin.math.sqrt(
                                            (tapOffset.x - marcadorX).pow(2) + (tapOffset.y - marcadorY).pow(2)
                                        )

                                        if (distancia <= 80f) {
                                            selectedCamara = camara
                                            showBottomSheet = true
                                        }
                                    }
                                }
                            }
                    )  {
                        camaras.forEach { camara ->
                            val marcadorX = camara.latitud
                            val marcadorY = camara.longitud

                            drawCircle(
                                color = Color.Black.copy(alpha = 0.2f),
                                radius = 35f,
                                center = Offset(marcadorX + offsetX, marcadorY + offsetY)
                            )

                            drawCircle(
                                color = Color.Yellow,
                                radius = 28f,
                                center = Offset(marcadorX + offsetX, marcadorY + offsetY),
                                style = Stroke(width = 4f)
                            )

                            drawCircle(
                                color = Color.Red,
                                radius = 25f,
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


            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                    containerColor = Color.Red,
                ) {
                    selectedCamara?.let { camara ->

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_bookmark_24),
                                            contentDescription = "Item",
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .size(25.dp)
                                        )
                                        TextDialog(text = "Detalles del equipo")


                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    TextDetail(text = "Nombre: ${camara.nombre}")
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextDetail(text = "Descripcion: ${camara.descripcion}")
                                    Spacer(modifier = Modifier.height(20.dp))
                                }

                                OptionButton(
                                    icon = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                                    buttonColor = Color.Red,
                                    onClick = {
                                        showBottomSheet = false
                                        navController.navigate("detalleInformes/${camara.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }


            if (showModalCamaras) {
                CamaraModal(listaCamaras, navController) {
                    showModalCamaras = false
                }
            }


        }
    }

}


@Composable
fun CamaraModal(camaras: List<Camara>, navController: NavController, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextDialog(text = "Resultados")

                if (camaras.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No hay resultados", color = Color.Black,)
                    }
                } else {
                    camaras.forEach { camara ->

                        Box(modifier = Modifier.background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        onDismiss()
                                        navController.navigate("detalleInformes/${camara.id}")
                                    }
                            ) {
                                TextDetailOne(text = "Nombre: ${camara.nombre}")
                                Spacer(modifier = Modifier.height(4.dp))
                                TextDetailOne(text = "Codigo: ${camara.codigo}")
                            }
                        }

                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    SecondaryButton(
                        text = "Cerrar",
                        onClick = onDismiss
                    )
                }


            }
        }
    }
}