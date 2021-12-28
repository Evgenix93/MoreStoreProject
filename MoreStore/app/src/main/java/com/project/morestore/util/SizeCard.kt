package com.project.morestore.util

import android.app.Application
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.android.material.card.MaterialCardView
import com.project.morestore.R

class SizeCard(app: Context, atr: AttributeSet): MaterialCardView(app, atr) {
    private var chosen = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        rootView.setOnClickListener {
            if(!chosen) {
                chosen = true
                it.setBackgroundColor(resources.getColor(R.color.gray3))
                strokeColor = resources.getColor(R.color.green)
            }else{
                chosen = false
                it.setBackgroundColor(resources.getColor(R.color.white))
                strokeColor = resources.getColor(R.color.gray1)
            }


        }
    }
}