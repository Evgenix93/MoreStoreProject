package com.project.morestore

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import com.project.morestore.mvpviews.AuthMvpView
import moxy.MvpAppCompatDialogFragment
import moxy.ktx.moxyPresenter

class ResumeRegisterDialog(): DialogFragment(){
    private val args: ResumeRegisterDialogArgs by navArgs()
    //private val presenter by moxyPresenter {  }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //val view = layoutInflater.inflate(R.layout.dialog_resume_registration, ConstraintLayout(requireContext()), true)
        //view.findViewById<TextView>(R.id.continueBtn).setOnClickListener {
           // Log.d("mylog", "onClick")
           // findNavController().navigate(ResumeRegisterDialogDirections.actionResumeRegisterDialogToRegistration1Fragment(false, true))
       // }
        //view.findViewById<MaterialButton>(R.id.enterBtn).setOnClickListener {
           // findNavController().navigate(ResumeRegisterDialogDirections.actionResumeRegisterDialogToFirstLaunchFragment())
        //}
        //return Dialog(requireContext()).apply {
         //   setContentView(view)
        //}




    return AlertDialog.Builder(requireContext())
            .setTitle("")
            .setMessage("Пользователь с такими данными уже существует")//if(!args.isEmail)"?" else "Эт)
            .setPositiveButton("Продолжить"){ _, _ ->
                findNavController().navigate(ResumeRegisterDialogDirections.actionResumeRegisterDialogToRegistration3Fragment(args.phoneOrEmail, args.code, args.userId))
            }
            .setNegativeButton("Вход"){ _, _ ->
                findNavController().navigate(ResumeRegisterDialogDirections.actionResumeRegisterDialogToMainFragment())
            }
            .create()

        //return Dialog(requireContext()).apply {
           // setContentView(dialog.)
        //}


    }

}