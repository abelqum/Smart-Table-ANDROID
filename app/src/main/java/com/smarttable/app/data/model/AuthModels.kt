package com.smarttable.app.data.model


data class LoginRequest(
    val correo: String,
    val contrasena: String
)


data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    val rol: String
)


data class LoginResponse(
    val token: String,
    val usuario: Usuario
)