package com.smarttable.app.nfc


import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow


enum class EstadoNfc {

    ACTIVO,
    DESACTIVADO,
    NO_DISPONIBLE
}


data class SolicitudEscrituraNfc(

    val etiquetaId: Int,

    val token: String,

    val contenido: String
)


sealed interface EventoNfc {

    data class TokenDetectado(
        val token: String
    ) : EventoNfc


    data class EscrituraExitosa(

        val etiquetaId: Int,

        val token: String

    ) : EventoNfc


    data class ErrorEscritura(

        val etiquetaId: Int,

        val mensaje: String

    ) : EventoNfc


    data class Error(
        val mensaje: String
    ) : EventoNfc
}


object NfcEventBus {

    private val _eventos =
        MutableSharedFlow<EventoNfc>(
            extraBufferCapacity =
                20
        )


    val eventos:
            SharedFlow<EventoNfc> =
        _eventos
            .asSharedFlow()


    private val _estado =
        MutableStateFlow(
            EstadoNfc.NO_DISPONIBLE
        )


    val estado:
            StateFlow<EstadoNfc> =
        _estado
            .asStateFlow()


    private val _solicitudEscritura =
        MutableStateFlow<SolicitudEscrituraNfc?>(
            null
        )


    val solicitudEscritura:
            StateFlow<SolicitudEscrituraNfc?> =
        _solicitudEscritura
            .asStateFlow()


    fun publicarToken(
        token: String
    ) {

        /*
         * Mientras estamos programando una etiqueta,
         * las lecturas normales quedan suspendidas.
         */
        if (
            _solicitudEscritura.value !=
            null
        ) {

            return
        }


        _eventos.tryEmit(
            EventoNfc.TokenDetectado(
                token
            )
        )
    }


    fun prepararEscritura(
        etiquetaId: Int,
        token: String,
        contenido: String
    ) {

        _solicitudEscritura.value =
            SolicitudEscrituraNfc(
                etiquetaId =
                    etiquetaId,

                token =
                    token,

                contenido =
                    contenido
            )
    }


    fun publicarEscrituraExitosa(
        solicitud: SolicitudEscrituraNfc
    ) {

        _solicitudEscritura.value =
            null


        _eventos.tryEmit(
            EventoNfc.EscrituraExitosa(
                etiquetaId =
                    solicitud.etiquetaId,

                token =
                    solicitud.token
            )
        )
    }


    fun publicarErrorEscritura(
        solicitud: SolicitudEscrituraNfc,
        mensaje: String
    ) {

        /*
         * NO eliminamos la solicitud.
         *
         * El administrador puede volver a acercar
         * la etiqueta y reintentar inmediatamente.
         */
        _eventos.tryEmit(
            EventoNfc.ErrorEscritura(
                etiquetaId =
                    solicitud.etiquetaId,

                mensaje =
                    mensaje
            )
        )
    }


    fun cancelarEscritura() {

        _solicitudEscritura.value =
            null
    }


    fun publicarError(
        mensaje: String
    ) {

        _eventos.tryEmit(
            EventoNfc.Error(
                mensaje
            )
        )
    }


    fun actualizarEstado(
        estado: EstadoNfc
    ) {

        _estado.value =
            estado
    }
}