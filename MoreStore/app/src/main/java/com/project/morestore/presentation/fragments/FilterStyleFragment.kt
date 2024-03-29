package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterStyleBinding
import com.project.morestore.data.models.Property
import com.project.morestore.domain.presenters.FilterPresenter
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.presentation.mvpviews.FilterView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FilterStyleFragment : MvpAppCompatFragment(R.layout.fragment_filter_style), FilterView {
    private val binding: FragmentFilterStyleBinding by viewBinding()
    @Inject
    lateinit var filterPresenter: FilterPresenter
    private val presenter by moxyPresenter { filterPresenter }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        loadFilterStyles()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }

    private fun bind(list: List<Property>) {

            binding.eveningCheckBox.isChecked = list[0].isChecked == true
            binding.busynessCheckBox.isChecked = list[1].isChecked == true
            binding.usualCheckBox.isChecked = list[2].isChecked == true
            binding.sportCheckBox.isChecked = list[3].isChecked == true

    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Стиль"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.arrowBackImageView.setOnClickListener { findNavController().popBackStack() }
    }

    private fun loadFilterStyles(){
        presenter.loadStyles()
    }

    private fun saveFilterStyles(){
        presenter.saveStyles(listOf(
            binding.eveningCheckBox.isChecked,
            binding.busynessCheckBox.isChecked,
            binding.usualCheckBox.isChecked,
            binding.sportCheckBox.isChecked
        ))
    }

    override fun onStop() {
        super.onStop()
        saveFilterStyles()
    }


    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        bind(result as List<Property>)
    }
}