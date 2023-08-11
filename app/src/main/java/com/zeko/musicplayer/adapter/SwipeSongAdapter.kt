package com.zeko.musicplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zeko.musicplayer.databinding.SwipeItemBinding

class SwipeSongAdapter constructor(
    private val listener: SongListener
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
