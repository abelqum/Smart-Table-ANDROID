package com.smarttable.app.ui.operacion


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
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


private val FondoApp =
    Color(
        0xFFF4F7F9
    )


private val FondoTarjeta =
    Color.White


private val NegroPrincipal =
    Color(
        0xFF111827
    )


private val GrisTexto =
    Color(
        0xFF64748B
    )


private val GrisSuave =
    Color(
        0xFFF1F5F9
    )


private val VerdeSmartTable =
    Color(
        0xFF059669
    )


private val VerdeSuave =
    Color(
        0xFFECFDF5
    )


private val RojoMesa =
    Color(
        0xFFEF4444
    )


private val AmarilloMesa =
    Color(
        0xFFF59E0B
    )


private val NaranjaMesa =
    Color(
        0xFFF97316
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
                        FondoApp
                    )
                    .padding(
                        padding
                    )
        ) {

            /*
             * =====================================================
             * CABECERA
             * =====================================================
             */

            EncabezadoOperacion(

                usuario =
                    usuario,

                onCerrarSesion =
                    onCerrarSesion
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


            LazyColumn(

                modifier =
                    Modifier.fillMaxSize(),

                contentPadding =
                    androidx.compose.foundation.layout.PaddingValues(

                        start =
                            16.dp,

                        end =
                            16.dp,

                        top =
                            16.dp,

                        bottom =
                            28.dp
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(
                        14.dp
                    )
            ) {

                /*
                 * =================================================
                 * RESUMEN OPERATIVO
                 * =================================================
                 */

                item {

                    ResumenOperacion(
                        mesas =
                            estado.mesas
                    )
                }


                /*
                 * =================================================
                 * NFC
                 * =================================================
                 */

                item {

                    TarjetaNfcPrincipal(
                        resolviendo =
                            estado.resolviendoNfc
                    )
                }


                /*
                 * =================================================
                 * PISOS
                 * =================================================
                 */

                item {

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

                    } else {

                        SelectorPisos(

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
                    }
                }


                /*
                 * =================================================
                 * MAPA
                 * =================================================
                 */

                if (
                    estado.pisos.isNotEmpty()
                ) {

                    item {

                        val pisoActual =
                            estado.pisos
                                .firstOrNull {

                                    it.id ==
                                            estado.pisoSeleccionadoId
                                }


                        Card(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(
                                        440.dp
                                    ),

                            shape =
                                RoundedCornerShape(
                                    24.dp
                                ),

                            colors =
                                CardDefaults.cardColors(

                                    containerColor =
                                        FondoTarjeta
                                ),

                            elevation =
                                CardDefaults.cardElevation(

                                    defaultElevation =
                                        2.dp
                                )
                        ) {

                            Column(

                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .padding(
                                            16.dp
                                        )
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
                                                pisoActual
                                                    ?.nombre
                                                    ?: "Mesas",

                                            color =
                                                NegroPrincipal,

                                            fontSize =
                                                18.sp,

                                            fontWeight =
                                                FontWeight.Bold
                                        )


                                        Text(

                                            text =
                                                "${mesasPiso.size} mesas en esta zona",

                                            color =
                                                GrisTexto,

                                            fontSize =
                                                11.sp
                                        )
                                    }


                                    Surface(

                                        shape =
                                            RoundedCornerShape(
                                                50.dp
                                            ),

                                        color =
                                            GrisSuave
                                    ) {

                                        Text(

                                            text =
                                                "Modo manual",

                                            modifier =
                                                Modifier.padding(

                                                    horizontal =
                                                        10.dp,

                                                    vertical =
                                                        6.dp
                                                ),

                                            color =
                                                GrisTexto,

                                            fontSize =
                                                10.sp,

                                            fontWeight =
                                                FontWeight.SemiBold
                                        )
                                    }
                                }


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
                                            14.dp
                                        )
                                )


                                Box(

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .weight(
                                                1f
                                            )
                                            .background(

                                                color =
                                                    Color(
                                                        0xFFF8FAFC
                                                    ),

                                                shape =
                                                    RoundedCornerShape(
                                                        18.dp
                                                    )
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
                                                    GrisTexto
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
                                        "El mapa funciona como respaldo. Para una operación más rápida, acerca el teléfono al NFC de la mesa.",

                                    color =
                                        GrisTexto,

                                    fontSize =
                                        10.sp,

                                    lineHeight =
                                        14.sp
                                )
                            }
                        }
                    }
                }
            }
        }


        /*
         * =========================================================
         * DETALLE OPERATIVO
         * =========================================================
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
         * =========================================================
         * ADMINISTRACIÓN NFC
         * =========================================================
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

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        color =
            Color.White,

        shadowElevation =
            3.dp
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(

                        horizontal =
                            18.dp,

                        vertical =
                            16.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(

                modifier =
                    Modifier.size(
                        45.dp
                    ),

                shape =
                    RoundedCornerShape(
                        14.dp
                    ),

                color =
                    NegroPrincipal
            ) {

                Box(

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text =
                            "ST",

                        color =
                            Color.White,

                        fontWeight =
                            FontWeight.ExtraBold,

                        fontSize =
                            15.sp
                    )
                }
            }


            Spacer(

                modifier =
                    Modifier.width(
                        12.dp
                    )
            )


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
                        NegroPrincipal,

                    fontSize =
                        20.sp,

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
                        GrisTexto,

                    fontSize =
                        11.sp
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
                        ),

                    fontSize =
                        12.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    }
}


@Composable
private fun ResumenOperacion(
    mesas: List<Mesa>
) {

    val disponibles =
        mesas.count {

            it.estado ==
                    "AVAILABLE"
        }


    val ocupadas =
        mesas.count {

            it.estado ==
                    "OCCUPIED"
        }


    val sucias =
        mesas.count {

            it.estado ==
                    "DIRTY"
        }


    val limpieza =
        mesas.count {

            it.estado ==
                    "CLEANING"
        }


    Column {

        Text(

            text =
                "Operación",

            color =
                NegroPrincipal,

            fontWeight =
                FontWeight.Bold,

            fontSize =
                21.sp
        )


        Text(

            text =
                "Estado actual del restaurante",

            modifier =
                Modifier.padding(
                    top =
                        2.dp
                ),

            color =
                GrisTexto,

            fontSize =
                12.sp
        )


        Spacer(

            modifier =
                Modifier.height(
                    12.dp
                )
        )


        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            TarjetaResumenEstado(

                titulo =
                    "Disponibles",

                cantidad =
                    disponibles,

                color =
                    VerdeSmartTable,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            TarjetaResumenEstado(

                titulo =
                    "Ocupadas",

                cantidad =
                    ocupadas,

                color =
                    RojoMesa,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            TarjetaResumenEstado(

                titulo =
                    "Sucias",

                cantidad =
                    sucias,

                color =
                    AmarilloMesa,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            TarjetaResumenEstado(

                titulo =
                    "Limpieza",

                cantidad =
                    limpieza,

                color =
                    NaranjaMesa,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )
        }
    }
}


@Composable
private fun TarjetaResumenEstado(
    titulo: String,
    cantidad: Int,
    color: Color,
    modifier: Modifier = Modifier
) {

    Card(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                16.dp
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(

                defaultElevation =
                    1.dp
            )
    ) {

        Column(

            modifier =
                Modifier.padding(
                    11.dp
                )
        ) {

            Box(

                modifier =
                    Modifier
                        .size(
                            8.dp
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
                    Modifier.height(
                        7.dp
                    )
            )


            Text(

                text =
                    cantidad.toString(),

                color =
                    NegroPrincipal,

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Text(

                text =
                    titulo,

                color =
                    GrisTexto,

                fontSize =
                    9.sp
            )
        }
    }
}


@Composable
private fun TarjetaNfcPrincipal(
    resolviendo: Boolean
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                22.dp
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    NegroPrincipal
            ),

        elevation =
            CardDefaults.cardElevation(

                defaultElevation =
                    4.dp
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(

                modifier =
                    Modifier.size(
                        54.dp
                    ),

                shape =
                    RoundedCornerShape(
                        17.dp
                    ),

                color =
                    Color.White.copy(
                        alpha =
                            0.12f
                    )
            ) {

                Box(

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text =
                            "NFC",

                        color =
                            Color.White,

                        fontWeight =
                            FontWeight.ExtraBold,

                        fontSize =
                            13.sp
                    )
                }
            }


            Spacer(

                modifier =
                    Modifier.width(
                        14.dp
                    )
            )


            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(

                    text =
                        if (
                            resolviendo
                        ) {

                            "Identificando mesa..."

                        } else {

                            "Operación NFC"
                        },

                    color =
                        Color.White,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        16.sp
                )


                Spacer(

                    modifier =
                        Modifier.height(
                            3.dp
                        )
                )


                Text(

                    text =
                        if (
                            resolviendo
                        ) {

                            "Consultando el estado actual de la mesa."

                        } else {

                            "Acerca el teléfono a una mesa para operar sin buscarla manualmente."
                        },

                    color =
                        Color.White.copy(
                            alpha =
                                0.72f
                        ),

                    fontSize =
                        11.sp,

                    lineHeight =
                        15.sp
                )
            }


            if (
                resolviendo
            ) {

                CircularProgressIndicator(

                    modifier =
                        Modifier.size(
                            25.dp
                        ),

                    color =
                        Color.White,

                    strokeWidth =
                        2.dp
                )

            } else {

                Surface(

                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),

                    color =
                        VerdeSmartTable
                ) {

                    Text(

                        text =
                            "LISTO",

                        modifier =
                            Modifier.padding(

                                horizontal =
                                    10.dp,

                                vertical =
                                    6.dp
                            ),

                        color =
                            Color.White,

                        fontSize =
                            9.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
private fun SelectorPisos(
    pisos: List<Piso>,
    pisoSeleccionadoId: Int?,
    onSeleccionar: (Int) -> Unit
) {

    Column {

        Text(

            text =
                "Zona",

            color =
                NegroPrincipal,

            fontWeight =
                FontWeight.Bold,

            fontSize =
                15.sp
        )


        Spacer(

            modifier =
                Modifier.height(
                    9.dp
                )
        )


        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),

            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            pisos.forEach {
                    piso ->

                val seleccionado =
                    piso.id ==
                            pisoSeleccionadoId


                Surface(

                    onClick = {

                        onSeleccionar(
                            piso.id
                        )
                    },

                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),

                    color =
                        if (
                            seleccionado
                        ) {

                            NegroPrincipal

                        } else {

                            Color.White
                        },

                    border =
                        if (
                            seleccionado
                        ) {

                            null

                        } else {

                            androidx.compose.foundation.BorderStroke(

                                width =
                                    1.dp,

                                color =
                                    Color(
                                        0xFFE2E8F0
                                    )
                            )
                        }
                ) {

                    Text(

                        text =
                            piso.nombre,

                        modifier =
                            Modifier.padding(

                                horizontal =
                                    16.dp,

                                vertical =
                                    9.dp
                            ),

                        color =
                            if (
                                seleccionado
                            ) {

                                Color.White

                            } else {

                                NegroPrincipal
                            },

                        fontSize =
                            12.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


@Composable
private fun LeyendaEstados() {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),

        horizontalArrangement =
            Arrangement.spacedBy(
                12.dp
            )
    ) {

        ItemLeyenda(
            nombre =
                "Disponible",
            color =
                VerdeSmartTable
        )


        ItemLeyenda(
            nombre =
                "Ocupada",
            color =
                RojoMesa
        )


        ItemLeyenda(
            nombre =
                "Sucia",
            color =
                AmarilloMesa
        )


        ItemLeyenda(
            nombre =
                "Limpieza",
            color =
                NaranjaMesa
        )
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
                        8.dp
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
                GrisTexto,

            fontSize =
                10.sp
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
                    8.dp
                )
    ) {

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

                    68.dp

                } else {

                    83.dp
                }


            val alto =
                if (
                    esRedonda
                ) {

                    68.dp

                } else {

                    60.dp
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
                                        NegroPrincipal,

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

                shadowElevation =
                    4.dp
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
                            FontWeight.ExtraBold,

                        fontSize =
                            15.sp
                    )


                    Text(

                        text =
                            "${mesa.capacidad} pers.",

                        color =
                            Color.White.copy(
                                alpha =
                                    0.82f
                            ),

                        fontSize =
                            9.sp
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
                    28.dp
                ),

            color =
                Color.White,

            shadowElevation =
                10.dp
        ) {

            Column(

                modifier =
                    Modifier.padding(
                        22.dp
                    )
            ) {

                /*
                 * =================================================
                 * ORIGEN NFC
                 * =================================================
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
                            VerdeSuave
                    ) {

                        Row(

                            modifier =
                                Modifier.padding(
                                    12.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Surface(

                                modifier =
                                    Modifier.size(
                                        34.dp
                                    ),

                                shape =
                                    RoundedCornerShape(
                                        10.dp
                                    ),

                                color =
                                    VerdeSmartTable
                            ) {

                                Box(

                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(

                                        text =
                                            "NFC",

                                        color =
                                            Color.White,

                                        fontSize =
                                            9.sp,

                                        fontWeight =
                                            FontWeight.ExtraBold
                                    )
                                }
                            }


                            Spacer(

                                modifier =
                                    Modifier.width(
                                        10.dp
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
                                        "Operación sobre la mesa física escaneada.",

                                    color =
                                        Color(
                                            0xFF047857
                                        ).copy(
                                            alpha =
                                                0.75f
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
                                16.dp
                            )
                    )
                }


                /*
                 * =================================================
                 * MESA
                 * =================================================
                 */

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

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

                            color =
                                NegroPrincipal,

                            fontWeight =
                                FontWeight.ExtraBold,

                            fontSize =
                                27.sp
                        )


                        Spacer(

                            modifier =
                                Modifier.height(
                                    3.dp
                                )
                        )


                        Text(

                            text =
                                "Capacidad ${mesa.capacidad} ${
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
                                GrisTexto,

                            fontSize =
                                12.sp
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
                                18.dp
                        ),

                    color =
                        Color(
                            0xFFE5E7EB
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
                                "No existen acciones disponibles para esta mesa.",

                            color =
                                GrisTexto
                        )
                    }
                }


                /*
                 * =================================================
                 * NFC ADMIN
                 * =================================================
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


                    HorizontalDivider(
                        color =
                            Color(
                                0xFFE5E7EB
                            )
                    )


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
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(
                                14.dp
                            )
                    ) {

                        Text(

                            text =
                                "NFC · Vincular o reemplazar etiqueta",

                            color =
                                NegroPrincipal,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }


                Spacer(

                    modifier =
                        Modifier.height(
                            10.dp
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
     * ASIGNAR TURNO
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


        val puedeAutorizarExceso =
            rolUsuario in
                    listOf(
                        "ADMIN",
                        "HOSTESS"
                    )


        /*
         * Un mesero no debería llegar normalmente
         * a este diálogo con un turno incompatible.
         */
        if (
            personasExtra ==
            0 ||
            puedeAutorizarExceso
        ) {

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

                    Column {

                        Text(

                            text =
                                "Turno #${turno.numero}",

                            color =
                                NegroPrincipal,

                            fontSize =
                                18.sp,

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
                                NegroPrincipal
                        )


                        Text(

                            text =
                                "${turno.personas} personas · Mesa ${mesa.numero}",

                            modifier =
                                Modifier.padding(
                                    top =
                                        3.dp
                                ),

                            color =
                                GrisTexto
                        )


                        if (
                            personasExtra >
                            0
                        ) {

                            Spacer(

                                modifier =
                                    Modifier.height(
                                        12.dp
                                    )
                            )


                            Surface(

                                modifier =
                                    Modifier.fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(
                                        12.dp
                                    ),

                                color =
                                    Color(
                                        0xFFFFF7ED
                                    )
                            ) {

                                Text(

                                    text =
                                        "El grupo excede la capacidad por $personasExtra persona(s). Esta acción quedará registrada como excepción.",

                                    modifier =
                                        Modifier.padding(
                                            12.dp
                                        ),

                                    color =
                                        Color(
                                            0xFFB45309
                                        ),

                                    fontSize =
                                        11.sp,

                                    lineHeight =
                                        15.sp
                                )
                            }
                        }
                    }
                },

                confirmButton = {

                    Button(

                        onClick = {

                            turnoConfirmacion =
                                null


                            onAsignar(
                                turno
                            )
                        },

                        colors =
                            ButtonDefaults.buttonColors(

                                containerColor =
                                    NegroPrincipal
                            )
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
    }


    /*
     * =========================================================
     * CLIENTES RETIRADOS
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
                        "¿Los clientes se retiraron?"
                )
            },

            text = {

                Text(

                    text =
                        "La Mesa ${mesa.numero} quedará marcada como pendiente de limpieza."
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        confirmarSalida =
                            false


                        onClientesRetirados()
                    },

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                AmarilloMesa
                        )
                ) {

                    Text(
                        text =
                            "Sí, se retiraron"
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
                        "¿Confirmas que comenzará la limpieza de la Mesa ${mesa.numero}?"
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        confirmarInicioLimpieza =
                            false


                        onIniciarLimpieza()
                    },

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                NaranjaMesa
                        )
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
                        "Mesa lista"
                )
            },

            text = {

                Text(

                    text =
                        "¿La Mesa ${mesa.numero} ya está limpia y disponible para nuevos clientes?"
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        confirmarFinLimpieza =
                            false


                        onFinalizarLimpieza()
                    },

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                VerdeSmartTable
                        )
                ) {

                    Text(
                        text =
                            "Marcar disponible"
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

        MensajeEstado(

            titulo =
                "Mesa disponible",

            descripcion =
                "No hay acciones necesarias para tu rol.",

            color =
                VerdeSmartTable
        )


        return
    }


    Text(

        text =
            "¿Quién se sentó aquí?",

        color =
            NegroPrincipal,

        fontWeight =
            FontWeight.Bold,

        fontSize =
            19.sp
    )


    Text(

        text =
            "Verifica el número de turno y el nombre antes de confirmar.",

        modifier =
            Modifier.padding(
                top =
                    4.dp
            ),

        color =
            GrisTexto,

        fontSize =
            11.sp,

        lineHeight =
            15.sp
    )


    Spacer(

        modifier =
            Modifier.height(
                15.dp
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

        MensajeEstado(

            titulo =
                "Sin turnos pendientes",

            descripcion =
                "No hay clientes esperando que puedan asignarse en este momento.",

            color =
                GrisTexto
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
                9.dp
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

            val capacidad =
                contextoMesa
                    ?.mesa
                    ?.capacidad
                    ?: 0


            val excede =
                turno.personas >
                        capacidad


            val usuarioPuedeAutorizarExceso =
                rolUsuario in
                        listOf(
                            "ADMIN",
                            "HOSTESS"
                        )


            TurnoCard(

                turno =
                    turno,

                capacidadMesa =
                    capacidad,

                habilitado =
                    !ejecutandoAccion &&
                            (
                                    !excede ||
                                            usuarioPuedeAutorizarExceso
                                    ),

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


    Card(

        onClick =
            onClick,

        enabled =
            habilitado,

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =

                    when {

                        !habilitado ->

                            Color(
                                0xFFF8FAFC
                            )


                        turno.coincidePreferencia ->

                            VerdeSuave


                        else ->

                            Color(
                                0xFFF8FAFC
                            )
                    }
            ),

        elevation =
            CardDefaults.cardElevation(

                defaultElevation =
                    0.dp
            )
    ) {

        Row(

            modifier =
                Modifier.padding(
                    14.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(

                modifier =
                    Modifier.size(
                        42.dp
                    ),

                shape =
                    RoundedCornerShape(
                        13.dp
                    ),

                color =
                    if (
                        habilitado
                    ) {

                        NegroPrincipal

                    } else {

                        Color(
                            0xFFCBD5E1
                        )
                    }
            ) {

                Box(

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text =
                            "#${turno.numero}",

                        color =
                            Color.White,

                        fontSize =
                            11.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }


            Spacer(

                modifier =
                    Modifier.width(
                        11.dp
                    )
            )


            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(

                    text =
                        turno.nombre,

                    color =
                        if (
                            habilitado
                        ) {

                            NegroPrincipal

                        } else {

                            GrisTexto
                        },

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        14.sp
                )


                Text(

                    text =
                        "${turno.personas} personas · ${
                            turno.pisoPreferido
                                ?.nombre
                                ?: "Sin preferencia"
                        }",

                    modifier =
                        Modifier.padding(
                            top =
                                2.dp
                        ),

                    color =
                        GrisTexto,

                    fontSize =
                        10.sp
                )


                if (
                    turno.coincidePreferencia &&
                    habilitado
                ) {

                    Text(

                        text =
                            "Coincide con la zona preferida",

                        modifier =
                            Modifier.padding(
                                top =
                                    4.dp
                            ),

                        color =
                            VerdeSmartTable,

                        fontSize =
                            9.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }


                if (
                    !habilitado &&
                    personasExtra >
                    0
                ) {

                    Text(

                        text =
                            "Requiere autorización por capacidad",

                        modifier =
                            Modifier.padding(
                                top =
                                    4.dp
                            ),

                        color =
                            Color(
                                0xFFB45309
                            ),

                        fontSize =
                            9.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }


            if (
                personasExtra >
                0
            ) {

                Surface(

                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),

                    color =
                        Color(
                            0xFFFFF7ED
                        )
                ) {

                    Text(

                        text =
                            "+$personasExtra",

                        modifier =
                            Modifier.padding(

                                horizontal =
                                    8.dp,

                                vertical =
                                    5.dp
                            ),

                        color =
                            Color(
                                0xFFB45309
                            ),

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            10.sp
                    )
                }

            } else {

                Text(

                    text =
                        "Asignar",

                    color =
                        if (
                            habilitado
                        ) {

                            VerdeSmartTable

                        } else {

                            Color(
                                0xFF94A3B8
                            )
                        },

                    fontSize =
                        10.sp,

                    fontWeight =
                        FontWeight.Bold
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

        Surface(

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    16.dp
                ),

            color =
                Color(
                    0xFFFEF2F2
                )
        ) {

            Column(

                modifier =
                    Modifier.padding(
                        15.dp
                    )
            ) {

                Text(

                    text =
                        "Turno #${turno.numero}",

                    color =
                        NegroPrincipal,

                    fontSize =
                        19.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text =
                        turno.nombre,

                    modifier =
                        Modifier.padding(
                            top =
                                3.dp
                        ),

                    color =
                        NegroPrincipal,

                    fontSize =
                        14.sp
                )


                Text(

                    text =
                        "${turno.personas} personas",

                    modifier =
                        Modifier.padding(
                            top =
                                2.dp
                        ),

                    color =
                        GrisTexto,

                    fontSize =
                        11.sp
                )


                if (
                    turno.excedeCapacidad
                ) {

                    Text(

                        text =
                            "Excepción autorizada · +${turno.personasExtra}",

                        modifier =
                            Modifier.padding(
                                top =
                                    7.dp
                            ),

                        color =
                            Color(
                                0xFFB45309
                            ),

                        fontWeight =
                            FontWeight.SemiBold,

                        fontSize =
                            10.sp
                    )
                }
            }
        }

    } else {

        MensajeEstado(

            titulo =
                "Mesa ocupada",

            descripcion =
                "Existe una sesión activa en esta mesa.",

            color =
                RojoMesa
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
                Modifier
                    .fillMaxWidth()
                    .height(
                        52.dp
                    ),

            shape =
                RoundedCornerShape(
                    14.dp
                ),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        AmarilloMesa
                )
        ) {

            Text(

                text =
                    "Clientes se retiraron",

                fontWeight =
                    FontWeight.Bold
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

    MensajeEstado(

        titulo =
            "Pendiente de limpieza",

        descripcion =
            "La mesa necesita atención antes de volver a recibir clientes.",

        color =
            AmarilloMesa
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
                Modifier
                    .fillMaxWidth()
                    .height(
                        52.dp
                    ),

            shape =
                RoundedCornerShape(
                    14.dp
                ),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        NaranjaMesa
                )
        ) {

            Text(

                text =
                    "Iniciar limpieza",

                fontWeight =
                    FontWeight.Bold
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

    MensajeEstado(

        titulo =
            "Limpieza en proceso",

        descripcion =
            "Cuando termine, confirma para liberar inmediatamente la mesa.",

        color =
            NaranjaMesa
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
                Modifier
                    .fillMaxWidth()
                    .height(
                        52.dp
                    ),

            shape =
                RoundedCornerShape(
                    14.dp
                ),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        VerdeSmartTable
                )
        ) {

            Text(

                text =
                    "Finalizar y liberar mesa",

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


@Composable
private fun MensajeEstado(
    titulo: String,
    descripcion: String,
    color: Color
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        color =
            color.copy(
                alpha =
                    0.09f
            )
    ) {

        Row(

            modifier =
                Modifier.padding(
                    15.dp
                ),

            verticalAlignment =
                Alignment.Top
        ) {

            Box(

                modifier =
                    Modifier
                        .padding(
                            top =
                                4.dp
                        )
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
                        10.dp
                    )
            )


            Column {

                Text(

                    text =
                        titulo,

                    color =
                        NegroPrincipal,

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text =
                        descripcion,

                    modifier =
                        Modifier.padding(
                            top =
                                3.dp
                        ),

                    color =
                        GrisTexto,

                    fontSize =
                        11.sp,

                    lineHeight =
                        15.sp
                )
            }
        }
    }
}


@Composable
private fun EstadoMesaChip(
    estado: String
) {

    Surface(

        shape =
            RoundedCornerShape(
                50.dp
            ),

        color =
            colorMesa(
                estado
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
                        11.dp,

                    vertical =
                        6.dp
                ),

            color =
                Color.White,

            fontSize =
                9.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}


@Composable
private fun EstadoVacio(
    titulo: String,
    descripcion: String,
    onReintentar: () -> Unit
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                20.dp
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White
            )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        22.dp
                    ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(

                text =
                    titulo,

                color =
                    NegroPrincipal,

                fontWeight =
                    FontWeight.Bold,

                textAlign =
                    TextAlign.Center
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
                    GrisTexto,

                fontSize =
                    12.sp,

                textAlign =
                    TextAlign.Center
            )


            Spacer(

                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            Button(

                onClick =
                    onReintentar,

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            NegroPrincipal
                    )
            ) {

                Text(
                    text =
                        "Reintentar"
                )
            }
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
            VerdeSmartTable


        "OCCUPIED" ->
            RojoMesa


        "DIRTY" ->
            AmarilloMesa


        "CLEANING" ->
            NaranjaMesa


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