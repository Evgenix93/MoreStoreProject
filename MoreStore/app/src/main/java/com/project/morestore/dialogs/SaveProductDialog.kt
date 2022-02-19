package com.project.morestore.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.card.MaterialCardView
import com.project.morestore.R

class SaveProductDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.window_exit_create_product, MaterialCardView(requireContext()), true)

        return Dialog(requireContext()).apply { setContentView(view) }
    }
}