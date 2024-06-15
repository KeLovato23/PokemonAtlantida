package com.pruebatecnica.bancoAtlantida.data.repository

import android.util.Log
import com.google.gson.Gson
import com.pruebatecnica.bancoAtlantida.data.PokemonDao
import com.pruebatecnica.bancoAtlantida.data.PokemonEntity
import com.pruebatecnica.bancoAtlantida.data.model.Pokemon
import com.pruebatecnica.bancoAtlantida.data.model.PokemonDetailsResponse
import com.pruebatecnica.bancoAtlantida.data.model.PokemonResponse
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonApi
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonDetailsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonRepository(
    private val pokemonApi: PokemonApi,
    private val pokemonDetailsApi: PokemonDetailsApi,
    private val pokemonDao: PokemonDao
) {
    suspend fun getPokemons(limit: Int, offset: Int): List<Pokemon> {
        return withContext(Dispatchers.IO) {
            val cachedPokemons = pokemonDao.getAllPokemons()
            if (cachedPokemons.isNotEmpty()) {
                cachedPokemons.map { it.toPokemon() }
            } else {
                val response = pokemonApi.getPokemons(limit, offset)
                val pokemons = response.results.map { pokemon ->
                    val pokemonDetails = pokemonDetailsApi.getPokemonDetails(getPokemonIdFromUrl(pokemon.url))
                    val abilities = pokemonDetails.abilities.map { it.ability.name }.joinToString(", ")
                    PokemonEntity(pokemon.url, pokemon.name, abilities)
                }
                pokemonDao.insertPokemons(pokemons)
                pokemons.map { it.toPokemon() }
            }
        }
    }

    suspend fun getPokemonDetails(pokemonId: Int): PokemonDetailsResponse {
        return pokemonDetailsApi.getPokemonDetails(pokemonId)
    }

    private fun Pokemon.toPokemonEntity(): PokemonEntity {
        return PokemonEntity(url, name, abilities.joinToString(", "))
    }

    private fun PokemonEntity.toPokemon(): Pokemon {
        val abilities = this.abilities.split(", ")
        return Pokemon(name, url, abilities)
    }

    private fun getPokemonIdFromUrl(url: String): Int {
        val parts = url.split("/")
        return parts[parts.size - 2].toInt()
    }
}