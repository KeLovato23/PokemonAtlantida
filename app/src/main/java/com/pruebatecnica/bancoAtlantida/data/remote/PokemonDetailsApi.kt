package com.pruebatecnica.bancoAtlantida.data.remote

import com.pruebatecnica.bancoAtlantida.data.model.PokemonDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonDetailsApi {
    @GET("pokemon/{id}")
    suspend fun getPokemonDetails(@Path("id") id: Int): PokemonDetailsResponse
}