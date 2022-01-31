package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.checkbox.MaterialCheckBox
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterForWhoBinding
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterForWhoFragment : MvpAppCompatFragment(R.layout.fragment_filter_for_who), UserMvpView {
    private val binding: FragmentFilterForWhoBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRadioButtons()
        initToolBar()
        loadFilterForWho()
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

    private fun loadFilterForWho(){
        presenter.loadForWho()

    }

    override fun onStop() {
        super.onStop()
        presenter.saveForWho(listOf(
            binding.forWomenCheckBox.isChecked,
            binding.forMenCheckBox.isChecked,
            binding.forKidsCheckBox.isChecked
        ))
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        val forWho = result as List<Boolean>
        binding.forWomenCheckBox.isChecked = forWho[0]
        binding.forMenCheckBox.isChecked = forWho[1]
        binding.forKidsCheckBox.isChecked = forWho[2]


    }

    override fun successNewCode() {

    }
}