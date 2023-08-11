package com.udacity.spotifyclone.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.udacity.spotifyclone.R
import com.udacity.spotifyclone.data.models.Song
import java.text.SimpleDateFormat
import java.util.*

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

    @BindingAdapter("SongTime")
    @JvmStatic
    fun TextView.setSongTime(ms: LiveData<Long>) {
        if(ms.value != null) {
            val dataFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            this.text = dataFormat.format(ms)
        } else {
            this.text = "00:00"
        }

    }
}
