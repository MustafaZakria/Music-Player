package com.udacity.spotifyclone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.udacity.spotifyclone.R
import com.udacity.spotifyclone.adapter.BaseSongAdapter
import com.udacity.spotifyclone.adapter.SwipeSongAdapter
import com.udacity.spotifyclone.data.models.Song
import com.udacity.spotifyclone.databinding.ActivityMainBinding
import com.udacity.spotifyclone.exoplayer.toSong
import com.udacity.spotifyclone.ui.viewmodels.MainViewmodel
import com.udacity.spotifyclone.util.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    private val mainViewModel: MainViewmodel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.vpSong.adapter = swipeSongAdapter

        subscribeToObservers()

    }

    private fun switchViewPagerToCurrentSong(song:Song) {
        val itemIndex = swipeSongAdapter.currentList.indexOf(song)
        if(itemIndex != -1) {
            binding.vpSong.currentItem = itemIndex
            curPlayingSong = song
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.submitList(songs)
                            if(songs.isNotEmpty()) {
                                glide.load((curPlayingSong ?: songs[0]).imageUrl).into(binding.ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    Status.LOADING, Status.ERROR -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(this) {
            if(it == null) return@observe

            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUrl).into(binding.ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        }
    }
}