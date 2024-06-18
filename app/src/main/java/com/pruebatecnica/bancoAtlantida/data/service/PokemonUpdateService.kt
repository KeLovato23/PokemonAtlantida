package com.pruebatecnica.bancoAtlantida.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.lifecycleScope
import com.pruebatecnica.bancoAtlantida.MainActivity
import com.pruebatecnica.bancoAtlantida.R
import com.pruebatecnica.bancoAtlantida.data.local.PokemonDatabase
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonApi
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonDetailsApi
import com.pruebatecnica.bancoAtlantida.data.repository.PokemonRepository
import com.pruebatecnica.bancoAtlantida.ui.viewmodel.PokemonViewModel
import com.pruebatecnica.bancoAtlantida.ui.viewmodel.PokemonViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PokemonUpdateService : LifecycleService() {
    private lateinit var pokemonViewModel: PokemonViewModel
    private lateinit var pokemonRepository: PokemonRepository
    private var offset = 0
    override fun onCreate() {
        super.onCreate()
        // Inicializar el PokemonRepository
        val pokemonApi = createPokemonApi()
        val pokemonDetailsApi = createPokemonDetailsApi()
        val database = PokemonDatabase.getDatabase(applicationContext)
        val pokemonDao = database.pokemonDao()
        pokemonRepository = PokemonRepository(pokemonApi, pokemonDetailsApi, pokemonDao)


        val viewModelStore = ViewModelStore()


        val pokemonViewModelFactory = PokemonViewModelFactory(pokemonRepository)
        pokemonViewModel = ViewModelProvider(viewModelStore, pokemonViewModelFactory).get(PokemonViewModel::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startBackgroundWork()
        return START_STICKY
    }

    private fun startBackgroundWork() {
        lifecycleScope.launch {
            while (true) {
                try {
                    pokemonViewModel.getPokemons(10, offset)
                    offset += 10
                    Log.i("MOSTRARCONTADO", offset.toString())
                    showNotification()
                } catch (e: Exception) {
                    Log.e("PokemonUpdateService", "Error al obtener los Pokémon: ${e.message}")
                }
                delay(30000)
            }
        }
    }

    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "pokemon_update_channel"
        val channelName = "Pokémon Update"
        val importance = NotificationManager.IMPORTANCE_DEFAULT


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }


        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Actualización Pokémon")
            .setContentText("La lista de Pokémon ha sido actualizada.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun createPokemonApi(): PokemonApi {
        val intercepter = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(intercepter)
                .connectTimeout(3,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(25,TimeUnit.SECONDS)

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
                // time out setting
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
}