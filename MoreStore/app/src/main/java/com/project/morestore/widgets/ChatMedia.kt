package com.project.morestore.widgets

import android.content.Context
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.WidgetChatMediaBinding
import com.project.morestore.models.Media
import com.project.morestore.util.dp
import com.project.morestore.util.inflater

class ChatMedia(context :Context, media :Media) :FrameLayout(context) {
    private val views = WidgetChatMediaBinding.inflate(context.inflater, this)

    init {
        setPadding(1.dp, 1.dp, 1.dp, 1.dp)
        setBackgroundResource(R.drawable.bg_rectangle_border_round8)
        layoutParams = LayoutParams(58.dp, 58.dp)
        views.image.clipToOutline = true
        views.count
        when(media){
            is Media.Photo -> {
                Glide.with(views.image)
                    .load(media.photoId)
                    .centerCrop()
                    .into(views.image)
                if(media.count > 1) views.count.apply {
                    visibility = VISIBLE
                    text = context.getString(R.string.pattern_plus, media.count)
                }
            }
            is Media.Video -> {
                Glide.with(views.image)
                    .load(media.videoId)
                    .into(views.image)
                views.play.visibility = VISIBLE
            }
        }
    }
}