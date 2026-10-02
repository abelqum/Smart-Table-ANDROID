package com.smarttable.app.ui.sesion


import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.smarttable.app.data.repository.AuthRepository
import com.smarttable.app.data.session.SecureSessionManager

import com.smarttable.app.realtime.RealtimeManager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import retrofit2.HttpException

import java.io.IOException


class SesionViewModel(
    application: Application
) : AndroidViewModel(
    application
) {

    private val repositorio =
        AuthRepository()


    private val sesion =
        SecureSessionManager(
            application
        )


    private val _estado =
        MutableStateFlow<SesionUiState>(
            SesionUiState.Cargando
        )


    val estado:
            StateFlow<SesionUiState> =
        _estado.asStateFlow()


    private val _iniciandoSesion =
        MutableStateFlow(
            false
        )


    val iniciandoSesion:
            StateFlow<Boolean> =
        _iniciandoSesion.asStateFlow()


    init {

        verificarSesion()
    }


    fun verificarSesion() {

        viewModelScope.launch {

            val token =
                sesion.obtenerToken()


            if (
                token == null
            ) {

                RealtimeManager
                    .desconectar()


                _estado.value =
                    SesionUiState.SinSesion


                return@launch
            }


            try {

                /*
                 * Primero comprobamos que el JWT
                 * siga siendo válido.
                 */
                val usuario =
                    repositorio
                        .obtenerPerfil()


                /*
                 * Solamente después conectamos
                 * Socket.IO con ese JWT.
                 */
                RealtimeManager
                    .conectar(
                        token
                    )


                _estado.value =
                    SesionUiState.Autenticado(
                        usuario
                    )

            } catch (
                error: Exception
            ) {

                RealtimeManager
                    .desconectar()


                sesion.cerrarSesion()


                _estado.value =
                    SesionUiState.SinSesion
            }
        }
    }


    fun iniciarSesion(
        correo: String,
        contrasena: String
    ) {

        if (
            _iniciandoSesion.value
        ) {

            return
        }


        viewModelScope.launch {

            _iniciandoSesion.value =
                true


            try {

                val respuesta =
                    repositorio
                        .iniciarSesion(
                            correo,
                            contrasena
                        )


                /*
                 * Guardamos primero el JWT.
                 */
                sesion.guardarToken(
                    respuesta.token
                )


                /*
                 * Después conectamos tiempo real
                 * usando exactamente ese JWT.
                 */
                RealtimeManager
                    .conectar(
                        respuesta.token
                    )


                _estado.value =
                    SesionUiState.Autenticado(
                        respuesta.usuario
                    )

            } catch (
                error: HttpException
            ) {

                val mensaje =
                    when (
                        error.code()
                    ) {

                        401 ->
                            "Correo o contraseña incorrectos."

                        403 ->
                            "La cuenta se encuentra desactivada."

                        else ->
                            "No se pudo iniciar sesión."
                    }


                _estado.value =
                    SesionUiState.Error(
                        mensaje
                    )

            } catch (
                error: IOException
            ) {

                error.printStackTrace()


                _estado.value =
                    SesionUiState.Error(
                        "Error de conexión: ${
                            error.message
                                ?: "No se recibió respuesta del servidor."
                        }"
                    )

            } catch (
                error: Exception
            ) {

                error.printStackTrace()


                _estado.value =
                    SesionUiState.Error(
                        error.message
                            ?: "Ocurrió un error inesperado."
                    )

            } finally {

                _iniciandoSesion.value =
                    false
            }
        }
    }


    fun regresarLogin() {

        _estado.value =
            SesionUiState.SinSesion
    }


    fun cerrarSesion() {

        /*
         * Cerramos primero la conexión realtime
         * para que el usuario deje inmediatamente
         * de recibir eventos.
         */
        RealtimeManager
            .desconectar()


        sesion.cerrarSesion()


        _estado.value =
            SesionUiState.SinSesion
    }


    override fun onCleared() {

        super.onCleared()

        /*
         * No desconectamos aquí porque este
         * ViewModel puede recrearse por cambios
         * de configuración mientras la sesión
         * sigue siendo válida.
         */
    }
}