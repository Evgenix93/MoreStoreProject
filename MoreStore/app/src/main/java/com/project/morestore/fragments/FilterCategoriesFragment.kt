package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ProductCategoriesAdapter
import com.project.morestore.databinding.FragmentCategoriesBinding
import com.project.morestore.models.Filter

import com.project.morestore.models.ProductCategory
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterCategoriesFragment : MvpAppCompatFragment(R.layout.fragment_categories), UserMvpView {
    private val binding: FragmentCategoriesBinding by viewBinding()
    private lateinit var checkBoxes: List<CheckBox>
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
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
        Log.d("MyDebug", "saveCategories")
        if(categories != productCategoriesAdapter.getProductCategories().map{it.isChecked}) {
            presenter.clearSizes()
            Log.d("MyDebug", "clearSizes")
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

    override fun success(result: Any) {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
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

    override fun successNewCode() {

    }
}