package com.pruebatecnica.bancoAtlantida.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pruebatecnica.bancoAtlantida.data.model.PokemonEntity

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemons(pokemons: List<PokemonEntity>)

    @Query("SELECT * FROM pokemons LIMIT :limit OFFSET :offset")
    fun getPokemons(offset: Int, limit: Int): List<PokemonEntity>
}