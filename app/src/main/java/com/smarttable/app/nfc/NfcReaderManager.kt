package com.smarttable.app.nfc


import android.app.Activity

import android.content.Context

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag

import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

import android.util.Log

import java.nio.charset.Charset


class NfcReaderManager(
    private val activity: Activity
) : NfcAdapter.ReaderCallback {

    private val adaptadorNfc =
        NfcAdapter.getDefaultAdapter(
            activity
        )


    private var ultimoToken: String? =
        null


    private var ultimoEscaneoMs: Long =
        0


    companion object {

        private val FORMATO_TOKEN =
            Regex(
                "^v=1;t=([a-fA-F0-9]{32})$"
            )


        private const val TIEMPO_ANTIREBOTE_MS =
            1500L
    }


    fun activar() {

        if (
            adaptadorNfc ==
            null
        ) {

            NfcEventBus
                .actualizarEstado(
                    EstadoNfc.NO_DISPONIBLE
                )


            return
        }


        if (
            !adaptadorNfc.isEnabled
        ) {

            NfcEventBus
                .actualizarEstado(
                    EstadoNfc.DESACTIVADO
                )


            return
        }


        adaptadorNfc.enableReaderMode(

            activity,

            this,

            NfcAdapter.FLAG_READER_NFC_A,

            null
        )


        NfcEventBus
            .actualizarEstado(
                EstadoNfc.ACTIVO
            )


        Log.d(
            "SmartTableNFC",
            "ReaderMode NFC activado."
        )
    }


    fun desactivar() {

        if (
            adaptadorNfc ==
            null
        ) {

            return
        }


        try {

            adaptadorNfc
                .disableReaderMode(
                    activity
                )

        } catch (
            error: Exception
        ) {

            Log.e(
                "SmartTableNFC",
                "Error al desactivar ReaderMode.",
                error
            )
        }
    }


    override fun onTagDiscovered(
        tag: Tag
    ) {

        val solicitudEscritura =
            NfcEventBus
                .solicitudEscritura
                .value


        /*
         * Si el administrador está vinculando
         * una etiqueta, la misma aproximación NFC
         * se utiliza para ESCRIBIR.
         */
        if (
            solicitudEscritura !=
            null
        ) {

            escribirEtiqueta(
                tag,
                solicitudEscritura
            )


            return
        }


        leerEtiqueta(
            tag
        )
    }


    private fun leerEtiqueta(
        tag: Tag
    ) {

        try {

            val contenido =
                leerContenidoNdef(
                    tag
                )


            if (
                contenido ==
                null
            ) {

                NfcEventBus
                    .publicarError(
                        "La etiqueta NFC no contiene datos SmartTable."
                    )


                return
            }


            val coincidencia =
                FORMATO_TOKEN
                    .matchEntire(
                        contenido.trim()
                    )


            if (
                coincidencia ==
                null
            ) {

                NfcEventBus
                    .publicarError(
                        "La etiqueta NFC no pertenece a SmartTable."
                    )


                return
            }


            val token =
                coincidencia
                    .groupValues[1]
                    .lowercase()


            val ahora =
                System.currentTimeMillis()


            if (
                token ==
                ultimoToken &&
                ahora -
                ultimoEscaneoMs <
                TIEMPO_ANTIREBOTE_MS
            ) {

                return
            }


            ultimoToken =
                token


            ultimoEscaneoMs =
                ahora


            vibrarExito()


            Log.d(
                "SmartTableNFC",
                "Etiqueta SmartTable detectada."
            )


            NfcEventBus
                .publicarToken(
                    token
                )

        } catch (
            error: Exception
        ) {

            Log.e(
                "SmartTableNFC",
                "Error leyendo etiqueta.",
                error
            )


            NfcEventBus
                .publicarError(
                    "No fue posible leer la etiqueta NFC."
                )
        }
    }


    private fun escribirEtiqueta(
        tag: Tag,
        solicitud: SolicitudEscrituraNfc
    ) {

        try {

            val registro =
                NdefRecord.createTextRecord(
                    "es",
                    solicitud.contenido
                )


            val mensaje =
                NdefMessage(
                    arrayOf(
                        registro
                    )
                )


            val bytesNecesarios =
                mensaje
                    .toByteArray()
                    .size


            val ndef =
                Ndef.get(
                    tag
                )


            if (
                ndef !=
                null
            ) {

                try {

                    ndef.connect()


                    if (
                        !ndef.isWritable
                    ) {

                        throw IllegalStateException(
                            "La etiqueta NFC está protegida contra escritura."
                        )
                    }


                    if (
                        ndef.maxSize <
                        bytesNecesarios
                    ) {

                        throw IllegalStateException(
                            "La etiqueta NFC no tiene espacio suficiente."
                        )
                    }


                    ndef.writeNdefMessage(
                        mensaje
                    )

                } finally {

                    try {

                        ndef.close()

                    } catch (
                        _: Exception
                    ) {
                    }
                }

            } else {

                /*
                 * Algunas etiquetas nuevas todavía
                 * no están formateadas como NDEF.
                 */
                val formateable =
                    NdefFormatable.get(
                        tag
                    )
                        ?: throw IllegalStateException(
                            "Esta etiqueta NFC no puede utilizarse con SmartTable."
                        )


                try {

                    formateable.connect()


                    formateable.format(
                        mensaje
                    )

                } finally {

                    try {

                        formateable.close()

                    } catch (
                        _: Exception
                    ) {
                    }
                }
            }


            vibrarExito()


            Log.d(
                "SmartTableNFC",
                "Etiqueta NFC escrita correctamente."
            )


            NfcEventBus
                .publicarEscrituraExitosa(
                    solicitud
                )

        } catch (
            error: Exception
        ) {

            Log.e(
                "SmartTableNFC",
                "Error escribiendo etiqueta NFC.",
                error
            )


            NfcEventBus
                .publicarErrorEscritura(
                    solicitud =
                        solicitud,

                    mensaje =
                        error.message
                            ?: "No fue posible escribir la etiqueta NFC."
                )
        }
    }


    private fun leerContenidoNdef(
        tag: Tag
    ): String? {

        val ndef =
            Ndef.get(
                tag
            )
                ?: return null


        try {

            ndef.connect()


            val mensaje =
                ndef.ndefMessage
                    ?: ndef.cachedNdefMessage
                    ?: return null


            for (
            registro in
            mensaje.records
            ) {

                val texto =
                    leerRegistroTexto(
                        registro
                    )


                if (
                    texto !=
                    null
                ) {

                    return texto
                }
            }


            return null

        } finally {

            try {

                ndef.close()

            } catch (
                _: Exception
            ) {
            }
        }
    }


    private fun leerRegistroTexto(
        registro: NdefRecord
    ): String? {

        if (
            registro.tnf !=
            NdefRecord.TNF_WELL_KNOWN
        ) {

            return null
        }


        if (
            !registro.type.contentEquals(
                NdefRecord.RTD_TEXT
            )
        ) {

            return null
        }


        val payload =
            registro.payload


        if (
            payload.isEmpty()
        ) {

            return null
        }


        val estado =
            payload[0]
                .toInt()


        val usaUtf16 =
            estado and
                    0x80 !=
                    0


        val longitudIdioma =
            estado and
                    0x3F


        val inicioTexto =
            1 +
                    longitudIdioma


        if (
            inicioTexto >=
            payload.size
        ) {

            return null
        }


        val charset =
            if (
                usaUtf16
            ) {

                Charset.forName(
                    "UTF-16"
                )

            } else {

                Charsets.UTF_8
            }


        return String(
            payload,
            inicioTexto,
            payload.size -
                    inicioTexto,
            charset
        )
    }


    private fun vibrarExito() {

        try {

            val vibrator =
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.S
                ) {

                    val manager =
                        activity.getSystemService(
                            Context.VIBRATOR_MANAGER_SERVICE
                        ) as VibratorManager


                    manager.defaultVibrator

                } else {

                    @Suppress(
                        "DEPRECATION"
                    )

                    activity.getSystemService(
                        Context.VIBRATOR_SERVICE
                    ) as Vibrator
                }


            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    80,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )

        } catch (
            error: Exception
        ) {

            /*
             * Una falla de vibración nunca debe
             * impedir la operación NFC.
             */
        }
    }
}