package com.example.alicorpdemo

import PisoViewModel
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.alicorpdemo.components.ActionButton
import com.example.alicorpdemo.components.HeaderText
import com.example.alicorpdemo.components.InputCreate
import com.example.alicorpdemo.components.LongButton
import com.example.alicorpdemo.components.MainColumn
import com.example.alicorpdemo.database.Piso
import java.io.File


@Composable
fun PisoCreateScreen(viewModel: PisoViewModel, navController: NavController, context: Context) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf<Uri?>(null) }

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
                imagenError = (imagen == null)
            } else {
                Log.e("ImagePicker", "Error al guardar la imagen seleccionada")
            }
        }
    }

    MainColumn() {
        HeaderText(texto = "Registros de pisos", navController = navController)

        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
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

            OutlinedTextField(
                value = imagen?.toString() ?: "",
                onValueChange = {},
                label = { Text("Imagen URL (selecciona una imagen)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (imagenError) Color.Red else Color.Black,
                    disabledBorderColor = if (imagenError) Color.Red else Color.Black,
                    disabledTextColor = if (imagenError) Color.Red else Color.Black,
                    disabledLabelColor = if (imagenError) Color.Red else Color.Black,
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ActionButton(
                    text = "Seleccionar Imagen",
                    onClick = { imagePickerLauncher.launch("image/*") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray)

            ) {
                imagen?.let {
                    val archivo = File(it.path!!)
                    if (archivo.exists()) {
                        Image(
                            painter = rememberImagePainter(archivo),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "No se pudo cargar la imagen.",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } ?: Text(
                    text = "No hay imagen seleccionada",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            LongButton(
                text = "Agregar piso",
                buttonColor = Color.Black,
                onClick = {
                    if (nombre.isEmpty() || descripcion.isEmpty() || imagen == null) {
                        nombreError = nombre.isEmpty()
                        descripcionError = descripcion.isEmpty()
                        imagenError = imagen == null
                    } else {
                        viewModel.agregarPiso(nombre, descripcion, imagen.toString())
                        nombre = ""
                        descripcion = ""
                        imagen = null
                        navController.popBackStack()
                    }
                },
            )

        }

    }
}
