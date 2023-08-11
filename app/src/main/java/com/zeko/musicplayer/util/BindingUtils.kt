package com.zeko.musicplayer.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.zeko.musicplayer.R
import com.zeko.musicplayer.data.models.Song
import java.text.SimpleDateFormat
import java.util.*

object BindingUtils {
    @BindingAdapter("SongImage")
    @JvmStatic
    fun ImageView.setSongImage(song: Song?) {
        song?.let {
            Glide.with(context).setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
            ).load(it.imageUrl).into(this)
        }
    }

    @BindingAdapter("SwipeText")
    @JvmStatic
    fun TextView.setText(song: Song?) {
        song?.let {
            this.text = context.getString(R.string.swipe_text, it.title, it.subtitle)
        }
    }

    @BindingAdapter("SongTime")
    @JvmStatic
    fun TextView.setSongTime(ms: LiveData<Long?>) {
        ms.value?.let {
            val dataFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            this.text = dataFormat.format(it)
        }
    }
}
