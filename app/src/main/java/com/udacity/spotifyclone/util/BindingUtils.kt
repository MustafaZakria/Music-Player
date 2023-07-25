package com.udacity.spotifyclone.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.udacity.spotifyclone.R
import com.udacity.spotifyclone.data.models.Song

object BindingUtils {
    @BindingAdapter("SongImage")
    @JvmStatic
    fun ImageView.setSongImage(url: String) {
            Glide.with(context).setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
            ).load(url).into(this)
    }

    @BindingAdapter("SwipeText")
    @JvmStatic
    fun TextView.setText(song: Song) {
        this.text = context.getString(R.string.swipe_text, song.title, song.subtitle)
    }
}
