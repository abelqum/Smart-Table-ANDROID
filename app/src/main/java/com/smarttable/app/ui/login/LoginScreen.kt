package com.smarttable.app.ui.login


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val NegroPrincipal =
    Color(
        0xFF111827
    )


private val GrisTexto =
    Color(
        0xFF64748B
    )


private val VerdeSmartTable =
    Color(
        0xFF059669
    )


@Composable
fun LoginScreen(
    cargando: Boolean,
    mensajeError: String?,
    onIniciarSesion: (
        correo: String,
        contrasena: String
    ) -> Unit
) {

    var correo by
    remember {
        mutableStateOf(
            ""
        )
    }


    var contrasena by
    remember {
        mutableStateOf(
            ""
        )
    }


    var mostrarContrasena by
    remember {
        mutableStateOf(
            false
        )
    }


    val formularioValido =
        correo.isNotBlank() &&
                contrasena.isNotBlank()


    Box(

        modifier =
            Modifier
                .fillMaxSize()
                .background(

                    brush =
                        Brush.verticalGradient(

                            colors =
                                listOf(

                                    Color(
                                        0xFFF8FAFC
                                    ),

                                    Color(
                                        0xFFEEF4F9
                                    ),

                                    Color(
                                        0xFFE8F5F0
                                    )
                                )
                        )
                )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(

                        horizontal =
                            22.dp,

                        vertical =
                            30.dp
                    ),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            /*
             * =================================================
             * IDENTIDAD SMARTTABLE
             * =================================================
             */

            Surface(

                modifier =
                    Modifier.size(
                        72.dp
                    ),

                shape =
                    RoundedCornerShape(
                        22.dp
                    ),

                color =
                    NegroPrincipal,

                shadowElevation =
                    8.dp
            ) {

                Box(

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text =
                            "ST",

                        color =
                            Color.White,

                        fontSize =
                            24.sp,

                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }
            }


            Spacer(

                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            Text(

                text =
                    "SmartTable",

                color =
                    NegroPrincipal,

                fontSize =
                    32.sp,

                fontWeight =
                    FontWeight.ExtraBold
            )


            Spacer(

                modifier =
                    Modifier.height(
                        5.dp
                    )
            )


            Text(

                text =
                    "Operación inteligente de mesas",

                color =
                    GrisTexto,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Medium
            )


            Spacer(

                modifier =
                    Modifier.height(
                        30.dp
                    )
            )


            /*
             * =================================================
             * TARJETA LOGIN
             * =================================================
             */

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        26.dp
                    ),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            Color.White
                    ),

                elevation =
                    CardDefaults.cardElevation(

                        defaultElevation =
                            8.dp
                    )
            ) {

                Column(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                24.dp
                            )
                ) {

                    Text(

                        text =
                            "Bienvenido",

                        color =
                            NegroPrincipal,

                        fontSize =
                            24.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(

                        modifier =
                            Modifier.height(
                                5.dp
                            )
                    )


                    Text(

                        text =
                            "Inicia sesión para comenzar tu operación.",

                        color =
                            GrisTexto,

                        fontSize =
                            13.sp
                    )


                    Spacer(

                        modifier =
                            Modifier.height(
                                22.dp
                            )
                    )


                    /*
                     * =================================================
                     * CORREO
                     * =================================================
                     */

                    Text(

                        text =
                            "Correo electrónico",

                        color =
                            NegroPrincipal,

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )


                    Spacer(

                        modifier =
                            Modifier.height(
                                7.dp
                            )
                    )


                    OutlinedTextField(

                        value =
                            correo,

                        onValueChange = {

                            correo =
                                it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        placeholder = {

                            Text(

                                text =
                                    "nombre@smarttable.com",

                                color =
                                    Color(
                                        0xFF9CA3AF
                                    )
                            )
                        },

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),

                        colors =
                            coloresCampo()
                    )


                    Spacer(

                        modifier =
                            Modifier.height(
                                17.dp
                            )
                    )


                    /*
                     * =================================================
                     * CONTRASEÑA
                     * =================================================
                     */

                    Text(

                        text =
                            "Contraseña",

                        color =
                            NegroPrincipal,

                        fontSize =
                            13.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )


                    Spacer(

                        modifier =
                            Modifier.height(
                                7.dp
                            )
                    )


                    OutlinedTextField(

                        value =
                            contrasena,

                        onValueChange = {

                            contrasena =
                                it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        placeholder = {

                            Text(

                                text =
                                    "Ingresa tu contraseña",

                                color =
                                    Color(
                                        0xFF9CA3AF
                                    )
                            )
                        },

                        singleLine =
                            true,

                        visualTransformation =

                            if (
                                mostrarContrasena
                            ) {

                                VisualTransformation.None

                            } else {

                                PasswordVisualTransformation()
                            },

                        trailingIcon = {

                            TextButton(

                                onClick = {

                                    mostrarContrasena =
                                        !mostrarContrasena
                                }
                            ) {

                                Text(

                                    text =

                                        if (
                                            mostrarContrasena
                                        ) {

                                            "Ocultar"

                                        } else {

                                            "Ver"
                                        },

                                    color =
                                        NegroPrincipal,

                                    fontWeight =
                                        FontWeight.SemiBold,

                                    fontSize =
                                        12.sp
                                )
                            }
                        },

                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),

                        colors =
                            coloresCampo()
                    )


                    /*
                     * =================================================
                     * ERROR
                     * =================================================
                     */

                    if (
                        !mensajeError.isNullOrBlank()
                    ) {

                        Spacer(

                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )


                        Surface(

                            modifier =
                                Modifier.fillMaxWidth(),

                            color =
                                Color(
                                    0xFFFEF2F2
                                ),

                            shape =
                                RoundedCornerShape(
                                    12.dp
                                )
                        ) {

                            Text(

                                text =
                                    mensajeError,

                                modifier =
                                    Modifier.padding(
                                        12.dp
                                    ),

                                color =
                                    Color(
                                        0xFFB91C1C
                                    ),

                                fontSize =
                                    12.sp,

                                fontWeight =
                                    FontWeight.Medium
                            )
                        }
                    }


                    Spacer(

                        modifier =
                            Modifier.height(
                                24.dp
                            )
                    )


                    /*
                     * =================================================
                     * LOGIN
                     * =================================================
                     */

                    Button(

                        onClick = {

                            onIniciarSesion(

                                correo.trim(),

                                contrasena
                            )
                        },

                        enabled =
                            formularioValido &&
                                    !cargando,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    54.dp
                                ),

                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(

                                containerColor =
                                    NegroPrincipal,

                                contentColor =
                                    Color.White,

                                disabledContainerColor =
                                    Color(
                                        0xFFCBD5E1
                                    ),

                                disabledContentColor =
                                    Color.White
                            )
                    ) {

                        if (
                            cargando
                        ) {

                            CircularProgressIndicator(

                                modifier =
                                    Modifier.size(
                                        21.dp
                                    ),

                                color =
                                    Color.White,

                                strokeWidth =
                                    2.dp
                            )


                            Spacer(

                                modifier =
                                    Modifier.width(
                                        10.dp
                                    )
                            )


                            Text(

                                text =
                                    "Ingresando...",

                                fontWeight =
                                    FontWeight.SemiBold
                            )

                        } else {

                            Text(

                                text =
                                    "Iniciar sesión",

                                fontSize =
                                    15.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }


                    Spacer(

                        modifier =
                            Modifier.height(
                                20.dp
                            )
                    )


                    HorizontalDivider(

                        color =
                            Color(
                                0xFFE5E7EB
                            )
                    )


                    Spacer(

                        modifier =
                            Modifier.height(
                                18.dp
                            )
                    )


                    /*
                     * =================================================
                     * MENSAJE OPERATIVO
                     * =================================================
                     */

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(

                            modifier =
                                Modifier
                                    .size(
                                        9.dp
                                    )
                                    .background(

                                        color =
                                            VerdeSmartTable,

                                        shape =
                                            CircleShape
                                    )
                        )


                        Spacer(

                            modifier =
                                Modifier.width(
                                    8.dp
                                )
                        )


                        Text(

                            text =
                                "Sistema conectado",

                            color =
                                VerdeSmartTable,

                            fontWeight =
                                FontWeight.Bold,

                            fontSize =
                                11.sp
                        )
                    }


                    Spacer(

                        modifier =
                            Modifier.height(
                                7.dp
                            )
                    )


                    Text(

                        text =
                            "Acceso para administración, hostess, meseros y personal de limpieza.",

                        color =
                            GrisTexto,

                        fontSize =
                            11.sp,

                        lineHeight =
                            16.sp
                    )
                }
            }


            Spacer(

                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            /*
             * =================================================
             * PIE
             * =================================================
             */

            Text(

                text =
                    "SMARTTABLE · OPERACIÓN EN TIEMPO REAL",

                color =
                    Color(
                        0xFF94A3B8
                    ),

                fontSize =
                    10.sp,

                fontWeight =
                    FontWeight.Bold,

                letterSpacing =
                    1.sp,

                textAlign =
                    TextAlign.Center
            )
        }
    }
}


@Composable
private fun coloresCampo() =
    OutlinedTextFieldDefaults.colors(

        /*
         * Esto corrige precisamente el problema
         * que tenías: el contenido introducido
         * siempre es negro y claramente visible.
         */

        focusedTextColor =
            Color.Black,

        unfocusedTextColor =
            Color.Black,

        disabledTextColor =
            Color(
                0xFF64748B
            ),

        errorTextColor =
            Color.Black,


        focusedContainerColor =
            Color.White,

        unfocusedContainerColor =
            Color.White,

        disabledContainerColor =
            Color(
                0xFFF8FAFC
            ),

        errorContainerColor =
            Color.White,


        focusedBorderColor =
            Color(
                0xFF111827
            ),

        unfocusedBorderColor =
            Color(
                0xFFCBD5E1
            ),

        disabledBorderColor =
            Color(
                0xFFE2E8F0
            ),

        errorBorderColor =
            Color(
                0xFFDC2626
            ),


        cursorColor =
            Color(
                0xFF111827
            ),


        focusedTrailingIconColor =
            Color(
                0xFF111827
            ),

        unfocusedTrailingIconColor =
            Color(
                0xFF475569
            )
    )