package com.smarttable.app.ui.operacion


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.smarttable.app.nfc.EstadoNfc
import com.smarttable.app.nfc.NfcEventBus


@Composable
fun NfcStatusCard() {

    val estado by
    NfcEventBus
        .estado
        .collectAsState()


    val fondo =
        when (
            estado
        ) {

            EstadoNfc.ACTIVO ->
                Color(
                    0xFFECFDF5
                )

            EstadoNfc.DESACTIVADO ->
                Color(
                    0xFFFFF7ED
                )

            EstadoNfc.NO_DISPONIBLE ->
                Color(
                    0xFFF1F5F9
                )
        }


    val texto =
        when (
            estado
        ) {

            EstadoNfc.ACTIVO ->
                Color(
                    0xFF047857
                )

            EstadoNfc.DESACTIVADO ->
                Color(
                    0xFFC2410C
                )

            EstadoNfc.NO_DISPONIBLE ->
                Color(
                    0xFF64748B
                )
        }


    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    color =
                        fondo,

                    shape =
                        RoundedCornerShape(
                            16.dp
                        )
                )
                .padding(
                    14.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                "📡",

            fontSize =
                25.sp
        )


        Spacer(
            modifier =
                Modifier.width(
                    12.dp
                )
        )


        Column {

            Text(
                text =
                    when (
                        estado
                    ) {

                        EstadoNfc.ACTIVO ->
                            "SmartTable NFC activo"

                        EstadoNfc.DESACTIVADO ->
                            "NFC desactivado"

                        EstadoNfc.NO_DISPONIBLE ->
                            "NFC no disponible"
                    },

                color =
                    texto,

                fontWeight =
                    FontWeight.Bold,

                fontSize =
                    14.sp
            )


            Text(
                text =
                    when (
                        estado
                    ) {

                        EstadoNfc.ACTIVO ->
                            "Acerca el teléfono a una mesa para operarla."

                        EstadoNfc.DESACTIVADO ->
                            "Activa NFC en el teléfono. El mapa sigue disponible."

                        EstadoNfc.NO_DISPONIBLE ->
                            "Puedes continuar operando las mesas desde el mapa."
                    },

                modifier =
                    Modifier.padding(
                        top = 2.dp
                    ),

                color =
                    texto.copy(
                        alpha =
                            0.8f
                    ),

                fontSize =
                    11.sp
            )
        }
    }
}