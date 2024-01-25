package com.example.examenpmob.ws

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Fabrica {
    fun getBaseUrl(): String {
        return "https://mindicador.cl/api/"
    }
    fun getService( serviceClass: Class<*>): Any {
        val adapter = KotlinJsonAdapterFactory()
        val moshi = Moshi.Builder()
            .add(adapter)
            .build()
        val converter = MoshiConverterFactory.create(moshi)
        val retrofit = Retrofit.Builder()
            .addConverterFactory(converter)
            .baseUrl(getBaseUrl())
            .build()
        return retrofit.create(serviceClass)
    }

    fun getDolaresService(): DolaresService {
        return getService(DolaresService::class.java) as DolaresService
    }
}