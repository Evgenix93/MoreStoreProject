package com.project.morestore.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.card.MaterialCardView
import com.project.morestore.R

class ProductIsCreatedDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.window_create_product, MaterialCardView(requireContext()), false)
        view.findViewById<ImageView>(R.id.closeIcon).setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

    }


}