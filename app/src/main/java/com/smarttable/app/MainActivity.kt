package com.smarttable.app


import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.lifecycle.viewmodel.compose.viewModel

import com.smarttable.app.data.api.ApiClient

import com.smarttable.app.nfc.NfcReaderManager

import com.smarttable.app.ui.login.LoginScreen
import com.smarttable.app.ui.operacion.OperacionScreen
import com.smarttable.app.ui.sesion.SesionUiState
import com.smarttable.app.ui.sesion.SesionViewModel

import com.smarttable.app.ui.theme.SmartTableTheme


class MainActivity :
    ComponentActivity() {

    private lateinit var nfcReaderManager:
            NfcReaderManager


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        ApiClient.inicializar(
            applicationContext
        )


        nfcReaderManager =
            NfcReaderManager(
                this
            )


        enableEdgeToEdge()


        setContent {

            SmartTableTheme {

                SmartTableApp()
            }
        }
    }


    override fun onResume() {

        super.onResume()


        nfcReaderManager
            .activar()
    }


    override fun onPause() {

        nfcReaderManager
            .desactivar()


        super.onPause()
    }
}


@Composable
fun SmartTableApp(
    sesionViewModel:
    SesionViewModel =
        viewModel()
) {

    val estado by
    sesionViewModel
        .estado
        .collectAsState()


    val iniciandoSesion by
    sesionViewModel
        .iniciandoSesion
        .collectAsState()


    when (
        val estadoActual =
            estado
    ) {

        SesionUiState.Cargando -> {

            PantallaCargando()
        }


        SesionUiState.SinSesion -> {

            LoginScreen(
                cargando =
                    iniciandoSesion,

                mensajeError =
                    null,

                onIniciarSesion = {
                        correo,
                        contrasena ->

                    sesionViewModel
                        .iniciarSesion(
                            correo,
                            contrasena
                        )
                }
            )
        }


        is SesionUiState.Error -> {

            LoginScreen(
                cargando =
                    iniciandoSesion,

                mensajeError =
                    estadoActual.mensaje,

                onIniciarSesion = {
                        correo,
                        contrasena ->

                    sesionViewModel
                        .iniciarSesion(
                            correo,
                            contrasena
                        )
                }
            )
        }


        is SesionUiState.Autenticado -> {

            OperacionScreen(
                usuario =
                    estadoActual.usuario,

                onCerrarSesion = {

                    sesionViewModel
                        .cerrarSesion()
                }
            )
        }
    }
}


@Composable
private fun PantallaCargando() {

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(
                        0xFFF8FAFC
                    )
                ),

        contentAlignment =
            Alignment.Center
    ) {

        CircularProgressIndicator(
            color =
                Color(
                    0xFF059669
                )
        )
    }
}