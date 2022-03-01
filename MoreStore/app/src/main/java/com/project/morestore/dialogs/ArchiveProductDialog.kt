package com.project.morestore.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.card.MaterialCardView
import com.project.morestore.R
import com.project.morestore.fragments.CreateProductStep6Fragment

class ArchiveProductDialog: DialogFragment(R.layout.window_delete_product) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.window_delete_product, MaterialCardView(requireContext()), true)
        val yesButton = view.findViewById<Button>(R.id.yesButton)
        view.findViewById<TextView>(R.id.questionTextView).text = "Уверены, что хотите архивировать объявление?"
        yesButton.setOnClickListener{
            val parent = parentFragment as CreateProductStep6Fragment
                parent.changeProductStatus(2)
            dismiss()
        }
        val noButton = view.findViewById<Button>(R.id.noButton)
        noButton.setOnClickListener { dismiss() }
        return Dialog(requireContext()).apply { setContentView(view) }
    }
}