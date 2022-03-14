package com.project.morestore.models

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.util.dp
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import java.util.*

@JsonClass(generateAdapter = true)
class Review(
    val id: Long,
    val user :User,
    val photo :Array<ReviewPhoto>?,
    val text :String,
    val rate :Byte,
    val date :Calendar
)

 abstract class ReviewListItem{
    object Create
}

class ReviewItem(val review :Review): ReviewListItem()

class PreviewPhoto(context :Context, url :String) :FrameLayout(context){

    init {
        setPadding(1.dp, 1.dp, 1.dp, 1.dp)
        setBackgroundResource(R.drawable.bg_rectangle_border_round8)
        layoutParams = LayoutParams(58.dp, 58.dp)
        val photo = ImageView(context)
            .apply {
                layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
                setBackgroundResource(R.drawable.bg_rectangle_border_round8)
                clipToOutline = true
            }
            .also { addView(it) }
        Glide.with(context)
            .load(url)
            .centerCrop()
            .into(photo)
    }
}

object CalendarAdapter{
    @FromJson
    fun deserialize(sec :Long): Calendar = Calendar.getInstance().apply{ timeInMillis = sec * 1000 }

    @ToJson
    fun serialize(calendar :Calendar) :Long = calendar.timeInMillis
}