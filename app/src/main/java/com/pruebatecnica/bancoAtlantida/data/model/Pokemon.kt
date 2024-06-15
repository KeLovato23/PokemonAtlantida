    package com.pruebatecnica.bancoAtlantida.data.model

    data class Pokemon(
        val name: String,
        val url: String,
        val abilities: List<String>
    )

    fun Pokemon.withAbilities(abilities: List<String>): Pokemon {
        return copy(abilities = abilities)
    }