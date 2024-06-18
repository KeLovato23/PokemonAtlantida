package com.pruebatecnica.bancoAtlantida

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import com.pruebatecnica.bancoAtlantida.data.service.PokemonUpdateService
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

class MainActivity : AppCompatActivity() {
    private lateinit var pokemonViewModel: PokemonViewModel
    private lateinit var pokemonAdapter: PokemonAdapter
    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Verificar y solicitar los permisos de notificación
        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            Manifest.permission.POST_NOTIFICATIONS
        }

        if (ContextCompat.checkSelfPermission(
                this,
                notificationPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(notificationPermission),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Los permisos ya están concedidos, iniciar el servicio en segundo plano
            startPokemonUpdateService()
        }

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
        recyclerView.adapter = pokemonAdapter

        // Obtener la lista inicial de Pokémon
        CoroutineScope(Dispatchers.Main).launch {
            pokemonViewModel.getPokemons(15, 0)
        }

        // Observar los cambios en la lista de Pokémon y actualizar el adaptador
        pokemonViewModel.pokemons.observe(this, Observer { pokemons ->
            pokemonAdapter.submitList(pokemons)
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == "pokemon_update") {
            // Actualizar la lista de Pokémon
            updatePokemonList()
        }
    }

    private fun updatePokemonList() {
        // Obtener la lista actualizada de Pokémon desde el ViewModel
        pokemonViewModel.getPokemons(pokemonViewModel.getOffset(), 10)
    }

    private fun createPokemonApi(): PokemonApi {
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
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
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
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
                // Los permisos han sido concedidos, iniciar el servicio en segundo plano
                startPokemonUpdateService()
            } else {
                // Los permisos han sido denegados, mostrar un mensaje al usuario
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