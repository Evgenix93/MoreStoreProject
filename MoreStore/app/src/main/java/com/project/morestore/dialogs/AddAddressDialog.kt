package com.project.morestore.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.morestore.R
import com.project.morestore.databinding.DialogAddressAddBinding

class AddAddressDialog(private val callback :(Type) -> Unit) : BottomSheetDialogFragment(){
    private lateinit var views : DialogAddressAddBinding
    override fun getTheme() = R.style.App_Dialog_Transparent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogAddressAddBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(views){
            close.setOnClickListener { dismiss() }
            courier.setOnClickListener {
                callback(Type.DELIVERY)
                dismiss()
            }
            delivery.setOnClickListener {
                callback(Type.PICKUP)
                dismiss()
            }
            cancel.setOnClickListener { dismiss() }
        }
    }

    enum class Type{ PICKUP, DELIVERY }

}