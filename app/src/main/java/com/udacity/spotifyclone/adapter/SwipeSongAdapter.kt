package com.udacity.spotifyclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.udacity.spotifyclone.databinding.SwipeItemBinding

class SwipeSongAdapter constructor(
    val listener: SongListener
) : BaseSongAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding =
            SwipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        val binding = holder.binding as SwipeItemBinding
        binding.song = song
        binding.listener = listener
        binding.executePendingBindings()
    }
}
