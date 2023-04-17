package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterProductStatusBinding
import com.project.morestore.data.models.Filter
import com.project.morestore.domain.presenters.FilterPresenter
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.presentation.mvpviews.FilterView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FilterProductStatusFragment: MvpAppCompatFragment(R.layout.fragment_filter_product_status), FilterView {
    private val binding: FragmentFilterProductStatusBinding by viewBinding()
    @Inject
    lateinit var filterPresenter: FilterPresenter
    private val presenter by moxyPresenter { filterPresenter }
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


    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
        binding.loader.isVisible = true
    }

    override fun loaded(result: Any) {
        val filter = result as Filter
        bind(filter.chosenProductStatus)
    }
}