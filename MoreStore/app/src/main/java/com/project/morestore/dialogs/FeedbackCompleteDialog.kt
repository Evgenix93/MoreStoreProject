package com.project.morestore.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.project.morestore.R
import com.project.morestore.databinding.DialogFeedbackCompleteBinding
import com.project.morestore.util.inflater

class FeedbackCompleteDialog(
    context :Context,
    private val callback :() -> Unit,
    val title :String? = null,
    val content :String? = null,
) :AlertDialog(context, R.style.App_Dialog_AlertTransparent) {
    private val views = DialogFeedbackCompleteBinding.inflate(context.inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        setView(views.root)
        views.complete.setOnClickListener {
            dismiss()
            callback()
        }
        views.close.setOnClickListener { dismiss() }

        if (title != null) {
            views.title.text = title
        }
        if (content != null){
            views.content.visibility = View.VISIBLE
            views.content.text = content;
        }
        super.onCreate(savedInstanceState)
    }
}