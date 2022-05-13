package com.project.morestore.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.morestore.R
import com.project.morestore.databinding.BottomdialogYesNoBinding

class YesNoDialog(val title: String, val content: String?, val listener: onClickListener) : BottomSheetDialogFragment() {

    companion object {
        val TAG = "MORESHOP_ACCEPT_DIALOG"
    }

    interface onClickListener {
        fun onYesClick()
        fun onNoClick()
    }

    private lateinit var binding: BottomdialogYesNoBinding

    ///////////////////////////////////////////////////////////////////////////
    //                   BottomSheetDialogFragment
    ///////////////////////////////////////////////////////////////////////////

    override fun getTheme() = R.style.App_Dialog_Transparent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomdialogYesNoBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            ynDIalogNoBtn.setOnClickListener {
                listener.onNoClick()
                dismiss()
            }
            ynDIalogYesBtn.setOnClickListener{
                listener.onYesClick()
                dismiss()
            }

            ynDIalogTitle.text = title
            if (content != null) {
                ynDialogContent.text = content
                ynDialogContent.visibility = View.VISIBLE
            }
        }
    }
}