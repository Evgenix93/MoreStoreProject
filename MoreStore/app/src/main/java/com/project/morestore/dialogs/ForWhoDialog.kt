package com.project.morestore.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.card.MaterialCardView
import com.project.morestore.R

class ForWhoDialog(): DialogFragment() {
    val args: ForWhoDialogArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.window_for_who, FrameLayout(requireContext()), true)
        val forWhoTextView = view.findViewById<TextView>(R.id.forWhoTextView)
        if(args.isMale)
            forWhoTextView.text = "Для мужчин"
        else
            forWhoTextView.text = "Для женщин"

        return Dialog(requireContext()).apply { setContentView(view) }
    }
}