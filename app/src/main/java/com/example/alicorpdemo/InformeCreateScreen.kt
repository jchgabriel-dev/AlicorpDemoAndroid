package com.example.alicorpdemo

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.PointF
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.alicorpdemo.database.Camara
import com.example.alicorpdemo.database.Informe
import java.io.File
import java.util.Calendar

@Composable
fun InformeCreateScreen(
    camaraId: Int,
    viewModel: InformeViewModel,
    viewModelCamara: CamaraViewModel,
    navController: NavController,
    context: Context
) {

    var autor by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var fechaError by remember { mutableStateOf(false) }
    var datePickerVisible by remember { mutableStateOf(false) }
    var observacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var autorError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(0.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .background(Color.Red)
        ) {
            Text(
                text = "Gestion de Informes",
                color = Color.White,
                modifier = Modifier
                    .padding(20.dp)
            )
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


        }



        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = autor,
                onValueChange = {
                    autor = it
                    autorError = it.isEmpty() // Actualiza el error cuando el campo cambia
                },
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (autorError) Color.Red else Color.Black,
                    unfocusedLabelColor = if (autorError) Color.Red else Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                )
            )




            OutlinedTextField(
                value = fecha,
                onValueChange = {
                    fecha = it
                    fechaError = it.isEmpty()
                                },
                label = { Text("Fecha") },
                modifier = Modifier
                    .fillMaxWidth()
                    ,
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (fechaError) Color.Red else Color.Black,
                    unfocusedLabelColor = if (fechaError) Color.Red else Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                )
            )

            Button(
                onClick = {
                    datePickerVisible = true
                },
                modifier = Modifier.padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    text = "Seleccionar Fecha",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            if (datePickerVisible) {
                val context = LocalContext.current
                val calendar = Calendar.getInstance()

                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        fecha = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year) // Formato de la fecha
                        fechaError = false
                        datePickerVisible = false // Reinicia el estado
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    setOnDismissListener {
                        datePickerVisible = false // Asegura que se reinicie incluso si el usuario cierra el diálogo manualmente
                    }
                }.show()
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripcion") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // Ajusta la altura para un campo más grande
                    .padding(vertical = 8.dp), // Agrega algo de espacio
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                ),
                maxLines = 5, // Permite hasta 5 líneas de texto
            )

            OutlinedTextField(
                value = observacion,
                onValueChange = { observacion = it },
                label = { Text("Observacion") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // Ajusta la altura para un campo más grande
                    .padding(vertical = 8.dp), // Agrega algo de espacio
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                ),
                maxLines = 5, // Permite hasta 5 líneas de texto
            )


            Button(
                onClick = {
                    if (autor.isEmpty() || fecha.isEmpty() ) {
                        autorError = autor.isEmpty()
                        fechaError = descripcion.isEmpty()
                    } else {
                        val newInforme = Informe(
                            autor = autor,
                            fecha = fecha,
                            descripcion = descripcion,
                            observacion = observacion,
                            camaraId = camaraId
                        )


                        viewModel.agregarInforme(newInforme)
                        navController.popBackStack()

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
                    text = "Agregar Informe",
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}