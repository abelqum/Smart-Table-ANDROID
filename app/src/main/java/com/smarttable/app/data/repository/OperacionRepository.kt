package com.smarttable.app.data.repository


import com.smarttable.app.data.api.ApiClient
import com.smarttable.app.data.model.AsignarMesaRequest
import com.smarttable.app.data.model.Mesa
import com.smarttable.app.data.model.Piso
import com.smarttable.app.data.model.TurnoMesa
import com.smarttable.app.data.model.TurnosParaMesaResponse


class OperacionRepository {

    suspend fun obtenerPisos(): List<Piso> {

        return ApiClient.api
            .obtenerPisos()
    }


    suspend fun obtenerMesas(): List<Mesa> {

        return ApiClient.api
            .obtenerMesas()
    }


    suspend fun obtenerTurnosParaMesa(
        mesaId: Int
    ): TurnosParaMesaResponse {

        return ApiClient.api
            .obtenerTurnosParaMesa(
                mesaId
            )
    }


    suspend fun asignarTurnoAMesa(
        mesa: Mesa,
        turno: TurnoMesa,
        permitirExcesoCapacidad: Boolean
    ): Mesa {

        return ApiClient.api
            .asignarTurnoAMesa(
                mesa.id,

                AsignarMesaRequest(
                    turnoId =
                        turno.id,

                    permitirExcesoCapacidad =
                        permitirExcesoCapacidad
                )
            )
    }


    suspend fun registrarSalidaClientes(
        mesaId: Int
    ): Mesa {

        return ApiClient.api
            .registrarSalidaClientes(
                mesaId
            )
    }


    suspend fun iniciarLimpieza(
        mesaId: Int
    ): Mesa {

        return ApiClient.api
            .iniciarLimpieza(
                mesaId
            )
    }


    suspend fun finalizarLimpieza(
        mesaId: Int
    ): Mesa {

        return ApiClient.api
            .finalizarLimpieza(
                mesaId
            )
    }
}