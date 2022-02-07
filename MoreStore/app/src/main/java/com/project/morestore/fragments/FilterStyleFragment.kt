package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterStyleBinding
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterStyleFragment : MvpAppCompatFragment(R.layout.fragment_filter_style), UserMvpView {
    private val binding: FragmentFilterStyleBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        loadFilterStyles()

    }

    private fun bind(list: List<Boolean>) {

           // val allNotSelected = FilterState.filter.chosenStyles.all { !it }
            binding.newWithTagCheckBox.isChecked = list[0]
            binding.newWithotuTagCheckBox.isChecked = list[1]
            binding.ExcellentCheckBox.isChecked = list[2]
            binding.goodCheckBox.isChecked = list[3]

    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Стиль"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    private fun loadFilterStyles(){
        presenter.loadStyles()
    }

    private fun saveFilterStyles(){
        presenter.saveStyles(listOf(
            binding.newWithTagCheckBox.isChecked,
            binding.newWithotuTagCheckBox.isChecked,
            binding.ExcellentCheckBox.isChecked,
            binding.goodCheckBox.isChecked
        ))
    }

    override fun onStop() {
        super.onStop()
        saveFilterStyles()
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        bind(result as List<Boolean>)

    }

    override fun successNewCode() {

    }
}