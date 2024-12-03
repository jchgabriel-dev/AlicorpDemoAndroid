package com.example.alicorpdemo

import PisoViewModel
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alicorpdemo.components.HeaderText
import com.example.alicorpdemo.components.InputCreate
import com.example.alicorpdemo.components.LongButton
import com.example.alicorpdemo.components.MainColumn
import com.example.alicorpdemo.components.TextTitle
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID


fun guardarImagenEnAlmacenamientoInterno(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val nombreArchivo = generarNombreImagenUnico() // Genera un nombre único para cada imagen
        val archivoSalida = File(context.filesDir, nombreArchivo)

        inputStream?.use { entrada ->
            archivoSalida.outputStream().use { salida ->
                entrada.copyTo(salida)
            }
        }
        archivoSalida.absolutePath // Retorna la ruta persistente
    } catch (e: Exception) {
        Log.e("GuardarImagen", "Error copiando la imagen: ${e.message}")
        null
    }
}

fun generarNombreImagenUnico(): String {
    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
    val timestamp = sdf.format(Date()) // Fecha y hora actual
    return "imagen_${timestamp}_${UUID.randomUUID()}.jpg" // Nombre único con timestamp y UUID
}

@Composable
fun PisoScreen(viewModel: PisoViewModel, navController: NavController, context: Context) {
    val pisos by viewModel.pisos.observeAsState(emptyList())
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf<Uri?>(null) }

    // Estados para verificar si los campos están completos
    var nombreError by remember { mutableStateOf(false) }
    var descripcionError by remember { mutableStateOf(false) }
    var imagenError by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val rutaPersistente = guardarImagenEnAlmacenamientoInterno(context, it)
            if (rutaPersistente != null) {
                imagen = Uri.fromFile(File(rutaPersistente))
            } else {
                Log.e("ImagePicker", "Error al guardar la imagen seleccionada")
            }
        }
    }

    MainColumn() {
        HeaderText(texto = "Gestion de pisos", navController = navController)
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            TextTitle( text = "Listado de Pisos", icon = painterResource(id = R.drawable.baseline_add_home_work_24),)

            LongButton(
                text = "Agregar piso",
                buttonColor = Color.Black,
                onClick = {
                    navController.navigate("crearPiso")
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (pisos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(330.dp)
                        .fillMaxWidth()
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay pisos disponibles",
                        color = Color.Black,

                        )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(pisos) { piso ->
                        PisoItem(piso = piso, navController = navController) // Asegúrate de pasar el navController aquí
                    }
                }
            }

        }



    }
}
@Composable
fun PisoItem(piso: Piso, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray)
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("detalleCamaras/${piso.id}")
            }
    ) {
        Text(
            text = "Nombre: ${piso.nombre}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(text = "Descripción: ${piso.descripcion}")
        Spacer(modifier = Modifier.height(8.dp))
        piso.imagen?.let { imageUrl ->
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = "Imagen de ${piso.nombre}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } ?: Text(text = "No hay imagen disponible.", color = Color.Red)
    }
}