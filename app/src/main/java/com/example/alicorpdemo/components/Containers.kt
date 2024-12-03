package com.example.alicorpdemo.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainColumn(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(0.dp)
            .statusBarsPadding()
    ) {
        content()
    }
}

@Composable
fun TextDialog(
    text: String
) {
    Text(text = text, color = Color.Black, fontSize = 20.sp)
}

@Composable
fun InputCreate(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean

) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = if (isError) Color.Red else Color.Black,
            unfocusedLabelColor = if (isError) Color.Red else Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            focusedLabelColor = if (isError) Color.Red else Color.Black,
            focusedBorderColor = if (isError) Color.Red else Color.Black,
            cursorColor = if (isError) Color.Red else Color.Black
        )
    )
}