package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.checkbox.MaterialCheckBox
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterForWhoBinding
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
class FilterForWhoFragment : MvpAppCompatFragment(R.layout.fragment_filter_for_who), FilterView {
    private val binding: FragmentFilterForWhoBinding by viewBinding()
    @Inject
    lateinit var filterPresenter: FilterPresenter
    private val presenter by moxyPresenter { filterPresenter }
    private var forWho = listOf<Boolean>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRadioButtons()
        initToolBar()
        getFilter()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }

    }

    private fun initRadioButtons() {
        binding.forWomenCheckBox.setOnClickListener {
            setupRadioButton(binding.forWomenCheckBox)
        }
        binding.forMenCheckBox.setOnClickListener {
            setupRadioButton(binding.forMenCheckBox)
        }
        binding.forKidsCheckBox.setOnClickListener {
            setupRadioButton(binding.forKidsCheckBox)
        }
    }

    private fun setupRadioButton(radioBtn: MaterialCheckBox) {
        if (!radioBtn.isChecked) {
            radioBtn.isChecked = true
            return
        }
        binding.forWomenCheckBox.isChecked = false
        binding.forMenCheckBox.isChecked = false
        binding.forKidsCheckBox.isChecked = false
        radioBtn.isChecked = true
    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Кому"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.arrowBackImageView.setOnClickListener { findNavController().popBackStack() }
    }

    private fun getFilter(){
        presenter.getFilter()
    }

    private fun saveForWho(){
        presenter.saveForWho(listOf(
            binding.forWomenCheckBox.isChecked,
            binding.forMenCheckBox.isChecked,
            binding.forKidsCheckBox.isChecked
        ))
        val newForWho = listOf(binding.forWomenCheckBox.isChecked, binding.forMenCheckBox.isChecked, binding.forKidsCheckBox.isChecked)
        if(forWho != newForWho){
           presenter.clearSizes()
        }
    }

    override fun onStop() {
        super.onStop()
        saveForWho()
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
      binding.loader.isVisible = true
    }

    override fun loaded(result: Any) {
        val filter = result as Filter
        binding.forWomenCheckBox.isChecked = filter.chosenForWho[0]
        binding.forMenCheckBox.isChecked = filter.chosenForWho[1]
        binding.forKidsCheckBox.isChecked = filter.chosenForWho[2]
        forWho = filter.chosenForWho
    }
}