package com.smarttable.app.data.model


data class ResolverNfcRequest(
    val token: String
)


data class AccionNfc(
    val codigo: String,
    val titulo: String
)


data class LecturaNfc(
    val metodo: String,
    val fechaHora: String
)


data class ResolverNfcResponse(
    val mesa: Mesa,
    val accionesPermitidas: List<String>,
    val accionSugerida: AccionNfc?,
    val lectura: LecturaNfc
)


data class PrepararVinculacionNfcRequest(
    val mesaId: Int
)


data class MesaVinculacionNfc(
    val id: Int,
    val numero: String,
    val piso: PisoResumen
)


data class PrepararVinculacionNfcResponse(
    val etiquetaId: Int,
    val token: String,
    val contenido: String,
    val mesa: MesaVinculacionNfc
)


data class ConfirmarVinculacionNfcRequest(
    val etiquetaId: Int,
    val token: String
)


data class ConfirmarVinculacionNfcResponse(
    val vinculada: Boolean,
    val etiquetaId: Int,
    val mesa: MesaVinculacionNfc
)


data class CancelarVinculacionNfcRequest(
    val etiquetaId: Int
)


data class CancelarVinculacionNfcResponse(
    val cancelada: Boolean
)