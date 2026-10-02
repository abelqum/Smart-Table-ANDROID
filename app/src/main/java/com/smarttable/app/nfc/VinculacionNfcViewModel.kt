package com.smarttable.app.ui.nfc


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.smarttable.app.data.model.Mesa
import com.smarttable.app.data.model.PrepararVinculacionNfcResponse
import com.smarttable.app.data.repository.NfcRepository

import com.smarttable.app.nfc.EventoNfc
import com.smarttable.app.nfc.NfcEventBus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class VinculacionNfcUiState(

    val preparando: Boolean =
        false,

    val esperandoEtiqueta: Boolean =
        false,

    val confirmando: Boolean =
        false,

    val completada: Boolean =
        false,

    val mensajeError: String? =
        null
)


class VinculacionNfcViewModel :
    ViewModel() {

    private val repositorio =
        NfcRepository()


    private val _estado =
        MutableStateFlow(
            VinculacionNfcUiState()
        )


    val estado:
            StateFlow<VinculacionNfcUiState> =
        _estado.asStateFlow()


    private var vinculacionPreparada:
            PrepararVinculacionNfcResponse? =
        null


    init {

        escucharEventosNfc()
    }


    fun iniciar(
        mesa: Mesa
    ) {

        if (
            _estado.value.preparando ||
            _estado.value.esperandoEtiqueta ||
            _estado.value.confirmando
        ) {

            return
        }


        viewModelScope.launch {

            _estado.value =
                VinculacionNfcUiState(
                    preparando =
                        true
                )


            try {

                val respuesta =
                    repositorio
                        .prepararVinculacion(
                            mesa.id
                        )


                vinculacionPreparada =
                    respuesta


                NfcEventBus
                    .prepararEscritura(

                        etiquetaId =
                            respuesta.etiquetaId,

                        token =
                            respuesta.token,

                        contenido =
                            respuesta.contenido
                    )


                _estado.value =
                    VinculacionNfcUiState(
                        esperandoEtiqueta =
                            true
                    )

            } catch (
                error: Exception
            ) {

                _estado.value =
                    VinculacionNfcUiState(
                        mensajeError =
                            error.message
                                ?: "No fue posible preparar la etiqueta NFC."
                    )
            }
        }
    }


    private fun escucharEventosNfc() {

        viewModelScope.launch {

            NfcEventBus
                .eventos
                .collect {
                        evento ->


                    when (
                        evento
                    ) {

                        is EventoNfc.EscrituraExitosa -> {

                            confirmarEscritura(
                                evento
                            )
                        }


                        is EventoNfc.ErrorEscritura -> {

                            val vinculacion =
                                vinculacionPreparada
                                    ?: return@collect


                            if (
                                evento.etiquetaId !=
                                vinculacion.etiquetaId
                            ) {

                                return@collect
                            }


                            _estado.value =
                                _estado.value.copy(
                                    esperandoEtiqueta =
                                        true,

                                    mensajeError =
                                        evento.mensaje
                                )
                        }


                        else -> {
                        }
                    }
                }
        }
    }


    private fun confirmarEscritura(
        evento: EventoNfc.EscrituraExitosa
    ) {

        val vinculacion =
            vinculacionPreparada
                ?: return


        if (
            evento.etiquetaId !=
            vinculacion.etiquetaId
        ) {

            return
        }


        viewModelScope.launch {

            _estado.value =
                VinculacionNfcUiState(
                    confirmando =
                        true
                )


            try {

                repositorio
                    .confirmarVinculacion(
                        etiquetaId =
                            evento.etiquetaId,

                        token =
                            evento.token
                    )


                vinculacionPreparada =
                    null


                _estado.value =
                    VinculacionNfcUiState(
                        completada =
                            true
                    )

            } catch (
                error: Exception
            ) {

                _estado.value =
                    VinculacionNfcUiState(
                        mensajeError =
                            error.message
                                ?: "La etiqueta fue escrita, pero no pudo confirmarse en SmartTable."
                    )
            }
        }
    }


    fun cancelar() {

        val vinculacion =
            vinculacionPreparada


        NfcEventBus
            .cancelarEscritura()


        vinculacionPreparada =
            null


        _estado.value =
            VinculacionNfcUiState()


        if (
            vinculacion ==
            null
        ) {

            return
        }


        viewModelScope.launch {

            try {

                repositorio
                    .cancelarVinculacion(
                        vinculacion.etiquetaId
                    )

            } catch (
                _: Exception
            ) {

                /*
                 * La vinculación pendiente está
                 * inactiva, así que una falla al
                 * limpiarla no compromete operación.
                 */
            }
        }
    }
}