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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.alicorpdemo.components.HeaderText
import com.example.alicorpdemo.components.LongButton
import com.example.alicorpdemo.components.MainColumn
import com.example.alicorpdemo.components.OptionButton
import com.example.alicorpdemo.components.TextTitle
import com.example.alicorpdemo.components.TextTitleAlternative
import com.example.alicorpdemo.database.Informe
import com.example.alicorpdemo.database.Piso

@Composable
fun InformeScreen(camaraId: Int, viewModel: InformeViewModel, viewModelCamara: CamaraViewModel , navController: NavController) {
    val informes by viewModel.obtenerInformesPorCamara(camaraId).observeAsState(emptyList())
    val camara by viewModelCamara.obtenerCamaraPorId(camaraId).observeAsState()

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    MainColumn() {
        HeaderText(texto = camara?.nombre ?: "Equipo no encontrado", navController = navController)

        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row( horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                TextTitleAlternative( text = "Listado de informes", icon = painterResource(id = R.drawable.baseline_file_copy_24),)
                OptionButton(
                    icon = painterResource(id = R.drawable.baseline_build_24),
                    buttonColor = Color.Red,
                    onClick = { navController.navigate("updateCamara/${camara?.id}") },
                )
            }

            LongButton(
                text = "Agregar informe",
                buttonColor = Color.Black,
                onClick = {
                    navController.navigate("crearInforme/${camara?.id}")
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

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
}


@Composable
fun InformeItem(informe: Informe, navController: NavController) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.DarkGray)
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
            color = Color.White
        )
        Text(text = "Autor: ${informe.autor}", color = Color.White)
    }
}