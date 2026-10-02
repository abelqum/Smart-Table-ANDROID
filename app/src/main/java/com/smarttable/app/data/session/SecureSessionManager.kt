package com.smarttable.app.data.session


import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64

import java.security.KeyStore

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


class SecureSessionManager(
    context: Context
) {

    companion object {

        private const val KEYSTORE_PROVIDER =
            "AndroidKeyStore"

        private const val KEY_ALIAS =
            "smarttable_session_key"

        private const val TRANSFORMATION =
            "AES/GCM/NoPadding"

        private const val PREFS_NAME =
            "smarttable_secure_session"

        private const val TOKEN_KEY =
            "jwt_encrypted"

        private const val IV_KEY =
            "jwt_iv"
    }


    private val preferencias =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )


    private fun obtenerOCrearClave(): SecretKey {

        val keyStore =
            KeyStore.getInstance(
                KEYSTORE_PROVIDER
            ).apply {
                load(null)
            }


        val claveExistente =
            keyStore.getKey(
                KEY_ALIAS,
                null
            ) as? SecretKey


        if (claveExistente != null) {
            return claveExistente
        }


        val generador =
            KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )


        val especificacion =
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,

                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(
                    KeyProperties.BLOCK_MODE_GCM
                )
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .build()


        generador.init(
            especificacion
        )


        return generador.generateKey()
    }


    fun guardarToken(
        token: String
    ) {

        val clave =
            obtenerOCrearClave()


        val cipher =
            Cipher.getInstance(
                TRANSFORMATION
            )


        cipher.init(
            Cipher.ENCRYPT_MODE,
            clave
        )


        val tokenCifrado =
            cipher.doFinal(
                token.toByteArray(
                    Charsets.UTF_8
                )
            )


        val tokenBase64 =
            Base64.encodeToString(
                tokenCifrado,
                Base64.NO_WRAP
            )


        val ivBase64 =
            Base64.encodeToString(
                cipher.iv,
                Base64.NO_WRAP
            )


        preferencias
            .edit()
            .putString(
                TOKEN_KEY,
                tokenBase64
            )
            .putString(
                IV_KEY,
                ivBase64
            )
            .apply()
    }


    fun obtenerToken(): String? {

        val tokenBase64 =
            preferencias.getString(
                TOKEN_KEY,
                null
            )
                ?: return null


        val ivBase64 =
            preferencias.getString(
                IV_KEY,
                null
            )
                ?: return null


        return try {

            val clave =
                obtenerOCrearClave()


            val cipher =
                Cipher.getInstance(
                    TRANSFORMATION
                )


            val iv =
                Base64.decode(
                    ivBase64,
                    Base64.NO_WRAP
                )


            val especificacionGcm =
                GCMParameterSpec(
                    128,
                    iv
                )


            cipher.init(
                Cipher.DECRYPT_MODE,
                clave,
                especificacionGcm
            )


            val datosCifrados =
                Base64.decode(
                    tokenBase64,
                    Base64.NO_WRAP
                )


            val datos =
                cipher.doFinal(
                    datosCifrados
                )


            String(
                datos,
                Charsets.UTF_8
            )

        } catch (
            error: Exception
        ) {

            cerrarSesion()

            null
        }
    }


    fun cerrarSesion() {

        preferencias
            .edit()
            .clear()
            .apply()
    }


    fun existeSesion(): Boolean {

        return obtenerToken() != null
    }
}