package com.project.morestore.presentation.widgets.loading

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity.CENTER
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.project.morestore.R

class LoadingDialog(context :Context) :AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setContentView(FrameLayout(context).apply {
            val params = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply { gravity = CENTER }
            ProgressBar(context)
                .apply {
                    layoutParams = params
                    val tintColor = ContextCompat.getColor(context, R.color.green)
                    DrawableCompat.setTint(indeterminateDrawable, tintColor)
                }
                .also { addView(it) }
        })
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}