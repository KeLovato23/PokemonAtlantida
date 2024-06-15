package com.pruebatecnica.bancoAtlantida.data

import com.pruebatecnica.bancoAtlantida.data.model.Pokemon
import com.pruebatecnica.bancoAtlantida.data.model.PokemonResponse
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonApi

class FakePokemonApi : PokemonApi {
    override suspend fun getPokemons(limit: Int, offset: Int): PokemonResponse {
        // Retorna una respuesta de ejemplo
        return PokemonResponse(
            count = 3,
            next = null,
            previous = null,
            results = listOf(
                Pokemon(name = "Bulbasaur", url = "https://example.com/pokemon/1", abilities = emptyList()),
                Pokemon(name = "Ivysaur", url = "https://example.com/pokemon/2", abilities = emptyList()),
                Pokemon(name = "Venusaur", url = "https://example.com/pokemon/3", abilities = emptyList())
            )
        )
    }
}