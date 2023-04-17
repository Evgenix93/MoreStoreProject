package com.project.morestore.presentation.dialogs

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.project.morestore.databinding.DialogSuccessSaveSearchBinding
import com.project.morestore.util.inflater

class SuccessSaveSearchDialog(context: Context): AlertDialog(context) {
    private val binding = DialogSuccessSaveSearchBinding.inflate(context.inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        setView(binding.root)
        binding.okBtn.setOnClickListener { dismiss() }
        binding.crossIcon.setOnClickListener { dismiss() }

        super.onCreate(savedInstanceState)

    }


}