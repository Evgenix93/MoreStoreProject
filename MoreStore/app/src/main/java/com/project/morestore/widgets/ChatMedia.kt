package com.project.morestore.widgets

import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.WidgetChatMediaBinding
import com.project.morestore.data.models.Media
import com.project.morestore.util.dp
import com.project.morestore.util.inflater

class ChatMedia(context :Context, private val media :Media, private val size: Int) :FrameLayout(context) {
    private val views = WidgetChatMediaBinding.inflate(context.inflater, this)

    init {
        setPadding(1.dp, 1.dp, 1.dp, 1.dp)
        setBackgroundResource(R.drawable.bg_rectangle_border_round8)
        layoutParams = LayoutParams(58.dp, 58.dp)
        views.image.clipToOutline = true
        views.count
        when(media){
            is Media.Photo -> {
                Log.d("MyDebug", "media photo")
                Glide.with(views.image)
                    .load(media.photoUri)
                    .centerCrop()
                    .into(views.image)
                Log.d("MyDebug", "media count = ${media.count}")
                if(media.count == 4) views.count.apply {
                    if(size == 4)
                        return@apply
                    visibility = VISIBLE
                    text = context.getString(R.string.pattern_plus, size - 4)
                }
                views.play.visibility = GONE
            }
            is Media.Video -> {
                Log.d("MyDebug", "media video")
                Glide.with(views.image)
                    .load(media.videoUri)
                    .into(views.image)
             //   Log.d("MyDebug", "media size = ${media.size}")
                if(media.count == 4) views.count.apply {
                    if(size == 4)
                        return@apply
                    visibility = VISIBLE
                    text = context.getString(R.string.pattern_plus, size - 4)
                }
               // views.play.visibility = VISIBLE
            }
        }
    }
}