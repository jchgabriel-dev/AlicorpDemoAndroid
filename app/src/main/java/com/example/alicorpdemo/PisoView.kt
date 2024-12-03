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
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

    Column(modifier = Modifier.padding(0.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .background(Color.Red)
        ) {
            Text(
                text = "Gestion de Pisos",
                color = Color.White,
                modifier = Modifier
                    .padding(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = it.isEmpty() // Actualiza el error cuando el campo cambia
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (nombreError) Color.Red else Color.Black,
                    unfocusedLabelColor = if (nombreError) Color.Red else Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                )
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                    descripcionError = it.isEmpty() // Actualiza el error cuando el campo cambia
                },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (descripcionError) Color.Red else Color.Black,
                    unfocusedLabelColor = if (descripcionError) Color.Red else Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                )
            )

            OutlinedTextField(
                value = imagen?.toString() ?: "",
                onValueChange = { },
                label = { Text("Imagen URL (selecciona una imagen)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (imagenError) Color.Red else Color.Black,
                    disabledBorderColor = if (imagenError) Color.Red else Color.Black,
                    disabledTextColor = if (imagenError) Color.Red else Color.Black,
                    disabledLabelColor = if (imagenError) Color.Red else Color.Black,
                )
            )

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                )
            ) {
                Text("Seleccionar Imagen", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            imagen?.let {
                val archivo = File(it.path!!)
                if (archivo.exists()) {
                    Image(
                        painter = rememberImagePainter(archivo),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.fillMaxWidth()
                            .height(200.dp),
                    )
                } else {
                    Text("No se pudo cargar la imagen.", color = Color.Red)
                }
            }

            Button(
                onClick = {
                    if (nombre.isEmpty() || descripcion.isEmpty() || imagen == null) {
                        // Si algún campo está vacío, mostramos error
                        nombreError = nombre.isEmpty()
                        descripcionError = descripcion.isEmpty()
                        imagenError = imagen == null
                    } else {
                        // Si todos los campos están completos, agregamos el piso
                        viewModel.agregarPiso(nombre, descripcion, imagen.toString()) // Pasa la URL de la imagen
                        nombre = ""
                        descripcion = ""
                        imagen = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = "Agregar Piso",
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = "Listado de Pisos",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            modifier = Modifier.padding(20.dp),
        )



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
                    text = "No hay informes disponibles",
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
        Text(text = "Imagen URL: ${piso.imagen}")
    }
}