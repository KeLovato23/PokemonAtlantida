package com.pruebatecnica.bancoAtlantida

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.pruebatecnica.bancoAtlantida.data.local.PokemonDatabase
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonApi
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonDetailsApi
import com.pruebatecnica.bancoAtlantida.data.repository.PokemonRepository
import com.pruebatecnica.bancoAtlantida.ui.adapter.PokemonAdapter
import com.pruebatecnica.bancoAtlantida.ui.viewmodel.PokemonViewModel
import com.pruebatecnica.bancoAtlantida.ui.viewmodel.PokemonViewModelFactory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.Manifest
class MainActivity : AppCompatActivity() {
    private lateinit var pokemonViewModel: PokemonViewModel
    private lateinit var pokemonAdapter: PokemonAdapter
    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Los permisos ya están concedidos, iniciar el servicio en segundo plano
            startPokemonUpdateService()
        }



        val intent = Intent(this, PokemonUpdateService::class.java)
        startService(intent)






        val pokemonApi = createPokemonApi()
        val pokemonDetailsApi = createPokemonDetailsApi()
        val database = PokemonDatabase.getDatabase(this)
        val pokemonDao = database.pokemonDao()
        val pokemonRepository = PokemonRepository(pokemonApi, pokemonDetailsApi, pokemonDao)
        val pokemonViewModelFactory = PokemonViewModelFactory(pokemonRepository)

        pokemonViewModel = ViewModelProvider(this, pokemonViewModelFactory).get(PokemonViewModel::class.java)
        pokemonAdapter = PokemonAdapter(pokemonViewModel)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = pokemonAdapter


        CoroutineScope(Dispatchers.Main).launch {
            pokemonViewModel.getPokemons(15, 0)
        }

        pokemonViewModel.pokemons.observe(this, Observer { pokemons ->
            pokemonAdapter.submitList(pokemons)
        })
    }

    private fun createPokemonApi(): PokemonApi {
        val intercepter = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(intercepter)
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)

        }.build()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()



        return retrofit.create(PokemonApi::class.java)
    }

    private fun createPokemonDetailsApi(): PokemonDetailsApi {

        val intercepter = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(intercepter)

                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)

        }.build()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(PokemonDetailsApi::class.java)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPokemonUpdateService()
            } else {

                Toast.makeText(
                    this,
                    "Los permisos de notificación son necesarios para recibir actualizaciones de Pokémon.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startPokemonUpdateService() {
        val intent = Intent(this, PokemonUpdateService::class.java)
        startService(intent)
    }
}