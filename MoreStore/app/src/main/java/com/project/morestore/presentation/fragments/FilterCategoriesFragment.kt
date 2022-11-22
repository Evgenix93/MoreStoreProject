package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.ProductCategoriesAdapter
import com.project.morestore.databinding.FragmentCategoriesBinding
import com.project.morestore.data.models.Filter

import com.project.morestore.data.models.ProductCategory
import com.project.morestore.domain.presenters.FilterPresenter
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.presentation.mvpviews.FilterView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FilterCategoriesFragment : MvpAppCompatFragment(R.layout.fragment_categories), FilterView {
    private val binding: FragmentCategoriesBinding by viewBinding()
    @Inject lateinit var filterPresenter: FilterPresenter
    private val presenter by moxyPresenter { filterPresenter }
    private var productCategoriesAdapter: ProductCategoriesAdapter by autoCleared()
    private var categories = listOf<Boolean>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initList()
        getProductCategories()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.showOffersBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }

    private fun initList(){
       productCategoriesAdapter = ProductCategoriesAdapter()
       binding.productCategoriesRecyclerView.adapter = productCategoriesAdapter
       binding.productCategoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onStop() {
        super.onStop()
        saveCategories()
    }

    private fun saveCategories() {
        if(categories != productCategoriesAdapter.getProductCategories().map{it.isChecked}) {
            presenter.clearSizes()
        }
        presenter.saveCategories(productCategoriesAdapter.getProductCategories())
    }

    private fun initToolbar(){
        binding.toolbarFilter.titleTextView.text = "Категории"
        binding.toolbarFilter.actionTextView.isVisible = false
        binding.toolbarFilter.arrowBackImageView.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun getProductCategories(){
        presenter.getProductCategories()
    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
       binding.loader.isVisible = true
    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        when(result){
            is List<*> -> {
               val productCategories = result as List<ProductCategory>
                productCategoriesAdapter.updateList(listOf(ProductCategory(0, "Любая категория", null)) + productCategories)
              presenter.getFilter()
            }
            is Filter -> {
                if(result.categories.size == productCategoriesAdapter.getProductCategories().size)
                    productCategoriesAdapter.updateList(result.categories)
                else
                    productCategoriesAdapter.updateList2(result.categories)

               categories = result.categories.map{it.isChecked!!}
            }
        }
    }
}