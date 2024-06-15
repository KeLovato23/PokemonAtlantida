package com.pruebatecnica.bancoAtlantida.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pruebatecnica.bancoAtlantida.R
import com.pruebatecnica.bancoAtlantida.data.model.Pokemon
import com.pruebatecnica.bancoAtlantida.ui.viewmodel.PokemonViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// PokemonAdapter.kt
class PokemonAdapter(private val pokemonViewModel: PokemonViewModel) :
    ListAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(PokemonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = getItem(position)
        holder.bind(pokemon)
    }

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val abilitiesTextView: TextView = itemView.findViewById(R.id.abilitiesTextView)

        fun bind(pokemon: Pokemon) {
            val capitalizedName = pokemon.name.replaceFirstChar { it.uppercase() }
            nameTextView.text = capitalizedName
            CoroutineScope(Dispatchers.Main).launch {
                val imageUrl = pokemonViewModel.getPokemonImageUrl(pokemon.url)
                Glide.with(itemView)
                    .load(imageUrl)
                    .placeholder(R.drawable.imgdefault)
                    .error(R.drawable.imgdefault)
                    .into(imageView)
            }

            // Mostrar las habilidades del Pokémon
            bindAbilities(pokemon.abilities)
        }

        fun bindAbilities(abilities: List<String>) {
            abilitiesTextView.text = abilities.joinToString(", ")
        }
    }

    class PokemonDiffCallback : DiffUtil.ItemCallback<Pokemon>() {
        override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
            return oldItem == newItem
        }
    }
}