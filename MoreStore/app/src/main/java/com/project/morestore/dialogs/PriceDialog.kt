package com.project.morestore.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.morestore.R
import com.project.morestore.databinding.BottomdialogSetdiscountBinding

class PriceDialog() :BottomSheetDialogFragment() {
    constructor(price :Int, type :Type) :this(){
        arguments = bundleOf(
            PRICE to price,
            TYPE to type.ordinal
        )
    }
    companion object{
        const val PRICE = "price"
        const val TYPE = "type"
    }
    private lateinit var views :BottomdialogSetdiscountBinding

    override fun getTheme() = R.style.App_Dialog_Transparent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomdialogSetdiscountBinding.inflate(inflater).also{ views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val price = requireArguments().getInt(PRICE, 0)
        with(views){
            subtitle.text = getString(R.string.setDiscount_subtitle, price)
            editText.hint = String.format("%,d", price)
            editText.addTextChangedListener {
                it?.let { apply.isEnabled = it.length > 0 }
            }
            close.setOnClickListener { dismiss() }
            if(Type.values()[requireArguments().getInt(TYPE, 0)] == Type.DISCOUNT){
                title.setText(R.string.setDiscount_title)
                apply.setText(R.string.setDiscount_apply)

            } else {
                title.setText(R.string.setPrice_title)
                apply.setText(R.string.setPrice_apply)
                apply.setOnClickListener {
                    (parentFragment as Callback).applyNewPrice(editText.text.toString())
                    dismiss()
                }
            }
        }
    }

    enum class Type { PRICE, DISCOUNT }
    interface Callback {
        fun applyNewPrice(newPrice :String)
    }
}