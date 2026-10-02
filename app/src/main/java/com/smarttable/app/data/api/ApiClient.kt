package com.smarttable.app.data.api


import android.content.Context
import android.util.Log

import com.smarttable.app.data.session.SecureSessionManager

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit


object ApiClient {

    /*
     * Gracias a:
     *
     * adb reverse tcp:4000 tcp:4000
     *
     * Android puede acceder al backend de Windows
     * mediante 127.0.0.1.
     */
    private const val BASE_URL =
        "http://127.0.0.1:4000/api/"


    lateinit var api: SmartTableApi
        private set


    fun inicializar(
        context: Context
    ) {

        val contexto =
            context.applicationContext


        val sesion =
            SecureSessionManager(
                contexto
            )


        val logging =
            HttpLoggingInterceptor { mensaje ->

                Log.d(
                    "SmartTableHTTP",
                    mensaje
                )
            }.apply {

                /*
                 * BASIC muestra:
                 *
                 * método
                 * URL
                 * código HTTP
                 * tiempo
                 *
                 * pero NO imprime el JWT ni cuerpos.
                 */
                level =
                    HttpLoggingInterceptor.Level.BASIC
            }


        val clienteHttp =
            OkHttpClient.Builder()

                .addInterceptor { cadena ->

                    val peticionOriginal =
                        cadena.request()


                    val token =
                        sesion.obtenerToken()


                    val constructor =
                        peticionOriginal
                            .newBuilder()
                            .header(
                                "Accept",
                                "application/json"
                            )
                            .header(
                                "X-SmartTable-Origin",
                                "ANDROID"
                            )


                    if (
                        !token.isNullOrBlank()
                    ) {

                        constructor.header(
                            "Authorization",
                            "Bearer $token"
                        )
                    }


                    cadena.proceed(
                        constructor.build()
                    )
                }

                .addInterceptor(
                    logging
                )

                .connectTimeout(
                    15,
                    TimeUnit.SECONDS
                )

                .readTimeout(
                    15,
                    TimeUnit.SECONDS
                )

                .writeTimeout(
                    15,
                    TimeUnit.SECONDS
                )

                .build()


        api =
            Retrofit.Builder()

                .baseUrl(
                    BASE_URL
                )

                .client(
                    clienteHttp
                )

                .addConverterFactory(
                    GsonConverterFactory.create()
                )

                .build()

                .create(
                    SmartTableApi::class.java
                )


        Log.d(
            "SmartTableHTTP",
            "ApiClient inicializado con $BASE_URL"
        )
    }
}