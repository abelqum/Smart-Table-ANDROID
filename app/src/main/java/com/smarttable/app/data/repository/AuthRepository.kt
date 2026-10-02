package com.smarttable.app.data.repository


import com.smarttable.app.data.api.ApiClient
import com.smarttable.app.data.model.LoginRequest
import com.smarttable.app.data.model.LoginResponse
import com.smarttable.app.data.model.Usuario


class AuthRepository {

    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): LoginResponse {

        return ApiClient.api.iniciarSesion(
            LoginRequest(
                correo = correo.trim(),
                contrasena = contrasena
            )
        )
    }


    suspend fun obtenerPerfil(): Usuario {

        return ApiClient.api.obtenerPerfil()
    }
}