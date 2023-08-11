package com.zeko.musicplayer.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.zeko.musicplayer.data.models.Song
import com.zeko.musicplayer.util.Constants.SONG_PATH
import kotlinx.coroutines.tasks.await

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