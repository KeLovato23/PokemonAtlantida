package com.pruebatecnica.bancoAtlantida.data

import com.pruebatecnica.bancoAtlantida.data.model.Ability
import com.pruebatecnica.bancoAtlantida.data.model.AbilityInfo
import com.pruebatecnica.bancoAtlantida.data.model.PokemonDetailsResponse
import com.pruebatecnica.bancoAtlantida.data.model.PokemonSprites
import com.pruebatecnica.bancoAtlantida.data.remote.PokemonDetailsApi


class FakePokemonDetailsApi : PokemonDetailsApi {
    override suspend fun getPokemonDetails(id: Int): PokemonDetailsResponse {
        // Retorna una respuesta de ejemplo
        return PokemonDetailsResponse(
            id = id,
            name = "Bulbasaur",
            abilities = listOf(
                Ability(
                    ability = AbilityInfo(name = "Overgrow", url = "https://example.com/ability/overgrow"),
                    is_hidden = false,
                    slot = 1
                ),
                Ability(
                    ability = AbilityInfo(name = "Chlorophyll", url = "https://example.com/ability/chlorophyll"),
                    is_hidden = true,
                    slot = 2
                )
            ),
            sprites = PokemonSprites(front_default = "https://example.com/bulbasaur.png")
        )
    }
}