package com.project.morestore.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.project.morestore.R

class SaveProductDialog(val positiveCallback: () -> Unit): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.window_exit_create_product, MaterialCardView(requireContext()), true)
        val yesButton = view.findViewById<Button>(R.id.yesButton)
        val noButton = view.findViewById<Button>(R.id.noButton)
        yesButton.setOnClickListener{
            positiveCallback()
            dismiss()
        }
        noButton.setOnClickListener{
            findNavController().navigate(R.id.mainFragment)
        }

        return Dialog(requireContext()).apply { setContentView(view) }
    }
}