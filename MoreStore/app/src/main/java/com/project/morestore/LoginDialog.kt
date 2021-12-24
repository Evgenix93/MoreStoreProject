package com.project.morestore

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.databinding.WindowLoginBinding

class LoginDialog: DialogFragment() {
    private val binding: WindowLoginBinding by viewBinding()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.window_login, ConstraintLayout(requireContext()), true)
        view.findViewById<TextView>(R.id.registerTextView).setOnClickListener {
            Log.d("mylog", "onClick")
            findNavController().navigate(R.id.registration1Fragment)
        }
        return Dialog(requireContext()).apply {
            setContentView(view)
        }
    }




}