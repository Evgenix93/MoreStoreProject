package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterConditionBinding
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FilterConditionFragment: MvpAppCompatFragment(R.layout.fragment_filter_condition), UserMvpView {
    private val binding: FragmentFilterConditionBinding by viewBinding()
    @Inject
    lateinit var userPresenter: UserPresenter
    private val presenter by moxyPresenter { userPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        loadFilterConditions()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Состояние"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.arrowBackImageView.setOnClickListener { findNavController().popBackStack() }
    }

    private fun loadFilterConditions(){
        presenter.loadConditions()

    }

    override fun onStop() {
        super.onStop()
        presenter.saveConditions(listOf(binding.newWithTagCheckBox.isChecked, binding.newWithotuTagCheckBox.isChecked,
        binding.ExcellentCheckBox.isChecked, binding.goodCheckBox.isChecked))
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        val conditions = result as List<Boolean>
        binding.newWithTagCheckBox.isChecked = conditions[0]
        binding.newWithotuTagCheckBox.isChecked =  conditions[1]
        binding.ExcellentCheckBox.isChecked =  conditions[2]
        binding.goodCheckBox.isChecked =  conditions[3]

    }
}