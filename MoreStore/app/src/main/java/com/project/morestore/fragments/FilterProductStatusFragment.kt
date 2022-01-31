package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterProductStatusBinding
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterProductStatusFragment: MvpAppCompatFragment(R.layout.fragment_filter_product_status), UserMvpView {
    private val binding: FragmentFilterProductStatusBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFilterStatuses()
        initToolBar()
    }

    private fun bind(list: List<Boolean>){
        binding.newWithTagCheckBox.isChecked =  list[0]
        binding.newWithotuTagCheckBox.isChecked = list[1]
        binding.ExcellentCheckBox.isChecked = list[2]

    }
    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Показывать товары"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    private fun loadFilterStatuses(){
        presenter.loadProductStatuses()

    }

    override fun onStop() {
        super.onStop()
        presenter.saveProductStatuses(listOf(binding.newWithTagCheckBox.isChecked, binding.newWithotuTagCheckBox.isChecked,
        binding.ExcellentCheckBox.isChecked))
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