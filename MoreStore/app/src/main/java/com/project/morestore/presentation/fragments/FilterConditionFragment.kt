package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterConditionBinding
import com.project.morestore.domain.presenters.FilterPresenter
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.presentation.mvpviews.FilterView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FilterConditionFragment: MvpAppCompatFragment(R.layout.fragment_filter_condition),
    FilterView {
    private val binding: FragmentFilterConditionBinding by viewBinding()
    @Inject
    lateinit var filterPresenter: FilterPresenter
    private val presenter by moxyPresenter { filterPresenter }

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
        binding.excellentCheckBox.isChecked, binding.goodCheckBox.isChecked))
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
      binding.loader.isVisible = true
    }

    override fun loaded(result: Any) {
        val conditions = result as List<Boolean>
        binding.newWithTagCheckBox.isChecked = conditions[0]
        binding.newWithotuTagCheckBox.isChecked =  conditions[1]
        binding.excellentCheckBox.isChecked =  conditions[2]
        binding.goodCheckBox.isChecked =  conditions[3]

    }
}