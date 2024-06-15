package com.pruebatecnica.bancoAtlantida

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pruebatecnica.bancoAtlantida.data.PokemonDatabase
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonApi
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonDetailsApi
import com.pruebatecnica.bancoAtlantida.data.repository.PokemonRepository
import com.pruebatecnica.bancoAtlantida.ui.adapter.PokemonAdapter
import com.pruebatecnica.bancoAtlantida.ui.viewmodel.PokemonViewModel
import com.pruebatecnica.bancoAtlantida.ui.viewmodel.PokemonViewModelFactory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class MainActivity : AppCompatActivity() {
    private lateinit var pokemonViewModel: PokemonViewModel
    private lateinit var pokemonAdapter: PokemonAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Crear una instancia de PokemonApi utilizando Retrofit
        val pokemonApi = createPokemonApi()

        // Crear una instancia de PokemonDetailsApi utilizando Retrofit
        val pokemonDetailsApi = createPokemonDetailsApi()

        // Obtener una instancia de la base de datos y el DAO
        val database = PokemonDatabase.getDatabase(this)
        val pokemonDao = database.pokemonDao()

        // Crear una instancia de PokemonRepository
        val pokemonRepository = PokemonRepository(pokemonApi, pokemonDetailsApi, pokemonDao)

        // Crear una instancia de PokemonViewModelFactory
        val pokemonViewModelFactory = PokemonViewModelFactory(pokemonRepository)

        // Obtener una instancia de PokemonViewModel utilizando el PokemonViewModelFactory
        pokemonViewModel = ViewModelProvider(this, pokemonViewModelFactory).get(PokemonViewModel::class.java)

        // Crear una instancia de PokemonAdapter y pasar el PokemonViewModel
        pokemonAdapter = PokemonAdapter(pokemonViewModel)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        // recyclerView.layoutManager = GridLayoutManager(this, 2) // Dos columnas
        recyclerView.adapter = pokemonAdapter

        // Obtener la lista de Pokémon
        CoroutineScope(Dispatchers.Main).launch {
            pokemonViewModel.getPokemons(15, 0)
        }

        // Observar los cambios en la lista de Pokémon y actualizar el adaptador
        pokemonViewModel.pokemons.observe(this, Observer { pokemons ->
            pokemonAdapter.submitList(pokemons)
        })
    }

    private fun createPokemonApi(): PokemonApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PokemonApi::class.java)
    }

    private fun createPokemonDetailsApi(): PokemonDetailsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PokemonDetailsApi::class.java)
    }


}