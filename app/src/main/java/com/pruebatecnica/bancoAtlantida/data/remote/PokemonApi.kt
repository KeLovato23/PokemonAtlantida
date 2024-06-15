package com.pruebatecnica.bancoAtlantida.data.remote

import com.pruebatecnica.bancoAtlantida.data.model.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Query

// PokemonApi.kt
interface PokemonApi {
    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int, @Query("offset") offset: Int): PokemonResponse
}