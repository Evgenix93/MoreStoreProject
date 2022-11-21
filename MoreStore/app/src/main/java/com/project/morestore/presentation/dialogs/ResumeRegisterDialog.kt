package com.project.morestore.presentation.dialogs

import android.app.Dialog
import android.os.Bundle

import androidx.appcompat.app.AlertDialog

import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.project.morestore.R


class ResumeRegisterDialog: DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext())
            .setTitle("")
            .setMessage(getString(R.string.user_already_exist))
            .setPositiveButton(getString(R.string.enter)){ _, _ ->
                findNavController().navigate(ResumeRegisterDialogDirections.actionResumeRegisterDialogToMainFragment())
            }
            .create()




    }

}