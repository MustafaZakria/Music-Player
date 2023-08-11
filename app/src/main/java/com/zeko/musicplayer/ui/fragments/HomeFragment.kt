package com.zeko.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeko.musicplayer.R
import com.zeko.musicplayer.adapter.SongAdapter
import com.zeko.musicplayer.adapter.SongListener
import com.zeko.musicplayer.databinding.FragmentHomeBinding
import com.zeko.musicplayer.ui.viewmodels.MainViewmodel
import com.zeko.musicplayer.util.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    lateinit var mainViewmodel: MainViewmodel

    lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        mainViewmodel = ViewModelProvider(requireActivity())[MainViewmodel::class.java]

        songAdapter = SongAdapter(SongListener { song ->
            mainViewmodel.playOrToggleSong(song)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        subscribeToObservers()
    }

    private fun setUpRecyclerView() = binding.rvAllSongs.apply {
        adapter = songAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        mainViewmodel.mediaItems.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    binding.allSongsProgressBar.isVisible = false
                    result.data?.let { songs ->
                        songAdapter.submitList(songs)
                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> {
                    binding.allSongsProgressBar.isVisible = true
                }
            }
        }
    }


}