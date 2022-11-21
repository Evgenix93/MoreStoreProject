package com.project.morestore.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.project.morestore.databinding.WindowCardAddErrorBinding
import com.project.morestore.util.inflater

class AddCardErrorDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = WindowCardAddErrorBinding.inflate(requireContext().inflater)
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

    }
}