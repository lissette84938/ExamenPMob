package com.example.examenpmob.ws

import retrofit2.http.GET

interface DolaresService {
    //https://mindicador.cl/api/dolar/fecha
    @GET("dolares")
    suspend fun getDolares(): Dolares

}