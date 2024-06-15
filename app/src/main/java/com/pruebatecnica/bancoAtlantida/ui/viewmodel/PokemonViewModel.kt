package com.pruebatecnica.bancoAtlantida.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pruebatecnica.bancoAtlantida.data.model.Pokemon
import com.pruebatecnica.bancoAtlantida.data.model.PokemonDetailsResponse
import com.pruebatecnica.bancoAtlantida.data.repository.PokemonRepository

// PokemonViewModel.kt
class PokemonViewModel(private val pokemonRepository: PokemonRepository) : ViewModel() {
    private val _pokemons = MutableLiveData<List<Pokemon>>()
    val pokemons: LiveData<List<Pokemon>> = _pokemons

    suspend fun getPokemons(limit: Int, offset: Int) {
        try {
            val pokemonList = pokemonRepository.getPokemons(limit, offset)
            _pokemons.value = pokemonList
        } catch (e: Exception) {
            // Manejar excepción
            Log.e("PokemonViewModel", "Error al obtener los Pokémon: ${e.message}")
        }
    }

    suspend fun getPokemonImageUrl(pokemonUrl: String): String {
        return try {
            val pokemonId = getPokemonIdFromUrl(pokemonUrl)
            val pokemonDetails = pokemonRepository.getPokemonDetails(pokemonId)
            pokemonDetails.sprites.front_default
        } catch (e: Exception) {
            // Manejar excepción
            Log.e("PokemonViewModel", "Error al obtener la URL de la imagen: ${e.message}")
            ""
        }
    }

    suspend fun getPokemonDetails(pokemonUrl: String): PokemonDetailsResponse? {
        return try {
            val pokemonId = getPokemonIdFromUrl(pokemonUrl)
            pokemonRepository.getPokemonDetails(pokemonId)
        } catch (e: Exception) {
            // Manejar excepción
            Log.e("PokemonViewModel", "Error al obtener los detalles del Pokémon: ${e.message}")
            null
        }
    }

    private fun getPokemonIdFromUrl(url: String): Int {
        val parts = url.split("/")
        return parts[parts.size - 2].toInt()
    }
}