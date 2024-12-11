package com.example.alicorpdemo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MainColumn(
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Black
        )
    }

    Column(
        modifier = Modifier
            .padding(0.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        content()
    }
}

@Composable
fun TextDialog(
    text: String
) {
    Text(text = text, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
}


@Composable
fun TextDetail(
    text: String
) {
    Text(text = text, color = Color.Black, fontSize = 18.sp)
}

@Composable
fun TextDetailOne(
    text: String
) {
    Text(
        text = text,
        color = Color.Black,
        fontSize = 18.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun TextTitle(
    text: String,
    icon: Painter
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 5.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = "Item",
            tint = Color.Black,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        TextDialog(
            text = text
        )
    }
}

@Composable
fun TextTitleAlternative(
    text: String,
    icon: Painter
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 5.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = "Item",
            tint = Color.Black,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        TextDialog(
            text = text
        )
    }
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


@Composable
fun InputShow(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    val hasText = value.isNotEmpty()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .padding(bottom = 4.dp)
            .background(Color.Transparent),

        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.DarkGray,
            unfocusedBorderColor = Color.Black,
            unfocusedTextColor = Color.White,
            unfocusedLabelColor = if (hasText) Color.Black else Color.White,

            focusedContainerColor = Color.DarkGray,
            focusedBorderColor = Color.Black,
            focusedTextColor = Color.White,
            focusedLabelColor =  Color.Black,

            )


    )
}


@Composable
fun InputText(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().height(150.dp) ,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor =  Color.Black,
            unfocusedLabelColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            focusedLabelColor = Color.Black,
            focusedBorderColor = Color.Black,
            cursorColor = Color.Black
        ),
        maxLines = 5,
    )
}


@Composable
fun InputPassword(
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
        visualTransformation = PasswordVisualTransformation(),
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
