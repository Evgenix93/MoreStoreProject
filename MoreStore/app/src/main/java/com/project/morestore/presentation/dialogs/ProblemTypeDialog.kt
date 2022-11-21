package com.project.morestore.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.morestore.R
import com.project.morestore.databinding.BottomdialogProblemTypeBinding

class ProblemTypeDialog(val listener: onClickListener) : BottomSheetDialogFragment() {

    companion object {
        val TAG = "MORESHOP_PROBLEM_TYPE_DIALOG"
    }

    interface onClickListener {
        fun onClick(type: String)
    }

    private lateinit var binding: BottomdialogProblemTypeBinding

    ///////////////////////////////////////////////////////////////////////////
    //                   BottomSheetDialogFragment
    ///////////////////////////////////////////////////////////////////////////

    override fun getTheme() = R.style.App_Dialog_Transparent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomdialogProblemTypeBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            problemTypeWrongSize.setOnClickListener {
                listener.onClick(binding.problemTypeWrongSize.text.toString())
                dismiss()
            }
            problemTypeWrongDescription.setOnClickListener {
                listener.onClick(binding.problemTypeWrongDescription.text.toString())
                dismiss()
            }
            problemTypeWrongGood.setOnClickListener {
                listener.onClick(binding.problemTypeWrongGood.text.toString())
                dismiss()
            }
            problemTypeOther.setOnClickListener {
                listener.onClick(binding.problemTypeOther.text.toString())
                dismiss()
            }
        }
    }
}