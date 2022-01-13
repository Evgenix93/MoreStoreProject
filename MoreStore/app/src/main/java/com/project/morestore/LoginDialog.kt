package com.project.morestore

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.button.MaterialButton
import com.project.morestore.databinding.WindowLoginBinding

class LoginDialog: DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.window_login, ConstraintLayout(requireContext()), true)
        view.findViewById<TextView>(R.id.registerTextView).setOnClickListener {
            Log.d("mylog", "onClick")
            findNavController().navigate(LoginDialogDirections.actionLoginDialogToRegistration1Fragment(false, false))
        }
        view.findViewById<MaterialButton>(R.id.loginBtn).setOnClickListener {
            findNavController().navigate(LoginDialogDirections.actionLoginDialogToRegistration1Fragment(true, false))
        }
        view.findViewById<ImageView>(R.id.vkImageView).setOnClickListener {
            findNavController().navigate(LoginDialogDirections.actionLoginDialogToLoginSocialFragment("vk"))
        }
        view.findViewById<ImageView>(R.id.okImageView).setOnClickListener {
            findNavController().navigate(LoginDialogDirections.actionLoginDialogToLoginSocialFragment("ok"))
        }
        view.findViewById<ImageView>(R.id.googleImageView).setOnClickListener {
            findNavController().navigate(LoginDialogDirections.actionLoginDialogToLoginSocialFragment("gl"))
        }

        return Dialog(requireContext()).apply {
            setContentView(view)
        }
    }



}