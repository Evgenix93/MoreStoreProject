package com.project.morestore.widgets

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customviews.R

class EmptyListGallery(context :Context,
                       @DrawableRes img1 :Int,
                       @DrawableRes img2 :Int,
                       @DrawableRes img3 :Int
) :ConstraintLayout(context){

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.widget_emptylistgallery, this)
        findViewById<ImageView>(R.id.image1).setImageResource(img1)
        findViewById<ImageView>(R.id.image2).setImageResource(img2)
        findViewById<ImageView>(R.id.image3).setImageResource(img3)
    }
}