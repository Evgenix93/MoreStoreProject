package com.project.morestore.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import com.project.morestore.R
import com.project.morestore.databinding.DialogFeedbackCompleteBinding
import com.project.morestore.util.inflater

class FeedbackCompleteDialog(
    context :Context,
    val review :Boolean,
    private val callback :() -> Unit
) :AlertDialog(context, R.style.App_Dialog_AlertTransparent) {
    private val views = DialogFeedbackCompleteBinding.inflate(context.inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        setView(views.root)
        views.complete.setOnClickListener {
            dismiss()
            callback()
        }
        views.close.setOnClickListener { dismiss() }
        super.onCreate(savedInstanceState)
    }
}