package com.project.morestore.fragments

import android.os.Bundle
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
import com.project.morestore.models.ProductCategory
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CategoriesFragment : MvpAppCompatFragment(R.layout.fragment_categories), UserMvpView {
    private val binding: FragmentCategoriesBinding by viewBinding()
    private lateinit var checkBoxes: List<CheckBox>
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private var productCategoriesAdapter: ProductCategoriesAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initList()
        getProductCategories()

    }

    private fun initList(){
       productCategoriesAdapter = ProductCategoriesAdapter()
       binding.productCategoriesRecyclerView.adapter = productCategoriesAdapter
       binding.productCategoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadFilter() {
        if(FilterState.filter.categories.size == productCategoriesAdapter.getProductCategories().size)
            productCategoriesAdapter.updateList(FilterState.filter.categories)
     /*   viewLifecycleOwner.lifecycleScope.launch {
            val categoriesStringSet = withContext(Dispatchers.IO) {
                val sharedPrefs = requireContext().getSharedPreferences(
                    "categories_shared_prefs",
                    Context.MODE_PRIVATE
                )
                sharedPrefs.getStringSet("categories", null)
            }
            if (categoriesStringSet.isNullOrEmpty()){
                Log.d("Debug", "categoriesStringSet = null")
                binding.allCategoriesCheckBox.isChecked = true
                return@launch
            }
             Log.d("Debug", "categoriesStringSet = $categoriesStringSet")
            checkBoxes.forEach {
                it.isChecked = categoriesStringSet.contains(it.id.toString())
            }
        }*/
       // binding.allCategoriesCheckBox.isChecked = FilterState.filter.isAllCategories
       // if(FilterState.filter.categories.isNotEmpty())
       // checkBoxes.forEachIndexed{index, checkbox->
         //   checkbox.isChecked = FilterState.filter.categories[index]
       // }
    }

   /* private fun initCheckBoxes() {
        with(binding) {
            checkBoxes = listOf(
                underwearCheckBox,
                blousesShirtsCheckBox,
                trousersCheckBox,
                outerwearCheckBox,
                sweatersCheckBox,
                jeansCheckBox,
                homeWearCheckBox,
                dressesCheckBox,
                shoesCheckBox,
                accessoriesCheckBox,
                sportTourismCheckBox,
                otherCheckBox
            )
            allCategoriesCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    checkBoxes.forEach { it.isChecked = true }
                else if (checkBoxes.all { it.isChecked }) {
                    checkBoxes.forEach { checkBox -> checkBox.isChecked = false }
                }
            }
            checkBoxes.forEach {
                it.setOnCheckedChangeListener { _, _ ->
                    allCategoriesCheckBox.isChecked =
                        checkBoxes.all { checkBox -> checkBox.isChecked }
                }
            }
        }
    }*/



    override fun onStop() {
        super.onStop()
        safeFilter()
    }

    private fun safeFilter() {
        FilterState.filter.categories =  productCategoriesAdapter.getProductCategories()
           /* val categoriesStringSet = checkBoxes.mapNotNull {
                if(it.isChecked)
                    it.id.toString()
                else
                    null
            }.toSet()
        withContext(Dispatchers.IO) {
            Log.d("Debug", "safeFilterStart")

            val sharedPrefs = requireContext().getSharedPreferences(
                "categories_shared_prefs",
                Context.MODE_PRIVATE
            )
            Log.d("Debug", "isAllChecked = $isAllChecked")
            sharedPrefs.edit()
                .putBoolean("all_categories", isAllChecked)
                .putStringSet("categories", categoriesStringSet)
                .commit()

            Log.d("Debug", "safeFilterEnd")
        }*/
      //  FilterState.filter.isAllCategories = binding.allCategoriesCheckBox.isChecked || !binding.allCategoriesCheckBox.isChecked
       // FilterState.filter.categories = checkBoxes.map{it.isChecked}
       // Log.d("Debug", "safeFilter")
    }

    private fun initToolbar(){
        binding.toolbarFilter.titleTextView.text = "Категории"
        binding.toolbarFilter.actionTextView.isVisible = false
        binding.toolbarFilter.imageView2.setOnClickListener{
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
        val productCategories = result as List<ProductCategory>
        if(FilterState.filter.categories.size - 1 == productCategories.size)
            productCategoriesAdapter.updateList(FilterState.filter.categories)
        else
           productCategoriesAdapter.updateList(listOf(ProductCategory(0, "Все категории", null)) + productCategories)
    }

    override fun successNewCode() {

    }
}