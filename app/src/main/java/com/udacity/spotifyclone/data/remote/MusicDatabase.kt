package com.udacity.spotifyclone.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.udacity.spotifyclone.data.models.Song
import com.udacity.spotifyclone.util.Constants.SONG_PATH
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class MusicDatabase {
    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_PATH)

    suspend fun getAllSongs(): List<Song> {

        return try {
            songCollection.get().await().toObjects(Song::class.java)

        } catch (e: Exception) {
            emptyList()
        }
    }
}