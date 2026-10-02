package com.smarttable.app.realtime


import android.util.Log

import io.socket.client.IO
import io.socket.client.Socket

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

import org.json.JSONObject

import java.net.URI


object RealtimeManager {

    /*
     * Gracias a:
     *
     * adb reverse tcp:4000 tcp:4000
     *
     * 127.0.0.1:4000 en Android
     * apunta al backend que corre en Windows.
     *
     * OJO:
     *
     * Socket.IO se conecta a la raíz:
     *
     * http://127.0.0.1:4000
     *
     * NO:
     *
     * http://127.0.0.1:4000/api
     */
    private const val SOCKET_URL =
        "http://127.0.0.1:4000"


    private var socket: Socket? =
        null


    private var tokenActual: String? =
        null


    private val _actualizaciones =
        MutableSharedFlow<Set<String>>(
            extraBufferCapacity =
                20
        )


    val actualizaciones:
            SharedFlow<Set<String>> =
        _actualizaciones
            .asSharedFlow()


    fun conectar(
        token: String
    ) {

        /*
         * Ya estamos conectados utilizando
         * exactamente la misma sesión.
         */
        if (
            socket?.connected() == true &&
            tokenActual == token
        ) {

            return
        }


        /*
         * Si cambió el JWT, destruimos la
         * conexión anterior para no mantener
         * una identidad equivocada.
         */
        desconectar()


        tokenActual =
            token


        try {

            val opciones =
                IO.Options
                    .builder()

                    /*
                     * Igual que la web:
                     *
                     * el JWT se manda dentro
                     * del objeto auth del
                     * handshake.
                     *
                     * Backend:
                     *
                     * socket.handshake.auth.token
                     */
                    .setAuth(
                        mapOf(
                            "token" to token
                        )
                    )

                    .setReconnection(
                        true
                    )

                    .setReconnectionAttempts(
                        Int.MAX_VALUE
                    )

                    .setReconnectionDelay(
                        1000
                    )

                    .setReconnectionDelayMax(
                        5000
                    )

                    .setTimeout(
                        10000
                    )

                    .build()


            val nuevoSocket =
                IO.socket(
                    URI.create(
                        SOCKET_URL
                    ),
                    opciones
                )


            nuevoSocket.on(
                Socket.EVENT_CONNECT
            ) {

                Log.d(
                    "SmartTableSocket",
                    "Socket.IO conectado."
                )
            }


            nuevoSocket.on(
                Socket.EVENT_CONNECT_ERROR
            ) {
                    argumentos ->

                val mensaje =
                    argumentos
                        .firstOrNull()
                        ?.toString()
                        ?: "Error desconocido"


                Log.e(
                    "SmartTableSocket",
                    "Error de conexión: $mensaje"
                )
            }


            nuevoSocket.on(
                Socket.EVENT_DISCONNECT
            ) {
                    argumentos ->

                val motivo =
                    argumentos
                        .firstOrNull()
                        ?.toString()
                        ?: "Sin motivo"


                Log.d(
                    "SmartTableSocket",
                    "Socket.IO desconectado: $motivo"
                )
            }


            /*
             * Evento que ya utiliza nuestra web.
             *
             * Backend envía algo como:
             *
             * {
             *   recursos: [
             *     "mesas",
             *     "turnos",
             *     "dashboard"
             *   ],
             *   metodo: "POST",
             *   ruta: "...",
             *   fechaHora: "..."
             * }
             */
            nuevoSocket.on(
                "smarttable:actualizacion"
            ) {
                    argumentos ->

                val datos =
                    argumentos
                        .firstOrNull()
                            as? JSONObject
                        ?: return@on


                val arreglo =
                    datos.optJSONArray(
                        "recursos"
                    )
                        ?: return@on


                val recursos =
                    mutableSetOf<String>()


                for (
                indice in
                0 until arreglo.length()
                ) {

                    val recurso =
                        arreglo.optString(
                            indice
                        )


                    if (
                        recurso.isNotBlank()
                    ) {

                        recursos.add(
                            recurso
                        )
                    }
                }


                if (
                    recursos.isNotEmpty()
                ) {

                    Log.d(
                        "SmartTableSocket",
                        "Actualización recibida: $recursos"
                    )


                    _actualizaciones
                        .tryEmit(
                            recursos
                        )
                }
            }


            /*
             * Evento inicial que nuestro backend
             * manda cuando autentica correctamente
             * el Socket.
             */
            nuevoSocket.on(
                "smarttable:conectado"
            ) {

                Log.d(
                    "SmartTableSocket",
                    "SmartTable confirmó la sesión realtime."
                )
            }


            socket =
                nuevoSocket


            nuevoSocket.connect()

        } catch (
            error: Exception
        ) {

            Log.e(
                "SmartTableSocket",
                "No fue posible crear el Socket.IO.",
                error
            )
        }
    }


    fun desconectar() {

        val socketActual =
            socket


        if (
            socketActual != null
        ) {

            socketActual.off()

            socketActual.disconnect()

            socketActual.close()
        }


        socket =
            null


        tokenActual =
            null
    }


    fun estaConectado(): Boolean {

        return socket
            ?.connected()
            ?: false
    }
}