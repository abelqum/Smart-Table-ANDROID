package com.smarttable.app.data.api


import com.smarttable.app.data.model.AsignarMesaRequest
import com.smarttable.app.data.model.CancelarVinculacionNfcRequest
import com.smarttable.app.data.model.CancelarVinculacionNfcResponse
import com.smarttable.app.data.model.ConfirmarVinculacionNfcRequest
import com.smarttable.app.data.model.ConfirmarVinculacionNfcResponse
import com.smarttable.app.data.model.LoginRequest
import com.smarttable.app.data.model.LoginResponse
import com.smarttable.app.data.model.Mesa
import com.smarttable.app.data.model.Piso
import com.smarttable.app.data.model.PrepararVinculacionNfcRequest
import com.smarttable.app.data.model.PrepararVinculacionNfcResponse
import com.smarttable.app.data.model.ResolverNfcRequest
import com.smarttable.app.data.model.ResolverNfcResponse
import com.smarttable.app.data.model.TurnosParaMesaResponse
import com.smarttable.app.data.model.Usuario

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface SmartTableApi {

    @POST("auth/login")
    suspend fun iniciarSesion(
        @Body datos: LoginRequest
    ): LoginResponse


    @GET("auth/me")
    suspend fun obtenerPerfil(): Usuario


    @GET("pisos")
    suspend fun obtenerPisos(): List<Piso>


    @GET("mesas")
    suspend fun obtenerMesas(): List<Mesa>


    @GET("turnos/para-mesa/{mesaId}")
    suspend fun obtenerTurnosParaMesa(
        @Path("mesaId") mesaId: Int
    ): TurnosParaMesaResponse


    @POST("mesas/{mesaId}/asignar")
    suspend fun asignarTurnoAMesa(
        @Path("mesaId") mesaId: Int,
        @Body datos: AsignarMesaRequest
    ): Mesa


    @POST("mesas/{mesaId}/clientes-retirados")
    suspend fun registrarSalidaClientes(
        @Path("mesaId") mesaId: Int
    ): Mesa


    @POST("mesas/{mesaId}/limpieza/iniciar")
    suspend fun iniciarLimpieza(
        @Path("mesaId") mesaId: Int
    ): Mesa


    @POST("mesas/{mesaId}/limpieza/finalizar")
    suspend fun finalizarLimpieza(
        @Path("mesaId") mesaId: Int
    ): Mesa


    /*
     * =========================================================
     * NFC OPERACIONAL
     * =========================================================
     */

    @POST("nfc/resolver")
    suspend fun resolverNfc(
        @Body datos: ResolverNfcRequest
    ): ResolverNfcResponse


    /*
     * =========================================================
     * ADMINISTRACIÓN NFC
     * =========================================================
     */

    @POST("nfc/vinculaciones/preparar")
    suspend fun prepararVinculacionNfc(
        @Body datos: PrepararVinculacionNfcRequest
    ): PrepararVinculacionNfcResponse


    @POST("nfc/vinculaciones/confirmar")
    suspend fun confirmarVinculacionNfc(
        @Body datos: ConfirmarVinculacionNfcRequest
    ): ConfirmarVinculacionNfcResponse


    @POST("nfc/vinculaciones/cancelar")
    suspend fun cancelarVinculacionNfc(
        @Body datos: CancelarVinculacionNfcRequest
    ): CancelarVinculacionNfcResponse
}