package com.smarttable.app.ui.nfc


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.window.Dialog

import androidx.lifecycle.viewmodel.compose.viewModel

import com.smarttable.app.data.model.Mesa


@Composable
fun VinculacionNfcDialog(
    mesa: Mesa,
    onCerrar: () -> Unit,
    viewModel: VinculacionNfcViewModel =
        viewModel()
) {

    val estado by
    viewModel
        .estado
        .collectAsState()


    LaunchedEffect(
        mesa.id
    ) {

        viewModel
            .iniciar(
                mesa
            )
    }


    Dialog(
        onDismissRequest = {

            if (
                !estado.confirmando
            ) {

                viewModel
                    .cancelar()


                onCerrar()
            }
        }
    ) {

        Surface(
            modifier =
                Modifier.fillMaxWidth(),

            color =
                Color.White,

            shape =
                RoundedCornerShape(
                    28.dp
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(
                        28.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text =
                        when {

                            estado.completada ->
                                "✓"

                            else ->
                                "📡"
                        },

                    fontSize =
                        56.sp
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                Text(
                    text =
                        "Mesa ${mesa.numero}",

                    fontSize =
                        26.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )


                when {

                    estado.preparando -> {

                        CircularProgressIndicator()


                        Spacer(
                            modifier =
                                Modifier.height(
                                    16.dp
                                )
                        )


                        Text(
                            text =
                                "Preparando una nueva etiqueta SmartTable..."
                        )
                    }


                    estado.esperandoEtiqueta -> {

                        Text(
                            text =
                                "Acerca una NTAG213",

                            fontWeight =
                                FontWeight.Bold,

                            fontSize =
                                21.sp,

                            color =
                                Color(
                                    0xFF059669
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )


                        Text(
                            text =
                                "Mantén la tarjeta o moneda NFC junto al teléfono hasta sentir la vibración.",

                            textAlign =
                                TextAlign.Center,

                            color =
                                Color(
                                    0xFF64748B
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    16.dp
                                )
                        )


                        Text(
                            text =
                                "No necesitas abrir NFC Tools ni copiar ningún código.",

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color =
                                            Color(
                                                0xFFECFDF5
                                            ),

                                        shape =
                                            RoundedCornerShape(
                                                14.dp
                                            )
                                    )
                                    .padding(
                                        12.dp
                                    ),

                            textAlign =
                                TextAlign.Center,

                            color =
                                Color(
                                    0xFF047857
                                ),

                            fontSize =
                                12.sp
                        )
                    }


                    estado.confirmando -> {

                        CircularProgressIndicator()


                        Spacer(
                            modifier =
                                Modifier.height(
                                    16.dp
                                )
                        )


                        Text(
                            text =
                                "Etiqueta escrita.\nConfirmando con SmartTable...",

                            textAlign =
                                TextAlign.Center
                        )
                    }


                    estado.completada -> {

                        Text(
                            text =
                                "NFC vinculado",

                            color =
                                Color(
                                    0xFF059669
                                ),

                            fontSize =
                                22.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )


                        Text(
                            text =
                                "La etiqueta ya identifica a la Mesa ${mesa.numero}.",

                            textAlign =
                                TextAlign.Center,

                            color =
                                Color(
                                    0xFF64748B
                                )
                        )
                    }
                }


                if (
                    estado.mensajeError !=
                    null
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    Text(
                        text =
                            estado.mensajeError!!,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    color =
                                        Color(
                                            0xFFFEF2F2
                                        ),

                                    shape =
                                        RoundedCornerShape(
                                            12.dp
                                        )
                                )
                                .padding(
                                    12.dp
                                ),

                        color =
                            Color(
                                0xFFB91C1C
                            ),

                        textAlign =
                            TextAlign.Center,

                        fontSize =
                            12.sp
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            24.dp
                        )
                )


                if (
                    estado.completada
                ) {

                    Button(
                        onClick =
                            onCerrar,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            "Listo"
                        )
                    }

                } else {

                    TextButton(
                        onClick = {

                            viewModel
                                .cancelar()


                            onCerrar()
                        },

                        enabled =
                            !estado.confirmando
                    ) {

                        Text(
                            "Cancelar"
                        )
                    }
                }
            }
        }
    }
}