package com.udacity.spotifyclone.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.udacity.spotifyclone.data.models.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            it.iconUri.toString(),
            it.mediaId ?: "",
            it.mediaUri.toString(),
            it.subtitle.toString(),
            it.title.toString()
        )
    }
}