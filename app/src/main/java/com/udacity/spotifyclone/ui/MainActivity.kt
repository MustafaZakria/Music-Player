package com.udacity.spotifyclone.ui

import android.media.session.PlaybackState
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.udacity.spotifyclone.R
import com.udacity.spotifyclone.adapter.BaseSongAdapter
import com.udacity.spotifyclone.adapter.SongListener
import com.udacity.spotifyclone.adapter.SwipeSongAdapter
import com.udacity.spotifyclone.data.models.Song
import com.udacity.spotifyclone.databinding.ActivityMainBinding
import com.udacity.spotifyclone.exoplayer.isPlaying
import com.udacity.spotifyclone.exoplayer.toSong
import com.udacity.spotifyclone.ui.viewmodels.MainViewmodel
import com.udacity.spotifyclone.util.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    private val mainViewModel: MainViewmodel by viewModels()

    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        swipeSongAdapter = SwipeSongAdapter(SongListener { song ->
            navController.navigate(
                R.id.globalActionToSongFragment
            )
        })

        binding.vpSong.adapter = swipeSongAdapter

        subscribeToObservers()

        binding.vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.currentList[position])
                } else {
                    curPlayingSong = swipeSongAdapter.currentList[position]
                }
            }
        })

        binding.ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomBar()
                R.id.songFragment -> hideBottomBar()
                else -> showBottomBar()
            }
        }
    }

    private fun hideBottomBar() {
        binding.ivPlayPause.isVisible = false
        binding.ivCurSongImage.isVisible = false
        binding.vpSong.isVisible = false
    }

    private fun showBottomBar() {
        binding.ivPlayPause.isVisible = true
        binding.ivCurSongImage.isVisible = true
        binding.vpSong.isVisible = true
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

        mainViewModel.playbackState.observe(this) {
            playbackState = it
            binding.ivPlayPause.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        mainViewModel.isConnected.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                        binding.root,
                        result.message ?: "An unknown error occurred",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }

        mainViewModel.networkError.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                        binding.root,
                        result.message ?: "An unknown error occurred",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
    }
}