package com.smarttable.app.data.model


data class Piso(
    val id: Int,
    val nombre: String,
    val orden: Int = 0,
    val activo: Boolean = true
)


data class TurnoAsignado(
    val id: Int,
    val numero: Int,
    val nombre: String,
    val personas: Int,
    val excedeCapacidad: Boolean = false,
    val personasExtra: Int = 0,
    val excepcionCapacidadAutorizada: Boolean = false
)


data class Mesa(
    val id: Int,
    val numero: String,
    val capacidad: Int,
    val estado: String,
    val pisoId: Int,
    val posicionX: Int = 0,
    val posicionY: Int = 0,
    val forma: String = "RECTANGLE",
    val turnoAsignado: TurnoAsignado? = null
)


data class PisoResumen(
    val id: Int,
    val nombre: String
)


data class MesaContexto(
    val id: Int,
    val numero: String,
    val capacidad: Int,
    val estado: String,
    val piso: PisoResumen
)


data class TurnoMesa(
    val id: Int,
    val numero: Int,
    val nombre: String,
    val personas: Int,
    val estado: String,
    val fechaHoraLlegada: String? = null,
    val llamadoEn: String? = null,
    val pisoPreferidoId: Int? = null,
    val pisoPreferido: PisoResumen? = null,
    val cabeEnMesa: Boolean = true,
    val coincidePreferencia: Boolean = false
)


data class TurnosParaMesaResponse(
    val mesa: MesaContexto,
    val turnos: List<TurnoMesa>
)


data class AsignarMesaRequest(
    val turnoId: Int,
    val permitirExcesoCapacidad: Boolean
)