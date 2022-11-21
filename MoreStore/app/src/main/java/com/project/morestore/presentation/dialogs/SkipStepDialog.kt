package com.project.morestore.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.card.MaterialCardView
import com.project.morestore.R

class SkipStepDialog: DialogFragment() {
    private val args: SkipStepDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.window_skip_step, MaterialCardView(requireContext()), true)
        val yesButton = view.findViewById<Button>(R.id.yesButton)
        val noButton = view.findViewById<Button>(R.id.noButton)
        yesButton.setOnClickListener{
            if(args.isBrand)
            findNavController().navigate(SkipStepDialogDirections.actionSkipStepDialogToCreateProductNameFragment(category = args.category, forWho = args.forWho))
            else
                findNavController().navigate(SkipStepDialogDirections.actionSkipStepDialogToCreateProductStep5Fragment(category = args.category, forWho = args.forWho))
        }
        noButton.setOnClickListener{
            dismiss()
        }
        return Dialog(requireContext()).apply { setContentView(view) }
    }


}