package com.smarttable.app.ui.operacion
import com.smarttable.app.data.repository.NfcRepository

import com.smarttable.app.nfc.EventoNfc
import com.smarttable.app.nfc.NfcEventBus
import com.smarttable.app.realtime.RealtimeManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.smarttable.app.data.model.Mesa
import com.smarttable.app.data.model.TurnoMesa
import com.smarttable.app.data.repository.OperacionRepository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import org.json.JSONObject

import retrofit2.HttpException

import java.io.IOException


class OperacionViewModel :
    ViewModel() {

    private val repositorio =
        OperacionRepository()

    private val repositorioNfc =
        NfcRepository()

    private val _estado =
        MutableStateFlow(
            OperacionUiState()
        )


    val estado:
            StateFlow<OperacionUiState> =
        _estado.asStateFlow()

    init {

        escucharActualizacionesRealtime()
        escucharNfc()
    }

    private fun escucharActualizacionesRealtime() {

        viewModelScope.launch {

            RealtimeManager
                .actualizaciones
                .collect {
                        recursos ->


                    /*
                     * Cambió la distribución:
                     *
                     * necesitamos volver a obtener
                     * pisos y mesas.
                     */
                    if (
                        "pisos" in recursos
                    ) {

                        refrescarOperacionRealtime()

                        return@collect
                    }


                    /*
                     * Cambió una mesa:
                     *
                     * AVAILABLE
                     * OCCUPIED
                     * DIRTY
                     * CLEANING
                     * etc.
                     */
                    if (
                        "mesas" in recursos
                    ) {

                        refrescarMesasRealtime()
                    }


                    /*
                     * Crear/cancelar/editar un turno
                     * puede modificar la lista contextual
                     * de una mesa AVAILABLE.
                     */
                    if (
                        "turnos" in recursos
                    ) {

                        refrescarContextoSeleccionado()
                    }
                }
        }
    }
    fun cargarOperacion() {

        viewModelScope.launch {

            _estado.value =
                _estado.value.copy(
                    cargando =
                        true,

                    error =
                        null
                )


            try {

                val resultado =
                    coroutineScope {

                        val pisos =
                            async {
                                repositorio
                                    .obtenerPisos()
                            }


                        val mesas =
                            async {
                                repositorio
                                    .obtenerMesas()
                            }


                        Pair(
                            pisos.await(),
                            mesas.await()
                        )
                    }


                val pisos =
                    resultado.first


                val mesas =
                    resultado.second


                val pisoActual =
                    _estado.value
                        .pisoSeleccionadoId


                val pisoSeleccionado =
                    if (
                        pisoActual != null &&
                        pisos.any {
                            it.id ==
                                    pisoActual
                        }
                    ) {

                        pisoActual

                    } else {

                        pisos.firstOrNull()
                            ?.id
                    }


                _estado.value =
                    _estado.value.copy(
                        cargando =
                            false,

                        pisos =
                            pisos,

                        mesas =
                            mesas,

                        pisoSeleccionadoId =
                            pisoSeleccionado,

                        error =
                            null
                    )

            } catch (
                error: Exception
            ) {

                _estado.value =
                    _estado.value.copy(
                        cargando =
                            false,

                        error =
                            obtenerMensajeError(
                                error
                            )
                    )
            }
        }
    }


    fun seleccionarPiso(
        pisoId: Int
    ) {

        _estado.value =
            _estado.value.copy(
                pisoSeleccionadoId =
                    pisoId,

                mesaSeleccionadaId =
                    null,

                contextoMesa =
                    null
            )
    }


    fun seleccionarMesa(
        mesa: Mesa,
        rolUsuario: String
    ) {

        _estado.value =
            _estado.value.copy(
                mesaSeleccionadaId =
                    mesa.id,

                contextoMesa =
                    null,

                metodoSeleccionMesa =
                    "MANUAL",

                error =
                    null
            )


        val puedeAsignar =
            rolUsuario in listOf(
                "ADMIN",
                "HOSTESS",
                "WAITER"
            )


        if (
            mesa.estado ==
            "AVAILABLE" &&
            puedeAsignar
        ) {

            cargarContextoMesa(
                mesa.id
            )
        }
    }


    fun cerrarMesa() {

        _estado.value =
            _estado.value.copy(
                mesaSeleccionadaId =
                    null,

                contextoMesa =
                    null
            )
    }


    private fun cargarContextoMesa(
        mesaId: Int
    ) {

        viewModelScope.launch {

            _estado.value =
                _estado.value.copy(
                    cargandoContextoMesa =
                        true,

                    contextoMesa =
                        null
                )


            try {

                val contexto =
                    repositorio
                        .obtenerTurnosParaMesa(
                            mesaId
                        )


                _estado.value =
                    _estado.value.copy(
                        cargandoContextoMesa =
                            false,

                        contextoMesa =
                            contexto
                    )

            } catch (
                error: Exception
            ) {

                _estado.value =
                    _estado.value.copy(
                        cargandoContextoMesa =
                            false,

                        error =
                            obtenerMensajeError(
                                error
                            )
                    )
            }
        }
    }


    fun asignarTurno(
        mesa: Mesa,
        turno: TurnoMesa,
        rolUsuario: String
    ) {

        if (
            _estado.value
                .ejecutandoAccion
        ) {
            return
        }


        val excedeCapacidad =
            turno.personas >
                    mesa.capacidad


        if (
            excedeCapacidad &&
            rolUsuario !in listOf(
                "ADMIN",
                "HOSTESS"
            )
        ) {

            _estado.value =
                _estado.value.copy(
                    error =
                        "Tu rol no puede autorizar una excepción de capacidad."
                )

            return
        }


        viewModelScope.launch {

            _estado.value =
                _estado.value.copy(
                    ejecutandoAccion =
                        true,

                    error =
                        null
                )


            try {

                repositorio
                    .asignarTurnoAMesa(
                        mesa =
                            mesa,

                        turno =
                            turno,

                        permitirExcesoCapacidad =
                            excedeCapacidad
                    )


                refrescarMesas()


                _estado.value =
                    _estado.value.copy(
                        ejecutandoAccion =
                            false,

                        contextoMesa =
                            null,

                        mensaje =
                            "Turno #${turno.numero} confirmado en Mesa ${mesa.numero}."
                    )

            } catch (
                error: Exception
            ) {

                _estado.value =
                    _estado.value.copy(
                        ejecutandoAccion =
                            false,

                        error =
                            obtenerMensajeError(
                                error
                            )
                    )
            }
        }
    }


    fun registrarSalidaClientes(
        mesa: Mesa
    ) {

        ejecutarAccionMesa(
            accion = {

                repositorio
                    .registrarSalidaClientes(
                        mesa.id
                    )
            },

            mensaje =
                "Mesa ${mesa.numero} quedó pendiente de limpieza."
        )
    }


    fun iniciarLimpieza(
        mesa: Mesa
    ) {

        ejecutarAccionMesa(
            accion = {

                repositorio
                    .iniciarLimpieza(
                        mesa.id
                    )
            },

            mensaje =
                "Limpieza iniciada en Mesa ${mesa.numero}."
        )
    }


    fun finalizarLimpieza(
        mesa: Mesa
    ) {

        ejecutarAccionMesa(
            accion = {

                repositorio
                    .finalizarLimpieza(
                        mesa.id
                    )
            },

            mensaje =
                "Mesa ${mesa.numero} disponible nuevamente."
        )
    }


    private fun ejecutarAccionMesa(
        accion:
        suspend () -> Mesa,

        mensaje: String
    ) {

        if (
            _estado.value
                .ejecutandoAccion
        ) {
            return
        }


        viewModelScope.launch {

            _estado.value =
                _estado.value.copy(
                    ejecutandoAccion =
                        true,

                    error =
                        null
                )


            try {

                accion()


                refrescarMesas()


                _estado.value =
                    _estado.value.copy(
                        ejecutandoAccion =
                            false,

                        contextoMesa =
                            null,

                        mensaje =
                            mensaje
                    )

            } catch (
                error: Exception
            ) {

                _estado.value =
                    _estado.value.copy(
                        ejecutandoAccion =
                            false,

                        error =
                            obtenerMensajeError(
                                error
                            )
                    )
            }
        }
    }

    private suspend fun refrescarOperacionRealtime() {

        try {

            val resultado =
                coroutineScope {

                    val pisos =
                        async {
                            repositorio
                                .obtenerPisos()
                        }


                    val mesas =
                        async {
                            repositorio
                                .obtenerMesas()
                        }


                    Pair(
                        pisos.await(),
                        mesas.await()
                    )
                }


            val pisos =
                resultado.first


            val mesas =
                resultado.second


            val pisoActual =
                _estado.value
                    .pisoSeleccionadoId


            val pisoSeleccionado =
                if (
                    pisoActual != null &&
                    pisos.any {
                        it.id ==
                                pisoActual
                    }
                ) {

                    pisoActual

                } else {

                    pisos.firstOrNull()
                        ?.id
                }


            _estado.value =
                _estado.value.copy(
                    pisos =
                        pisos,

                    mesas =
                        mesas,

                    pisoSeleccionadoId =
                        pisoSeleccionado
                )


            refrescarContextoSeleccionado()

        } catch (
            error: Exception
        ) {

            /*
             * Una actualización realtime fallida
             * no tumba la pantalla.
             *
             * El usuario conserva los últimos datos
             * válidos que ya tenía.
             */
        }
    }


    private suspend fun refrescarMesasRealtime() {

        try {

            val mesas =
                repositorio
                    .obtenerMesas()


            _estado.value =
                _estado.value.copy(
                    mesas =
                        mesas
                )


            /*
             * Si después del cambio la mesa seleccionada
             * dejó de ser AVAILABLE, ya no necesitamos
             * conservar una lista de candidatos vieja.
             */
            val mesaSeleccionada =
                mesas.firstOrNull {
                    it.id ==
                            _estado.value
                                .mesaSeleccionadaId
                }


            if (
                mesaSeleccionada?.estado !=
                "AVAILABLE"
            ) {

                _estado.value =
                    _estado.value.copy(
                        contextoMesa =
                            null,

                        cargandoContextoMesa =
                            false
                    )
            }


            refrescarContextoSeleccionado()

        } catch (
            error: Exception
        ) {

            /*
             * Conservamos los últimos datos válidos.
             */
        }
    }


    private suspend fun refrescarContextoSeleccionado() {

        /*
         * Sólo refrescamos la lista contextual
         * si realmente existía una lista abierta.
         *
         * Esto evita que CLEANING consulte un
         * endpoint de turnos que no necesita.
         */
        val contextoActual =
            _estado.value
                .contextoMesa
                ?: return


        val mesa =
            _estado.value
                .mesas
                .firstOrNull {
                    it.id ==
                            contextoActual
                                .mesa
                                .id
                }
                ?: return


        if (
            mesa.estado !=
            "AVAILABLE"
        ) {

            _estado.value =
                _estado.value.copy(
                    contextoMesa =
                        null
                )


            return
        }


        try {

            val contextoNuevo =
                repositorio
                    .obtenerTurnosParaMesa(
                        mesa.id
                    )


            _estado.value =
                _estado.value.copy(
                    contextoMesa =
                        contextoNuevo
                )

        } catch (
            error: Exception
        ) {

            /*
             * No mostramos un error global por una
             * actualización silenciosa de Socket.IO.
             */
        }
    }
    private suspend fun refrescarMesas() {

        val mesas =
            repositorio
                .obtenerMesas()


        _estado.value =
            _estado.value.copy(
                mesas =
                    mesas
            )
    }


    fun limpiarMensaje() {

        _estado.value =
            _estado.value.copy(
                mensaje =
                    null
            )
    }


    fun limpiarError() {

        _estado.value =
            _estado.value.copy(
                error =
                    null
            )
    }


    private fun obtenerMensajeError(
        error: Exception
    ): String {

        if (
            error is HttpException
        ) {

            return try {

                val contenido =
                    error
                        .response()
                        ?.errorBody()
                        ?.string()


                if (
                    contenido.isNullOrBlank()
                ) {

                    "El servidor devolvió un error ${error.code()}."

                } else {

                    JSONObject(
                        contenido
                    )
                        .optString(
                            "mensaje",
                            "El servidor devolvió un error ${error.code()}."
                        )
                }

            } catch (
                errorLectura: Exception
            ) {

                "El servidor devolvió un error ${error.code()}."
            }
        }


        if (
            error is IOException
        ) {

            return "No fue posible conectarse con SmartTable."
        }


        return error.message
            ?: "Ocurrió un error inesperado."
    }

    private fun escucharNfc() {

        viewModelScope.launch {

            NfcEventBus
                .eventos
                .collect {
                        evento ->


                    when (
                        evento
                    ) {

                        is EventoNfc.TokenDetectado -> {

                            resolverEtiquetaNfc(
                                evento.token
                            )
                        }


                        is EventoNfc.Error -> {

                            _estado.value =
                                _estado.value.copy(
                                    error =
                                        evento.mensaje
                                )
                        }


                        /*
                         * Estos eventos pertenecen al flujo
                         * administrativo de vinculación NFC.
                         *
                         * Los procesa VinculacionNfcViewModel,
                         * no OperacionViewModel.
                         */
                        is EventoNfc.EscrituraExitosa -> {
                            // No hacer nada aquí.
                        }


                        is EventoNfc.ErrorEscritura -> {
                            // No hacer nada aquí.
                        }
                    }
                }
        }
    }


    private suspend fun resolverEtiquetaNfc(
        token: String
    ) {

        if (
            _estado.value
                .resolviendoNfc
        ) {

            return
        }


        _estado.value =
            _estado.value.copy(
                resolviendoNfc =
                    true,

                error =
                    null
            )


        try {

            val respuesta =
                repositorioNfc
                    .resolver(
                        token
                    )


            val mesa =
                respuesta.mesa


            /*
             * Actualizamos también nuestra copia local
             * de esa mesa con la versión que acaba
             * de devolver PostgreSQL.
             */
            val mesasActualizadas =
                _estado.value
                    .mesas
                    .map {
                            mesaActual ->

                        if (
                            mesaActual.id ==
                            mesa.id
                        ) {

                            mesa

                        } else {

                            mesaActual
                        }
                    }


            _estado.value =
                _estado.value.copy(
                    mesas =
                        mesasActualizadas,

                    pisoSeleccionadoId =
                        mesa.pisoId,

                    mesaSeleccionadaId =
                        mesa.id,

                    metodoSeleccionMesa =
                        "NFC",

                    contextoMesa =
                        null,

                    resolviendoNfc =
                        false,

                    mensaje =
                        "📡 NFC · Mesa ${mesa.numero} identificada"
                )


            /*
             * Si está AVAILABLE y el backend determinó
             * que el usuario puede confirmar un cliente,
             * cargamos inmediatamente los turnos.
             *
             * No hay otro toque intermedio.
             */
            if (
                mesa.estado ==
                "AVAILABLE" &&
                "CONFIRMAR_CLIENTE" in
                respuesta
                    .accionesPermitidas
            ) {

                cargarContextoMesa(
                    mesa.id
                )
            }

        } catch (
            error: Exception
        ) {

            _estado.value =
                _estado.value.copy(
                    resolviendoNfc =
                        false,

                    error =
                        obtenerMensajeError(
                            error
                        )
                )
        }
    }
}