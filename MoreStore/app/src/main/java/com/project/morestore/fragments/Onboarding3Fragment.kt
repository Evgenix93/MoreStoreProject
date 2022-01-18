package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.CategoryAdapter
import com.project.morestore.databinding.FragmentOnboarding3Binding
import com.project.morestore.models.Category
import com.project.morestore.mvpviews.OnBoardingMvpView

import com.project.morestore.presenters.ProductPresenter
import com.project.morestore.util.AutoClearedValue
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Onboarding3Fragment : MvpAppCompatFragment(R.layout.fragment_onboarding3), OnBoardingMvpView {
    private val binding: FragmentOnboarding3Binding by viewBinding()
    private val args: Onboarding3FragmentArgs by navArgs()
    private var categoryAdapter: CategoryAdapter by autoCleared()
    private val presenter by moxyPresenter { ProductPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setClickListeners()
        getCategories()

        /* binding.allCheckBox.setOnCheckedChangeListener { _, isChecked ->
             binding.luxuryCheckBox.isChecked = isChecked
             binding.middleCheckBox.isChecked = isChecked
             binding.massCheckBox.isChecked = isChecked
             binding.economyCheckBox.isChecked = isChecked
         } */
    }

    private fun initRecyclerView() {
        categoryAdapter = CategoryAdapter(true, requireContext()) { id, isChecked ->
            presenter.addRemoveCategoryId(id, isChecked)
        }
        with(binding.categoriesRecyclerView) {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setClickListeners() {
        binding.continueBtn.setOnClickListener {
            presenter.safeCategories()
        }
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun getCategories() {
        presenter.getCategories()
    }

    private fun showLoading(loading: Boolean){
        binding.continueBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }

    override fun success() {
        showLoading(false)
        findNavController().navigate(
            Onboarding3FragmentDirections.actionOnboarding3FragmentToOnboarding4Fragment(
                args.isMale
            )
        )

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
        showLoading(true)

    }

    override fun loaded(result: List<Any>) {
        showLoading(false)
        val categories = result as List<Category>
        categoryAdapter.updateList(categories)
    }
}