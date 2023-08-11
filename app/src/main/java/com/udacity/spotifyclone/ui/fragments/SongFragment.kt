package com.udacity.spotifyclone.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.udacity.spotifyclone.R
import com.udacity.spotifyclone.data.models.Song
import com.udacity.spotifyclone.databinding.FragmentSongBinding
import com.udacity.spotifyclone.exoplayer.isPlaying
import com.udacity.spotifyclone.exoplayer.toSong
import com.udacity.spotifyclone.ui.viewmodels.MainViewmodel
import com.udacity.spotifyclone.ui.viewmodels.SongViewModel
import com.udacity.spotifyclone.util.BindingUtils.setSongTime
import com.udacity.spotifyclone.util.Status
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

        binding.viewmodel = songViewModel

        mainViewmodel = ViewModelProvider(requireActivity())[MainViewmodel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subsribeToObservers()

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

    private fun subsribeToObservers() {
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
                binding.seekBar.progress = it.toInt()
            }
        }
        songViewModel.curSongDuration.observe(viewLifecycleOwner) {
            binding.seekBar.max = it.toInt()
        }
    }
    private fun setCurPlayerTimeToTextView(ms: Long) {
        val dataFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        binding.tvCurTime.text = dataFormat.format(ms)
    }

}