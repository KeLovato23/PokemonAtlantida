package com.pruebatecnica.bancoAtlantida.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemons(pokemons: List<PokemonEntity>)

    @Query("SELECT * FROM pokemons")
    fun getAllPokemons(): List<PokemonEntity>
}