package com.pruebatecnica.bancoAtlantida.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey

@Entity(tableName = "pokemons")
data class PokemonEntity(
    @PrimaryKey val url: String,
    val name: String,
    val abilities: String // Nuevo campo para almacenar las habilidades
)