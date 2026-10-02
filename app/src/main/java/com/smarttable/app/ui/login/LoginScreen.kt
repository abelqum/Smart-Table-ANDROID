package com.smarttable.app.ui.login


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LoginScreen(
    cargando: Boolean,
    mensajeError: String?,
    onIniciarSesion: (
        String,
        String
    ) -> Unit
) {

    var correo by remember {
        mutableStateOf("")
    }


    var contrasena by remember {
        mutableStateOf("")
    }


    val verdeSmartTable =
        Color(
            0xFF059669
        )


    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(
                        0xFFF8FAFC
                    )
                )
                .padding(
                    24.dp
                ),

        contentAlignment =
            Alignment.Center
    ) {

        Card(
            modifier =
                Modifier
                    .fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    24.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation =
                        4.dp
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(
                        24.dp
                    ),

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text =
                        "SmartTable",

                    fontSize =
                        30.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(
                            0xFF0F172A
                        )
                )


                Text(
                    text =
                        "Operación de restaurante",

                    modifier =
                        Modifier.padding(
                            top = 4.dp
                        ),

                    color =
                        Color(
                            0xFF64748B
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            28.dp
                        )
                )


                OutlinedTextField(
                    value =
                        correo,

                    onValueChange = {
                        correo = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Correo electrónico"
                        )
                    },

                    singleLine =
                        true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Email
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )


                OutlinedTextField(
                    value =
                        contrasena,

                    onValueChange = {
                        contrasena = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            "Contraseña"
                        )
                    },

                    singleLine =
                        true,

                    visualTransformation =
                        PasswordVisualTransformation(),

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Password
                        )
                )


                if (
                    mensajeError != null
                ) {

                    Text(
                        text =
                            mensajeError,

                        modifier =
                            Modifier.padding(
                                top = 14.dp
                            ),

                        color =
                            MaterialTheme
                                .colorScheme
                                .error,

                        fontSize =
                            13.sp
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            22.dp
                        )
                )


                Button(
                    onClick = {

                        onIniciarSesion(
                            correo,
                            contrasena
                        )
                    },

                    enabled =
                        !cargando &&
                                correo.isNotBlank() &&
                                contrasena.isNotBlank(),

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                52.dp
                            ),

                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                verdeSmartTable
                        )
                ) {

                    if (
                        cargando
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.height(
                                    22.dp
                                ),

                            color =
                                Color.White,

                            strokeWidth =
                                2.dp
                        )

                    } else {

                        Text(
                            text =
                                "Iniciar sesión",

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}