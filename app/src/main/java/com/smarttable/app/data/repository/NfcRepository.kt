package com.smarttable.app.data.repository


import com.smarttable.app.data.api.ApiClient
import com.smarttable.app.data.model.CancelarVinculacionNfcRequest
import com.smarttable.app.data.model.ConfirmarVinculacionNfcRequest
import com.smarttable.app.data.model.PrepararVinculacionNfcRequest
import com.smarttable.app.data.model.ResolverNfcRequest


class NfcRepository {

    suspend fun resolver(
        token: String
    ) =

        ApiClient.api
            .resolverNfc(
                ResolverNfcRequest(
                    token =
                        token
                )
            )


    suspend fun prepararVinculacion(
        mesaId: Int
    ) =

        ApiClient.api
            .prepararVinculacionNfc(
                PrepararVinculacionNfcRequest(
                    mesaId =
                        mesaId
                )
            )


    suspend fun confirmarVinculacion(
        etiquetaId: Int,
        token: String
    ) =

        ApiClient.api
            .confirmarVinculacionNfc(
                ConfirmarVinculacionNfcRequest(
                    etiquetaId =
                        etiquetaId,

                    token =
                        token
                )
            )


    suspend fun cancelarVinculacion(
        etiquetaId: Int
    ) =

        ApiClient.api
            .cancelarVinculacionNfc(
                CancelarVinculacionNfcRequest(
                    etiquetaId =
                        etiquetaId
                )
            )
}