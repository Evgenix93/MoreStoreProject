package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterProductStatusBinding
import com.project.morestore.models.Filter
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter


class FilterProductStatusFragment: MvpAppCompatFragment(R.layout.fragment_filter_product_status), UserMvpView {
    private val binding: FragmentFilterProductStatusBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFilter()
        initToolBar()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }

    private fun bind(list: List<Boolean>){
        binding.newWithTagCheckBox.isChecked =  list[0]
        binding.newWithotuTagCheckBox.isChecked = list[1]
        binding.excellentCheckBox.isChecked = list[2]

    }
    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Показывать товары"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.arrowBackImageView.setOnClickListener { findNavController().popBackStack() }
    }

    private fun getFilter(){
        presenter.getFilter()
    }

    private fun saveStatuses(){
        presenter.saveStatuses(listOf(binding.newWithTagCheckBox.isChecked, binding.newWithotuTagCheckBox.isChecked,
            binding.excellentCheckBox.isChecked))
    }

    override fun onStop() {
        super.onStop()
        saveStatuses()
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        val filter = result as Filter
        bind(filter.chosenProductStatus)

    }

    override fun successNewCode() {

    }
}