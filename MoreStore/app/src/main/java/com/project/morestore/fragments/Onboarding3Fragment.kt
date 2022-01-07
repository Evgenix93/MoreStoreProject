package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.CategoryAdapter
import com.project.morestore.databinding.FragmentOnboarding3Binding
import com.project.morestore.models.Category
import com.project.morestore.mvpviews.OnboardingMvpView
import com.project.morestore.presenters.ProductPresenter
import com.project.morestore.util.AutoClearedValue
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Onboarding3Fragment : MvpAppCompatFragment(R.layout.fragment_onboarding3), OnboardingMvpView {
    private val binding: FragmentOnboarding3Binding by viewBinding()
    private val args: Onboarding3FragmentArgs by navArgs()
    private var categoryAdapter: CategoryAdapter by AutoClearedValue()
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
        categoryAdapter = CategoryAdapter(requireContext()) { id, isChecked ->
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

    override fun success(result: Any) {
       /* findNavController().navigate(
            Onboarding3FragmentDirections.actionOnboarding3FragmentToOnboarding4Fragment(
                args.isMale
            )
        )*/
        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {

    }

    override fun loaded(list: List<Any>) {
        val categories = list as List<Category>
        categoryAdapter.updateList(categories)
    }
}