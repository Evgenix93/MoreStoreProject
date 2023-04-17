package com.project.morestore.presentation.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.project.morestore.R
import com.project.morestore.databinding.DeleteDialogBinding
import com.project.morestore.util.inflater

class DeleteDialog(
    context :Context,
    private val title :String = context.getString(R.string.deleteDialog_defaultTitle),
    private val message :String? = context.getString(R.string.deleteDialog_defaultMessage),
    private val confirmText :String = context.getString(R.string.deleteDialog_confirm),
    private val confirmCallback :(()->Unit)? = null,
    private val cancelText :String = context.getString(R.string.deleteDialog_cancel),
    private val cancelCallback :(()->Unit)? = null
) :AlertDialog(context, R.style.App_Dialog_AlertTransparent) {
    private val views = DeleteDialogBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        setView(views.root)

        views.title.text = title
        if (message == null) {
            views.text.visibility = View.GONE
        }else {
            views.text.text = message
        }
        views.confirm.text = confirmText
        views.close.setOnClickListener { dismiss() }
        views.confirm.setOnClickListener {
            confirmCallback?.invoke()
            dismiss()
        }
        views.cancel.text = cancelText
        views.cancel.setOnClickListener {
            cancelCallback?.invoke()
            dismiss()
        }

        super.onCreate(savedInstanceState)
    }

}