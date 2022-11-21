package com.project.morestore.presentation.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import com.project.morestore.databinding.DialogAddressDeleteBinding
import com.project.morestore.util.inflater

class AddressDeleteDialog(context :Context, val callback :()->Unit) : AlertDialog(context) {
    private val views = DialogAddressDeleteBinding.inflate(context.inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        with(views){
            close.setOnClickListener { dismiss() }
            confirm.setOnClickListener { callback(); dismiss() }
            cancel.setOnClickListener { dismiss() }
        }
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}