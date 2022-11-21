package com.project.morestore.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import com.project.morestore.R


class RatingBar :LinearLayout {
    //todo scaleType
    private val ratePoints by lazy {
        Array(childCount) { getChildAt(it) as ImageView }
    }
    var callback :((Int) -> Unit)? = null
    var rate :Int = 0
        set(value){
            field = value
            for(filledIndex in 0 until value){
                ratePoints[filledIndex].setImageResource(R.drawable.ic_star_filled)
            }
            for(emptyIndex in value until childCount){
                ratePoints[emptyIndex].setImageResource(R.drawable.ic_star_empty)
            }
            callback?.invoke(value)
        }
    constructor(context :Context) :super(context)
    constructor(context :Context, attributes :AttributeSet) :super(context, attributes){
        val attrs = context.obtainStyledAttributes(attributes, R.styleable.RatingBar)
        val total = attrs.getInt(R.styleable.RatingBar_totalPoints, 5)
        val isIndicator = attrs.getBoolean(R.styleable.RatingBar_android_isIndicator, false)
        attrs.recycle()
        for(point in 1..total){
            addView(ImageView(context).apply {
                layoutParams = LayoutParams(0, MATCH_PARENT).apply { weight = 1f }
                gravity = CENTER_HORIZONTAL
                setImageResource(R.drawable.ic_star_empty)
                if(!isIndicator) setOnClickListener { rate = point }
            })
        }
    }

    init {
        orientation = HORIZONTAL
    }
}