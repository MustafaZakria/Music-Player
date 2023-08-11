package com.zeko.musicplayer.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.zeko.musicplayer.R
import com.zeko.musicplayer.data.models.Song
import com.zeko.musicplayer.databinding.FragmentSongBinding
import com.zeko.musicplayer.exoplayer.isPlaying
import com.zeko.musicplayer.exoplayer.toSong
import com.zeko.musicplayer.ui.viewmodels.MainViewmodel
import com.zeko.musicplayer.ui.viewmodels.SongViewModel
import com.zeko.musicplayer.util.Status
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SongFragment : Fragment() {

    private lateinit var binding: FragmentSongBinding

    lateinit var mainViewmodel: MainViewmodel

    private val songViewModel : SongViewModel by viewModels()

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_song, container, false)

        binding.lifecycleOwner = this

        binding.viewmodel = songViewModel

        mainViewmodel = ViewModelProvider(requireActivity())[MainViewmodel::class.java]

        (activity as AppCompatActivity).supportActionBar?.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        binding.ivPlayPauseDetail.setOnClickListener {
            curPlayingSong?.let {
                mainViewmodel.playOrToggleSong(it, true)
            }
        }

        binding.ivSkip.setOnClickListener {
            mainViewmodel.skipToNextSong()
        }
        binding.ivSkipPrevious.setOnClickListener {
            mainViewmodel.skipToPreviousSong()
        }

        binding.ivMinimize.setOnClickListener {
            val navHostFragment = requireParentFragment() as NavHostFragment
            val navController = navHostFragment.findNavController()
            navController.navigateUp()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    setCurPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewmodel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar= true
                }
            }
        })
    }

    private fun subscribeToObservers() {
        mainViewmodel.mediaItems.observe(viewLifecycleOwner) {
            it?.let { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            if(curPlayingSong == null && songs.isNotEmpty()) {
                                curPlayingSong = songs[0]
                                binding.song = curPlayingSong
                            }
                        }
                    }
                    else -> Unit
                }

            }
        }

        mainViewmodel.curPlayingSong.observe(viewLifecycleOwner) {
            if(it == null) return@observe

            curPlayingSong = it.toSong()
            binding.song = curPlayingSong
        }
        mainViewmodel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            binding.ivPlayPauseDetail.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            binding.seekBar.progress = it?.position?.toInt() ?: 0
        }

        songViewModel.curPlayerPosition.observe(viewLifecycleOwner) {
            if(shouldUpdateSeekbar) {
                it?.let {
                    binding.seekBar.progress = it.toInt()
                }
            }
        }
        songViewModel.curSongDuration.observe(viewLifecycleOwner) {
            it?.let {
                binding.seekBar.max = it.toInt()
            }
        }
    }
    private fun setCurPlayerTimeToTextView(ms: Long) {
        val dataFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        binding.tvCurTime.text = dataFormat.format(ms)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}