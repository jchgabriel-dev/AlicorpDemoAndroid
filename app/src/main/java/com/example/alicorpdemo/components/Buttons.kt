package com.example.alicorpdemo.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        ),
        onClick = onClick
    ) {
        Text(
            text = text,
            color = Color.White
        )
    }
}


@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red
        ),
        onClick = onClick
    ) {
        Text(
            text = text,
            color = Color.White
        )
    }
}

@Composable
fun LongButton(text: String, buttonColor: Color, onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        onClick = onClick
    ) {
        Text(
            text = text,
            color = Color.White
        )
    }
}


@Composable
fun OptionButton(
    onClick: () -> Unit,
    icon: Painter,
    buttonColor: Color
) {
    OutlinedButton(
        modifier= Modifier.size(48.dp),
        shape = CircleShape,
        border= BorderStroke(0.dp, buttonColor),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor =  buttonColor),
        onClick = onClick
    ) {
        Icon(
            painter = icon,
            contentDescription = "Regresar",
            tint = Color.White,
            modifier = Modifier
                .size(25.dp)
        )
    }
}


@Composable
fun HeaderText(
    texto: String,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .height(70.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

        ) {

            OutlinedButton(
                modifier= Modifier.size(40.dp),
                shape = CircleShape,
                border= BorderStroke(0.dp, Color.Red),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Red),
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Regresar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(35.dp)
                )
            }

            Text(
                text = texto,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(
                modifier = Modifier.width(48.dp)
            )
        }
    }
}