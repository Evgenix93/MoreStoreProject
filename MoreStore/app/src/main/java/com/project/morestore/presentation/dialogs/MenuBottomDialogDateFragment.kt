package com.project.morestore.presentation.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.morestore.databinding.BottomdialogDateBinding
import java.util.*

class MenuBottomDialogDateFragment(private val isTime: Boolean, val onDatePicked: (day: Int, month: Int, year: Int) -> Unit,
val onTimePicked: (hour: Int, minute: Int) -> Unit): BottomSheetDialogFragment() {
    private lateinit var binding: BottomdialogDateBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomdialogDateBinding.inflate(inflater).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.datePicker.isVisible = isTime.not()
        binding.timePicker.isVisible = isTime
        binding.timePicker.setIs24HourView(true)
        val calendar = Calendar.getInstance()
        binding.timePicker.currentHour = calendar.get(Calendar.HOUR_OF_DAY) + 1
        binding.okButton.setOnClickListener{
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDatePicked(binding.datePicker.dayOfMonth, binding.datePicker.month + 1, binding.datePicker.year)
        onTimePicked(binding.timePicker.currentHour, binding.timePicker.currentMinute )
    }
}