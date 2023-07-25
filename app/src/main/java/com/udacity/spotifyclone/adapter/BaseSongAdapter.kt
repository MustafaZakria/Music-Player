package com.udacity.spotifyclone.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.spotifyclone.data.models.Song

abstract class BaseSongAdapter :
    ListAdapter<Song, BaseSongAdapter.SongViewHolder>(SongDiffCallback()) {

    class SongViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
}

class SongListener(val listener: (Song) -> Unit) {
    fun onClick(song: Song) = listener(song)
}