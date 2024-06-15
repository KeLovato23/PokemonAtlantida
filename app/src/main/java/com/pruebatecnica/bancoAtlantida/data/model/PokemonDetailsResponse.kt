package com.pruebatecnica.bancoAtlantida.data.model

data class PokemonDetailsResponse(
    val id: Int,
    val name: String,
    val abilities: List<Ability>,
    val sprites: PokemonSprites
)

data class Ability(
    val ability: AbilityInfo,
    val is_hidden: Boolean,
    val slot: Int
)

data class AbilityInfo(
    val name: String,
    val url: String
)

data class PokemonSprites(
    val front_default: String
)