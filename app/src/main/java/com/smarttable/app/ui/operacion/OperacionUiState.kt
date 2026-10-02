package com.smarttable.app.ui.operacion


import com.smarttable.app.data.model.Mesa
import com.smarttable.app.data.model.Piso
import com.smarttable.app.data.model.TurnosParaMesaResponse


data class OperacionUiState(

    val cargando: Boolean =
        true,

    val pisos: List<Piso> =
        emptyList(),

    val mesas: List<Mesa> =
        emptyList(),

    val pisoSeleccionadoId: Int? =
        null,

    val mesaSeleccionadaId: Int? =
        null,

    val contextoMesa: TurnosParaMesaResponse? =
        null,

    val cargandoContextoMesa: Boolean =
        false,

    val ejecutandoAccion: Boolean =
        false,

    val mensaje: String? =
        null,

    val error: String? =
        null,

    val metodoSeleccionMesa: String =
        "MANUAL",

    val resolviendoNfc: Boolean =
        false,
)