package com.project.morestore.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.morestore.adapters.DateAdapter
import com.project.morestore.adapters.SizeCardsAdapter
import com.project.morestore.databinding.BottomdialogDateBinding
import com.project.morestore.databinding.BottomdialogMenuBinding
import com.project.morestore.util.autoCleared
import com.project.morestore.util.inflater

class MenuBottomDialogDateFragment(context: Context, val isTime: Boolean, val onDatePicked: (day: Int, month: Int, year: Int) -> Unit,
val onTimePicked: (hour: Int, minute: Int) -> Unit): BottomSheetDialogFragment() {
    private lateinit var binding: BottomdialogDateBinding
    private var dateAdapter: DateAdapter by autoCleared()
    private var monthAdapter: DateAdapter by autoCleared()

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


       /* dateAdapter = DateAdapter(true)
        monthAdapter = DateAdapter(false)
        with(binding.dateList){
            adapter = monthAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)

        }
        with(binding.monthList){
            adapter = monthAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        dateAdapter.notifyDataSetChanged()
        monthAdapter.notifyDataSetChanged()*/

        /*binding.dateList.setOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    val linearManager = recyclerView.layoutManager as LinearLayoutManager
                    dateAdapter.updateSelectedDate(linearManager.findFirstCompletelyVisibleItemPosition())

                }
            }
        })*/

        /*binding.monthList.setOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    val linearManager = recyclerView.layoutManager as LinearLayoutManager
                    monthAdapter.updateSelectedDate(linearManager.findFirstCompletelyVisibleItemPosition())

                }
            }
        })*/

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDatePicked(binding.datePicker.dayOfMonth, binding.datePicker.month + 1, binding.datePicker.year)
        onTimePicked(binding.timePicker.currentHour, binding.timePicker.currentMinute )
    }


    /*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding.dateList.setOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

        return AlertDialog.Builder(requireContext()).setView(binding.root).create()





    }*/

}