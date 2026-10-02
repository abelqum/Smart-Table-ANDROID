package com.smarttable.app.ui.sesion


import com.smarttable.app.data.model.Usuario


sealed interface SesionUiState {

    data object Cargando :
        SesionUiState


    data object SinSesion :
        SesionUiState


    data class Autenticado(
        val usuario: Usuario
    ) : SesionUiState


    data class Error(
        val mensaje: String
    ) : SesionUiState
}