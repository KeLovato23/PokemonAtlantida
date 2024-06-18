package com.pruebatecnica.bancoAtlantida

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pruebatecnica.bancoAtlantida.data.remote.FakePokemonApi
import com.pruebatecnica.bancoAtlantida.data.remote.FakePokemonDetailsApi
import com.pruebatecnica.bancoAtlantida.data.local.PokemonDao
import com.pruebatecnica.bancoAtlantida.data.local.PokemonDatabase
import com.pruebatecnica.bancoAtlantida.data.model.PokemonEntity

import com.pruebatecnica.bancoAtlantida.data.repository.PokemonRepository
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonRepositoryTest {
    private lateinit var pokemonRepository: PokemonRepository
    private lateinit var pokemonDao: PokemonDao
    private lateinit var db: PokemonDatabase

    @Before
    fun setUp() {

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PokemonDatabase::class.java).build()
        pokemonDao = db.pokemonDao()


        val pokemonApi = FakePokemonApi()
        val pokemonDetailsApi = FakePokemonDetailsApi()
        pokemonRepository = PokemonRepository(pokemonApi, pokemonDetailsApi, pokemonDao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testStoringAndRetrievingPokemonList() = runBlocking {
        // Preparar datos de prueba
        val pokemonList = listOf(
            PokemonEntity("https://example.com/pokemon/1", "Bulbasaur", "Overgrow, Chlorophyll"),
            PokemonEntity("https://example.com/pokemon/2", "Ivysaur", "Overgrow, Chlorophyll"),
            PokemonEntity("https://example.com/pokemon/3", "Venusaur", "Overgrow, Chlorophyll")
        )


        pokemonDao.insertPokemons(pokemonList)
        val retrievedPokemonList = pokemonDao.getPokemons(0, pokemonList.size)
        assertThat(retrievedPokemonList, equalTo(pokemonList))
    }
}