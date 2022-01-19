package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.FilterState
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.*

class CategoriesFragment : Fragment(R.layout.fragment_categories) {
    private val binding: FragmentCategoriesBinding by viewBinding()
    private lateinit var checkBoxes: List<CheckBox>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initCheckBoxes()
        loadFilter()
    }

    private fun loadFilter() {
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
        binding.allCategoriesCheckBox.isChecked = FilterState.isAllCategories
        if(FilterState.categories.isNotEmpty())
        checkBoxes.forEachIndexed{index, checkbox->
            checkbox.isChecked = FilterState.categories[index]
        }
    }

    private fun initCheckBoxes() {
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
    }

    override fun onStop() {
        super.onStop()
        safeFilter()
    }
    override fun onDestroyView() {
        super.onDestroyView()
       /* GlobalScope.launch {
            Log.d("Debug", "coroutineLaunch")
            safeFilter(checkBoxes, binding.allCategoriesCheckBox.isChecked)
        }
        Thread.sleep(500)*/

    }

    private fun safeFilter() {
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
        if(checkBoxes.all { !it.isChecked })
            binding.allCategoriesCheckBox.isChecked = true
        FilterState.isAllCategories = binding.allCategoriesCheckBox.isChecked
        FilterState.categories = checkBoxes.map{it.isChecked}
        Log.d("Debug", "safeFilter")
    }

    private fun initToolbar(){
        binding.toolbarFilter.titleTextView.text = "Категории"
        binding.toolbarFilter.actionTextView.isVisible = false
        binding.toolbarFilter.imageView2.setOnClickListener{
            findNavController().popBackStack()
        }
    }
}