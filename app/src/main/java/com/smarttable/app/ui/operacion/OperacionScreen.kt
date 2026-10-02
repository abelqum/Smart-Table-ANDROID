package com.smarttable.app.ui.operacion


import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.window.Dialog

import androidx.lifecycle.viewmodel.compose.viewModel

import com.smarttable.app.data.model.Mesa
import com.smarttable.app.data.model.Piso
import com.smarttable.app.data.model.TurnoMesa
import com.smarttable.app.data.model.TurnosParaMesaResponse
import com.smarttable.app.data.model.Usuario

import com.smarttable.app.ui.nfc.VinculacionNfcDialog


private val VerdeSmartTable =
    Color(
        0xFF059669
    )


private val Fondo =
    Color(
        0xFFF8FAFC
    )


private val TextoPrincipal =
    Color(
        0xFF0F172A
    )


private val TextoSecundario =
    Color(
        0xFF64748B
    )


@Composable
fun OperacionScreen(
    usuario: Usuario,
    onCerrarSesion: () -> Unit,
    viewModel: OperacionViewModel = viewModel()
) {

    val estado by
    viewModel
        .estado
        .collectAsState()


    val snackbar =
        remember {
            SnackbarHostState()
        }


    /*
     * Mesa seleccionada exclusivamente para
     * programar o reemplazar su etiqueta NFC.
     *
     * La mantenemos separada de mesaSeleccionadaId
     * porque la vinculación NFC es una operación
     * administrativa distinta de la operación diaria.
     */
    var mesaParaVincularNfc by
    remember {
        mutableStateOf<Mesa?>(
            null
        )
    }


    LaunchedEffect(
        Unit
    ) {

        viewModel
            .cargarOperacion()
    }


    LaunchedEffect(
        estado.mensaje
    ) {

        val mensaje =
            estado.mensaje


        if (
            mensaje != null
        ) {

            snackbar.showSnackbar(
                mensaje
            )


            viewModel
                .limpiarMensaje()
        }
    }


    LaunchedEffect(
        estado.error
    ) {

        val error =
            estado.error


        if (
            error != null
        ) {

            snackbar.showSnackbar(
                error
            )


            viewModel
                .limpiarError()
        }
    }


    val mesasPiso =
        estado.mesas.filter {

            it.pisoId ==
                    estado.pisoSeleccionadoId
        }


    val mesaSeleccionada =
        estado.mesas.firstOrNull {

            it.id ==
                    estado.mesaSeleccionadaId
        }


    Scaffold(

        snackbarHost = {

            SnackbarHost(
                hostState =
                    snackbar
            )
        }

    ) { padding ->

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Fondo
                    )
                    .padding(
                        padding
                    )
                    .padding(
                        horizontal =
                            16.dp,

                        vertical =
                            14.dp
                    )
        ) {

            EncabezadoOperacion(
                usuario =
                    usuario,

                onCerrarSesion =
                    onCerrarSesion
            )


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            if (
                estado.cargando
            ) {

                Box(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color =
                            VerdeSmartTable
                    )
                }


                return@Column
            }


            if (
                estado.pisos.isEmpty()
            ) {

                EstadoVacio(

                    titulo =
                        "No existen pisos configurados",

                    descripcion =
                        "Configura las zonas del restaurante desde SmartTable Web.",

                    onReintentar = {

                        viewModel
                            .cargarOperacion()
                    }
                )


                return@Column
            }


            SelectorPiso(

                pisos =
                    estado.pisos,

                pisoSeleccionadoId =
                    estado.pisoSeleccionadoId,

                onSeleccionar = {

                    viewModel
                        .seleccionarPiso(
                            it
                        )
                }
            )


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            LeyendaEstados()


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            /*
             * El NFC se muestra como flujo principal.
             *
             * El mapa continúa disponible como
             * alternativa manual y contingencia.
             */
            NfcStatusCard()


            if (
                estado.resolviendoNfc
            ) {

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                Surface(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),

                    color =
                        Color(
                            0xFFECFDF5
                        )
                ) {

                    Row(

                        modifier =
                            Modifier.padding(
                                12.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        CircularProgressIndicator(

                            modifier =
                                Modifier.size(
                                    20.dp
                                ),

                            color =
                                VerdeSmartTable,

                            strokeWidth =
                                2.dp
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    10.dp
                                )
                        )


                        Text(

                            text =
                                "Identificando mesa NFC...",

                            color =
                                Color(
                                    0xFF047857
                                ),

                            fontWeight =
                                FontWeight.SemiBold,

                            fontSize =
                                12.sp
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            Card(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(
                            1f
                        ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),

                shape =
                    RoundedCornerShape(
                        20.dp
                    )
            ) {

                if (
                    mesasPiso.isEmpty()
                ) {

                    Box(

                        modifier =
                            Modifier.fillMaxSize(),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(

                            text =
                                "No hay mesas en esta zona.",

                            color =
                                TextoSecundario
                        )
                    }

                } else {

                    PlanoMesas(

                        mesas =
                            mesasPiso,

                        mesaSeleccionadaId =
                            estado.mesaSeleccionadaId,

                        onMesaSeleccionada = {

                            viewModel
                                .seleccionarMesa(

                                    mesa =
                                        it,

                                    rolUsuario =
                                        usuario.rol
                                )
                        }
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            Text(

                text =
                    "También puedes tocar una mesa manualmente si no puedes utilizar NFC.",

                modifier =
                    Modifier.fillMaxWidth(),

                color =
                    TextoSecundario,

                fontSize =
                    11.sp
            )
        }


        /*
         * =====================================================
         * OPERACIÓN DE MESA
         * =====================================================
         */

        if (
            mesaSeleccionada !=
            null
        ) {

            DetalleMesaDialog(

                mesa =
                    mesaSeleccionada,

                rolUsuario =
                    usuario.rol,

                metodoSeleccionMesa =
                    estado.metodoSeleccionMesa,

                contextoMesa =
                    estado.contextoMesa,

                cargandoContexto =
                    estado.cargandoContextoMesa,

                ejecutandoAccion =
                    estado.ejecutandoAccion,

                onCerrar = {

                    viewModel
                        .cerrarMesa()
                },

                onVincularNfc = {

                    /*
                     * Cerramos el diálogo operacional
                     * antes de abrir el administrativo
                     * para no apilar dos diálogos.
                     */
                    mesaParaVincularNfc =
                        mesaSeleccionada


                    viewModel
                        .cerrarMesa()
                },

                onAsignar = {

                    viewModel
                        .asignarTurno(

                            mesa =
                                mesaSeleccionada,

                            turno =
                                it,

                            rolUsuario =
                                usuario.rol
                        )
                },

                onClientesRetirados = {

                    viewModel
                        .registrarSalidaClientes(
                            mesaSeleccionada
                        )
                },

                onIniciarLimpieza = {

                    viewModel
                        .iniciarLimpieza(
                            mesaSeleccionada
                        )
                },

                onFinalizarLimpieza = {

                    viewModel
                        .finalizarLimpieza(
                            mesaSeleccionada
                        )
                }
            )
        }


        /*
         * =====================================================
         * VINCULACIÓN DE ETIQUETA NFC
         * =====================================================
         */

        val mesaNfc =
            mesaParaVincularNfc


        if (
            mesaNfc !=
            null
        ) {

            VinculacionNfcDialog(

                mesa =
                    mesaNfc,

                onCerrar = {

                    mesaParaVincularNfc =
                        null
                }
            )
        }
    }
}


@Composable
private fun EncabezadoOperacion(
    usuario: Usuario,
    onCerrarSesion: () -> Unit
) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(

            modifier =
                Modifier.weight(
                    1f
                )
        ) {

            Text(

                text =
                    "SmartTable",

                color =
                    TextoPrincipal,

                fontSize =
                    25.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Text(

                text =
                    "${usuario.nombre} · ${
                        obtenerNombreRol(
                            usuario.rol
                        )
                    }",

                color =
                    TextoSecundario,

                fontSize =
                    12.sp
            )
        }


        TextButton(
            onClick =
                onCerrarSesion
        ) {

            Text(

                text =
                    "Salir",

                color =
                    Color(
                        0xFFDC2626
                    )
            )
        }
    }
}


@Composable
private fun SelectorPiso(
    pisos: List<Piso>,
    pisoSeleccionadoId: Int?,
    onSeleccionar: (Int) -> Unit
) {

    var abierto by
    remember {
        mutableStateOf(
            false
        )
    }


    val pisoActual =
        pisos.firstOrNull {

            it.id ==
                    pisoSeleccionadoId
        }


    Box {

        OutlinedButton(

            onClick = {

                abierto =
                    true
            },

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    14.dp
                )
        ) {

            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(

                    text =
                        "Zona",

                    fontSize =
                        10.sp,

                    color =
                        TextoSecundario
                )


                Text(

                    text =
                        pisoActual
                            ?.nombre
                            ?: "Seleccionar",

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        TextoPrincipal
                )
            }


            Text(

                text =
                    "▼",

                color =
                    TextoSecundario
            )
        }


        DropdownMenu(

            expanded =
                abierto,

            onDismissRequest = {

                abierto =
                    false
            }
        ) {

            pisos.forEach {
                    piso ->

                DropdownMenuItem(

                    text = {

                        Text(
                            text =
                                piso.nombre
                        )
                    },

                    onClick = {

                        abierto =
                            false


                        onSeleccionar(
                            piso.id
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun LeyendaEstados() {

    Column(

        verticalArrangement =
            Arrangement.spacedBy(
                5.dp
            )
    ) {

        Row(

            horizontalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )
        ) {

            ItemLeyenda(

                nombre =
                    "Disponible",

                color =
                    Color(
                        0xFF10B981
                    )
            )


            ItemLeyenda(

                nombre =
                    "Ocupada",

                color =
                    Color(
                        0xFFF43F5E
                    )
            )
        }


        Row(

            horizontalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )
        ) {

            ItemLeyenda(

                nombre =
                    "Sucia",

                color =
                    Color(
                        0xFFF59E0B
                    )
            )


            ItemLeyenda(

                nombre =
                    "Limpieza",

                color =
                    Color(
                        0xFFF97316
                    )
            )
        }
    }
}


@Composable
private fun ItemLeyenda(
    nombre: String,
    color: Color
) {

    Row(

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(

            modifier =
                Modifier
                    .size(
                        9.dp
                    )
                    .background(

                        color =
                            color,

                        shape =
                            CircleShape
                    )
        )


        Spacer(
            modifier =
                Modifier.width(
                    5.dp
                )
        )


        Text(

            text =
                nombre,

            color =
                TextoSecundario,

            fontSize =
                11.sp
        )
    }
}


@Composable
private fun PlanoMesas(
    mesas: List<Mesa>,
    mesaSeleccionadaId: Int?,
    onMesaSeleccionada: (Mesa) -> Unit
) {

    BoxWithConstraints(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    10.dp
                )
                .background(

                    color =
                        Color(
                            0xFFF8FAFC
                        ),

                    shape =
                        RoundedCornerShape(
                            16.dp
                        )
                )
    ) {

        /*
         * Utilizamos las mismas coordenadas
         * configuradas desde la aplicación Web.
         *
         * El espacio lógico del editor web se
         * escala al tamaño disponible en móvil.
         */
        val escalaX =
            maxWidth.value /
                    900f


        val escalaY =
            maxHeight.value /
                    520f


        mesas.forEach {
                mesa ->

            val esRedonda =
                mesa.forma ==
                        "ROUND"


            val forma: Shape =
                if (
                    esRedonda
                ) {

                    CircleShape

                } else {

                    RoundedCornerShape(
                        14.dp
                    )
                }


            val ancho =
                if (
                    esRedonda
                ) {

                    67.dp

                } else {

                    82.dp
                }


            val alto =
                if (
                    esRedonda
                ) {

                    67.dp

                } else {

                    58.dp
                }


            val posicionX =
                (
                        mesa.posicionX *
                                escalaX
                        )
                    .coerceAtLeast(
                        0f
                    )
                    .dp


            val posicionY =
                (
                        mesa.posicionY *
                                escalaY
                        )
                    .coerceAtLeast(
                        0f
                    )
                    .dp


            val seleccionada =
                mesa.id ==
                        mesaSeleccionadaId


            Surface(

                onClick = {

                    onMesaSeleccionada(
                        mesa
                    )
                },

                modifier =
                    Modifier
                        .offset(

                            x =
                                posicionX,

                            y =
                                posicionY
                        )
                        .size(

                            width =
                                ancho,

                            height =
                                alto
                        )
                        .then(

                            if (
                                seleccionada
                            ) {

                                Modifier.border(

                                    width =
                                        3.dp,

                                    color =
                                        TextoPrincipal,

                                    shape =
                                        forma
                                )

                            } else {

                                Modifier
                            }
                        ),

                shape =
                    forma,

                color =
                    colorMesa(
                        mesa.estado
                    ),

                tonalElevation =
                    2.dp
            ) {

                Column(

                    modifier =
                        Modifier.fillMaxSize(),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Text(

                        text =
                            mesa.numero,

                        color =
                            Color.White,

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            15.sp
                    )


                    Text(

                        text =
                            "${mesa.capacidad} p.",

                        color =
                            Color.White.copy(
                                alpha =
                                    0.9f
                            ),

                        fontSize =
                            10.sp
                    )
                }
            }
        }
    }
}


@Composable
private fun DetalleMesaDialog(
    mesa: Mesa,
    rolUsuario: String,
    metodoSeleccionMesa: String,
    contextoMesa: TurnosParaMesaResponse?,
    cargandoContexto: Boolean,
    ejecutandoAccion: Boolean,
    onCerrar: () -> Unit,
    onVincularNfc: () -> Unit,
    onAsignar: (TurnoMesa) -> Unit,
    onClientesRetirados: () -> Unit,
    onIniciarLimpieza: () -> Unit,
    onFinalizarLimpieza: () -> Unit
) {

    var turnoConfirmacion by
    remember(
        mesa.id
    ) {

        mutableStateOf<TurnoMesa?>(
            null
        )
    }


    var confirmarSalida by
    remember(
        mesa.id
    ) {

        mutableStateOf(
            false
        )
    }


    var confirmarInicioLimpieza by
    remember(
        mesa.id
    ) {

        mutableStateOf(
            false
        )
    }


    var confirmarFinLimpieza by
    remember(
        mesa.id
    ) {

        mutableStateOf(
            false
        )
    }


    Dialog(
        onDismissRequest =
            onCerrar
    ) {

        Surface(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    24.dp
                ),

            color =
                Color.White
        ) {

            Column(

                modifier =
                    Modifier.padding(
                        20.dp
                    )
            ) {

                /*
                 * Si la mesa fue localizada mediante NFC
                 * lo hacemos muy evidente.
                 */
                if (
                    metodoSeleccionMesa ==
                    "NFC"
                ) {

                    Surface(

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),

                        color =
                            Color(
                                0xFFECFDF5
                            )
                    ) {

                        Row(

                            modifier =
                                Modifier.padding(
                                    11.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(

                                text =
                                    "📡",

                                fontSize =
                                    21.sp
                            )


                            Spacer(
                                modifier =
                                    Modifier.width(
                                        8.dp
                                    )
                            )


                            Column {

                                Text(

                                    text =
                                        "Mesa identificada por NFC",

                                    color =
                                        Color(
                                            0xFF047857
                                        ),

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize =
                                        12.sp
                                )


                                Text(

                                    text =
                                        "Estás operando la mesa física que acabas de escanear.",

                                    color =
                                        Color(
                                            0xFF047857
                                        ).copy(
                                            alpha =
                                                0.8f
                                        ),

                                    fontSize =
                                        10.sp
                                )
                            }
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )
                }


                Row(

                    verticalAlignment =
                        Alignment.Top
                ) {

                    Column(

                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Text(

                            text =
                                "Mesa ${mesa.numero}",

                            fontSize =
                                24.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextoPrincipal
                        )


                        Text(

                            text =
                                "${mesa.capacidad} ${
                                    if (
                                        mesa.capacidad ==
                                        1
                                    ) {
                                        "persona"
                                    } else {
                                        "personas"
                                    }
                                }",

                            color =
                                TextoSecundario,

                            fontSize =
                                13.sp
                        )
                    }


                    EstadoMesaChip(
                        estado =
                            mesa.estado
                    )
                }


                HorizontalDivider(

                    modifier =
                        Modifier.padding(
                            vertical =
                                16.dp
                        )
                )


                when (
                    mesa.estado
                ) {

                    "AVAILABLE" -> {

                        ContenidoMesaDisponible(

                            rolUsuario =
                                rolUsuario,

                            contextoMesa =
                                contextoMesa,

                            cargando =
                                cargandoContexto,

                            ejecutandoAccion =
                                ejecutandoAccion,

                            onSeleccionarTurno = {

                                turnoConfirmacion =
                                    it
                            }
                        )
                    }


                    "OCCUPIED" -> {

                        ContenidoMesaOcupada(

                            mesa =
                                mesa,

                            puedeRegistrarSalida =
                                rolUsuario in
                                        listOf(
                                            "ADMIN",
                                            "HOSTESS",
                                            "WAITER"
                                        ),

                            ejecutando =
                                ejecutandoAccion,

                            onClientesRetirados = {

                                confirmarSalida =
                                    true
                            }
                        )
                    }


                    "DIRTY" -> {

                        ContenidoMesaSucia(

                            /*
                             * El mesero también puede
                             * colaborar con limpieza.
                             */
                            puedeLimpiar =
                                rolUsuario in
                                        listOf(
                                            "ADMIN",
                                            "HOSTESS",
                                            "WAITER",
                                            "CLEANING"
                                        ),

                            ejecutando =
                                ejecutandoAccion,

                            onIniciar = {

                                confirmarInicioLimpieza =
                                    true
                            }
                        )
                    }


                    "CLEANING" -> {

                        ContenidoMesaLimpieza(

                            puedeLimpiar =
                                rolUsuario in
                                        listOf(
                                            "ADMIN",
                                            "HOSTESS",
                                            "WAITER",
                                            "CLEANING"
                                        ),

                            ejecutando =
                                ejecutandoAccion,

                            onFinalizar = {

                                confirmarFinLimpieza =
                                    true
                            }
                        )
                    }


                    else -> {

                        Text(

                            text =
                                "La mesa no tiene acciones disponibles en este momento.",

                            color =
                                TextoSecundario
                        )
                    }
                }


                /*
                 * Vincular etiquetas es una operación
                 * administrativa, no operativa.
                 */
                if (
                    rolUsuario ==
                    "ADMIN"
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                18.dp
                            )
                    )


                    HorizontalDivider()


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    OutlinedButton(

                        onClick =
                            onVincularNfc,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text =
                                "📡 Vincular / reemplazar NFC"
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                TextButton(

                    onClick =
                        onCerrar,

                    modifier =
                        Modifier.align(
                            Alignment.End
                        )
                ) {

                    Text(
                        text =
                            "Cerrar"
                    )
                }
            }
        }
    }


    /*
     * =========================================================
     * CONFIRMAR ASIGNACIÓN
     * =========================================================
     */

    if (
        turnoConfirmacion !=
        null
    ) {

        val turno =
            turnoConfirmacion!!


        val personasExtra =
            (
                    turno.personas -
                            mesa.capacidad
                    )
                .coerceAtLeast(
                    0
                )


        AlertDialog(

            onDismissRequest = {

                turnoConfirmacion =
                    null
            },

            title = {

                Text(

                    text =
                        if (
                            personasExtra >
                            0
                        ) {

                            "Autorizar excepción"

                        } else {

                            "Confirmar cliente"
                        }
                )
            },

            text = {

                Text(

                    text =
                        buildString {

                            append(
                                "Mesa ${mesa.numero}\n"
                            )


                            append(
                                "Capacidad: ${mesa.capacidad}\n\n"
                            )


                            append(
                                "Turno #${turno.numero}\n"
                            )


                            append(
                                "${turno.nombre}\n"
                            )


                            append(
                                "${turno.personas} personas"
                            )


                            if (
                                personasExtra >
                                0
                            ) {

                                append(
                                    "\n\nEl grupo excede la capacidad por $personasExtra persona(s)."
                                )
                            }


                            append(
                                "\n\nConfirma únicamente después de verificar físicamente al cliente."
                            )
                        }
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        turnoConfirmacion =
                            null


                        onAsignar(
                            turno
                        )
                    }
                ) {

                    Text(
                        text =
                            "Confirmar"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        turnoConfirmacion =
                            null
                    }
                ) {

                    Text(
                        text =
                            "Cancelar"
                    )
                }
            }
        )
    }


    /*
     * =========================================================
     * CLIENTES SE RETIRARON
     * =========================================================
     */

    if (
        confirmarSalida
    ) {

        AlertDialog(

            onDismissRequest = {

                confirmarSalida =
                    false
            },

            title = {

                Text(
                    text =
                        "Clientes se retiraron"
                )
            },

            text = {

                Text(
                    text =
                        "La Mesa ${mesa.numero} pasará a pendiente de limpieza."
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        confirmarSalida =
                            false


                        onClientesRetirados()
                    }
                ) {

                    Text(
                        text =
                            "Confirmar"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        confirmarSalida =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Cancelar"
                    )
                }
            }
        )
    }


    /*
     * =========================================================
     * INICIAR LIMPIEZA
     * =========================================================
     */

    if (
        confirmarInicioLimpieza
    ) {

        AlertDialog(

            onDismissRequest = {

                confirmarInicioLimpieza =
                    false
            },

            title = {

                Text(
                    text =
                        "Iniciar limpieza"
                )
            },

            text = {

                Text(
                    text =
                        "¿Comenzar la limpieza de la Mesa ${mesa.numero}?"
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        confirmarInicioLimpieza =
                            false


                        onIniciarLimpieza()
                    }
                ) {

                    Text(
                        text =
                            "Iniciar"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        confirmarInicioLimpieza =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Cancelar"
                    )
                }
            }
        )
    }


    /*
     * =========================================================
     * FINALIZAR LIMPIEZA
     * =========================================================
     */

    if (
        confirmarFinLimpieza
    ) {

        AlertDialog(

            onDismissRequest = {

                confirmarFinLimpieza =
                    false
            },

            title = {

                Text(
                    text =
                        "Finalizar limpieza"
                )
            },

            text = {

                Text(
                    text =
                        "La Mesa ${mesa.numero} volverá a estar disponible."
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        confirmarFinLimpieza =
                            false


                        onFinalizarLimpieza()
                    }
                ) {

                    Text(
                        text =
                            "Finalizar"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        confirmarFinLimpieza =
                            false
                    }
                ) {

                    Text(
                        text =
                            "Cancelar"
                    )
                }
            }
        )
    }
}


@Composable
private fun ContenidoMesaDisponible(
    rolUsuario: String,
    contextoMesa: TurnosParaMesaResponse?,
    cargando: Boolean,
    ejecutandoAccion: Boolean,
    onSeleccionarTurno: (TurnoMesa) -> Unit
) {

    val puedeAsignar =
        rolUsuario in
                listOf(
                    "ADMIN",
                    "HOSTESS",
                    "WAITER"
                )


    if (
        !puedeAsignar
    ) {

        Text(

            text =
                "La mesa está disponible. Tu rol no necesita realizar ninguna acción.",

            color =
                TextoSecundario
        )


        return
    }


    Text(

        text =
            "¿Quién ocupará esta mesa?",

        color =
            TextoPrincipal,

        fontWeight =
            FontWeight.Bold,

        fontSize =
            17.sp
    )


    Text(

        text =
            "Verifica el número de turno y el nombre del cliente.",

        modifier =
            Modifier.padding(
                top =
                    4.dp
            ),

        color =
            TextoSecundario,

        fontSize =
            12.sp
    )


    Spacer(
        modifier =
            Modifier.height(
                14.dp
            )
    )


    if (
        cargando
    ) {

        Box(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        100.dp
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            CircularProgressIndicator(
                color =
                    VerdeSmartTable
            )
        }


        return
    }


    val turnos =
        contextoMesa
            ?.turnos
            ?: emptyList()


    if (
        turnos.isEmpty()
    ) {

        Text(

            text =
                "No hay turnos disponibles para esta mesa.",

            color =
                TextoSecundario,

            fontSize =
                13.sp
        )


        return
    }


    LazyColumn(

        modifier =
            Modifier.heightIn(
                max =
                    360.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(
                8.dp
            )
    ) {

        items(

            items =
                turnos,

            key = {

                it.id
            }

        ) {
                turno ->

            TurnoCard(

                turno =
                    turno,

                capacidadMesa =
                    contextoMesa
                        ?.mesa
                        ?.capacidad
                        ?: 0,

                habilitado =
                    !ejecutandoAccion,

                onClick = {

                    onSeleccionarTurno(
                        turno
                    )
                }
            )
        }
    }
}


@Composable
private fun TurnoCard(
    turno: TurnoMesa,
    capacidadMesa: Int,
    habilitado: Boolean,
    onClick: () -> Unit
) {

    val personasExtra =
        (
                turno.personas -
                        capacidadMesa
                )
            .coerceAtLeast(
                0
            )


    Surface(

        onClick =
            onClick,

        enabled =
            habilitado,

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                14.dp
            ),

        color =
            if (
                turno.coincidePreferencia
            ) {

                Color(
                    0xFFF0FDF4
                )

            } else {

                Color(
                    0xFFF8FAFC
                )
            }
    ) {

        Row(

            modifier =
                Modifier.padding(
                    14.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(

                    text =
                        "Turno #${turno.numero}",

                    color =
                        TextoPrincipal,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text =
                        turno.nombre,

                    color =
                        TextoPrincipal,

                    fontSize =
                        14.sp
                )


                Text(

                    text =
                        "${turno.personas} ${
                            if (
                                turno.personas ==
                                1
                            ) {
                                "persona"
                            } else {
                                "personas"
                            }
                        } · ${
                            turno.pisoPreferido
                                ?.nombre
                                ?: "Sin preferencia"
                        }",

                    modifier =
                        Modifier.padding(
                            top =
                                3.dp
                        ),

                    color =
                        TextoSecundario,

                    fontSize =
                        11.sp
                )


                if (
                    turno.coincidePreferencia
                ) {

                    Text(

                        text =
                            "Prefiere esta zona",

                        modifier =
                            Modifier.padding(
                                top =
                                    4.dp
                            ),

                        color =
                            VerdeSmartTable,

                        fontSize =
                            10.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }


            if (
                personasExtra >
                0
            ) {

                Text(

                    text =
                        "+$personasExtra",

                    color =
                        Color(
                            0xFFB45309
                        ),

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        12.sp
                )

            } else {

                Text(

                    text =
                        "Compatible",

                    color =
                        VerdeSmartTable,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize =
                        10.sp
                )
            }
        }
    }
}


@Composable
private fun ContenidoMesaOcupada(
    mesa: Mesa,
    puedeRegistrarSalida: Boolean,
    ejecutando: Boolean,
    onClientesRetirados: () -> Unit
) {

    val turno =
        mesa.turnoAsignado


    if (
        turno !=
        null
    ) {

        Text(

            text =
                "Turno #${turno.numero}",

            color =
                TextoPrincipal,

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.Bold
        )


        Text(

            text =
                turno.nombre,

            modifier =
                Modifier.padding(
                    top =
                        4.dp
                ),

            color =
                TextoPrincipal
        )


        Text(

            text =
                "${turno.personas} ${
                    if (
                        turno.personas ==
                        1
                    ) {
                        "persona"
                    } else {
                        "personas"
                    }
                }",

            modifier =
                Modifier.padding(
                    top =
                        3.dp
                ),

            color =
                TextoSecundario
        )


        if (
            turno.excedeCapacidad
        ) {

            Text(

                text =
                    "Excepción autorizada: +${turno.personasExtra}",

                modifier =
                    Modifier.padding(
                        top =
                            8.dp
                    ),

                color =
                    Color(
                        0xFFB45309
                    ),

                fontSize =
                    12.sp
            )
        }

    } else {

        Text(

            text =
                "La mesa se encuentra ocupada.",

            color =
                TextoSecundario
        )
    }


    if (
        puedeRegistrarSalida
    ) {

        Spacer(
            modifier =
                Modifier.height(
                    18.dp
                )
        )


        Button(

            onClick =
                onClientesRetirados,

            enabled =
                !ejecutando,

            modifier =
                Modifier.fillMaxWidth(),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        Color(
                            0xFFF59E0B
                        )
                )
        ) {

            Text(
                text =
                    "Clientes se retiraron"
            )
        }
    }
}


@Composable
private fun ContenidoMesaSucia(
    puedeLimpiar: Boolean,
    ejecutando: Boolean,
    onIniciar: () -> Unit
) {

    Text(

        text =
            "Pendiente de limpieza",

        color =
            Color(
                0xFFB45309
            ),

        fontWeight =
            FontWeight.Bold,

        fontSize =
            18.sp
    )


    Text(

        text =
            "La mesa debe limpiarse antes de volver a recibir clientes.",

        modifier =
            Modifier.padding(
                top =
                    6.dp
            ),

        color =
            TextoSecundario,

        fontSize =
            13.sp
    )


    if (
        puedeLimpiar
    ) {

        Spacer(
            modifier =
                Modifier.height(
                    18.dp
                )
        )


        Button(

            onClick =
                onIniciar,

            enabled =
                !ejecutando,

            modifier =
                Modifier.fillMaxWidth(),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        Color(
                            0xFFF59E0B
                        )
                )
        ) {

            Text(
                text =
                    "Iniciar limpieza"
            )
        }
    }
}


@Composable
private fun ContenidoMesaLimpieza(
    puedeLimpiar: Boolean,
    ejecutando: Boolean,
    onFinalizar: () -> Unit
) {

    Text(

        text =
            "Limpieza en proceso",

        color =
            Color(
                0xFFEA580C
            ),

        fontWeight =
            FontWeight.Bold,

        fontSize =
            18.sp
    )


    Text(

        text =
            "Confirma cuando la mesa esté lista para recibir clientes nuevamente.",

        modifier =
            Modifier.padding(
                top =
                    6.dp
            ),

        color =
            TextoSecundario,

        fontSize =
            13.sp
    )


    if (
        puedeLimpiar
    ) {

        Spacer(
            modifier =
                Modifier.height(
                    18.dp
                )
        )


        Button(

            onClick =
                onFinalizar,

            enabled =
                !ejecutando,

            modifier =
                Modifier.fillMaxWidth(),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        VerdeSmartTable
                )
        ) {

            Text(
                text =
                    "Finalizar limpieza"
            )
        }
    }
}


@Composable
private fun EstadoMesaChip(
    estado: String
) {

    Surface(

        color =
            colorMesa(
                estado
            ),

        shape =
            RoundedCornerShape(
                50
            )
    ) {

        Text(

            text =
                nombreEstado(
                    estado
                ),

            modifier =
                Modifier.padding(

                    horizontal =
                        10.dp,

                    vertical =
                        5.dp
                ),

            color =
                Color.White,

            fontSize =
                10.sp,

            fontWeight =
                FontWeight.SemiBold
        )
    }
}


@Composable
private fun EstadoVacio(
    titulo: String,
    descripcion: String,
    onReintentar: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    24.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Text(

            text =
                titulo,

            color =
                TextoPrincipal,

            fontWeight =
                FontWeight.Bold
        )


        Text(

            text =
                descripcion,

            modifier =
                Modifier.padding(
                    top =
                        6.dp
                ),

            color =
                TextoSecundario,

            fontSize =
                13.sp
        )


        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )


        Button(
            onClick =
                onReintentar
        ) {

            Text(
                text =
                    "Reintentar"
            )
        }
    }
}


private fun colorMesa(
    estado: String
): Color {

    return when (
        estado
    ) {

        "AVAILABLE" ->

            Color(
                0xFF10B981
            )


        "OCCUPIED" ->

            Color(
                0xFFF43F5E
            )


        "DIRTY" ->

            Color(
                0xFFF59E0B
            )


        "CLEANING" ->

            Color(
                0xFFF97316
            )


        "BLOCKED" ->

            Color(
                0xFF64748B
            )


        else ->

            Color(
                0xFF64748B
            )
    }
}


private fun nombreEstado(
    estado: String
): String {

    return when (
        estado
    ) {

        "AVAILABLE" ->
            "Disponible"


        "OCCUPIED" ->
            "Ocupada"


        "DIRTY" ->
            "Sucia"


        "CLEANING" ->
            "En limpieza"


        "BLOCKED" ->
            "Bloqueada"


        else ->
            estado
    }
}


private fun obtenerNombreRol(
    rol: String
): String {

    return when (
        rol
    ) {

        "ADMIN" ->
            "Administrador"


        "HOSTESS" ->
            "Hostess"


        "WAITER" ->
            "Mesero"


        "CLEANING" ->
            "Limpieza"


        else ->
            rol
    }
}