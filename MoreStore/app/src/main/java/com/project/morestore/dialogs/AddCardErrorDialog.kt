package com.project.morestore.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.project.morestore.R
import com.project.morestore.databinding.WindowCardAddErrorBinding
import com.project.morestore.util.inflater

class AddCardErrorDialog(context: Context): DialogFragment() {
    private val binding = WindowCardAddErrorBinding.inflate(context.inflater)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

    }
}