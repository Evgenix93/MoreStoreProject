package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.checkbox.MaterialCheckBox
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterForWhoBinding
import com.project.morestore.models.Filter
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterForWhoFragment : MvpAppCompatFragment(R.layout.fragment_filter_for_who), UserMvpView {
    private val binding: FragmentFilterForWhoBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }

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
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
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
    }

    override fun onStop() {
        super.onStop()
        saveForWho()
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        val filter = result as Filter
        binding.forWomenCheckBox.isChecked = filter.chosenForWho[0]
        binding.forMenCheckBox.isChecked = filter.chosenForWho[1]
        binding.forKidsCheckBox.isChecked = filter.chosenForWho[2]


    }

    override fun successNewCode() {

    }
}