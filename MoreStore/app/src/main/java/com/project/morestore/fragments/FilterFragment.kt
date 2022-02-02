package com.project.morestore.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterBinding
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.showDropdown
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterFragment : MvpAppCompatFragment(R.layout.fragment_filter), UserMvpView {
    private val binding: FragmentFilterBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)
        showGreenDots()
        setClickListeners()

        initToolBar()
    }

    override fun onStop() {
        super.onStop()
        //binding.typeAutoCompleteTextView.setAdapter(null)
    }

    override fun onResume() {
        super.onResume()
        initAutoCompleteTextView()
    }

    private fun showGreenDots() {
        if (FilterState.filter.chosenForWho.isNotEmpty()) {
            binding.forWomenTextView.text =
                if (FilterState.filter.chosenForWho[0]) "Женщинам"
                else if (FilterState.filter.chosenForWho[1]) "Мужчинам"
                else
                    "Детям"
        }

        binding.showProductsGreenDotImageView.isVisible =
            if (FilterState.filter.chosenProductStatus.isEmpty()) true
            else (FilterState.filter.chosenProductStatus.all { it } || FilterState.filter.chosenProductStatus.all { !it }).not()
        val strings =   FilterState.filter.chosenProductStatus.mapIndexedNotNull { index, b ->
            if (b) {
                when (index) {
                    0 -> "В наличии"
                    1 -> "забронированные"
                    2 -> "проданные"
                    else -> null
                }
            } else
                null
        }
        Log.d("Debug", strings.toString())
        Log.d("Debug", FilterState.filter.chosenProductStatus.toString())
        if (FilterState.filter.chosenProductStatus.all{it} || FilterState.filter.chosenProductStatus.all{!it} )
            binding.inStockTextView.text = "В наличии, забронированные, проданные"
        else
            binding.inStockTextView.text = strings.joinToString(", ")

        binding.categoriesGreenDotImageView.isVisible = (FilterState.filter.categories.all {
            it.isChecked ?: false
        } || FilterState.filter.categories.all { !(it.isChecked ?: false) }).not()
        if (FilterState.filter.categories.all{it.isChecked == true} || FilterState.filter.categories.all{it.isChecked == false || it.isChecked == null})
            binding.allCategories.text = getString(R.string.all_categories)
        else
            binding.allCategories.text =
                FilterState.filter.categories.filter { it.isChecked ?: false }
                    .joinToString(", ") { it.name }

        /* viewLifecycleOwner.lifecycleScope.launch{
             val sharedPrefs = requireContext().getSharedPreferences("categories_shared_prefs", Context.MODE_PRIVATE)
             val isAllCategories = sharedPrefs.getBoolean("all_categories", true)
             Log.d("Debug", "isAllCategories = $isAllCategories")
             binding.categoriesGreenDotImageView.isVisible = !isAllCategories
             binding.allCategories.isVisible = isAllCategories
         }*/
        Log.d("Debug", "isAllBrands = ${FilterState.filter.isAllBrands}")
        binding.brandsGreenDotImageView.isVisible =
            FilterState.filter.brands.any { it.isChecked == true } && FilterState.filter.brands.all { it.isChecked == true }
                .not()
        if (FilterState.filter.brands.all{it.isChecked == true} || FilterState.filter.brands.all{it.isChecked == false || it.isChecked == null})
            binding.allBrands.text = getString(R.string.all_brands)
        else
            binding.allBrands.text = FilterState.filter.brands.filter { it.isChecked ?: false }
                .joinToString(", ") { it.name }
        binding.regionsGreenDotImageView.isVisible =
            FilterState.filter.regions.any { it.isChecked == true } && FilterState.filter.regions.all { it.isChecked == true }
                .not()//(com.project.morestore.singletones.FilterState.filter.regions.all { it.isChecked == true } || FilterState.filter.regions.all { it.isChecked == false }).not()
            if ( FilterState.filter.regions.all { it.isChecked == false || it.isChecked == null } || FilterState.filter.regions.all { it.isChecked == true })
                binding.allRegions.text = getString(R.string.all_regions)
          else
              binding.allRegions.text =
                    FilterState.filter.regions.filter { it.isChecked ?: false }
                        .joinToString(", ") { it.name }
                //com.project.morestore.singletones.FilterState.filter.regions.all { it.isChecked == true } || FilterState.filter.regions.all { it.isChecked == false }
        //binding.showProductsGreenDotImageView.isVisible = com.project.morestore.singletones.FilterState.chosenProductStatus
        //val allSelected = com.project.morestore.singletones.FilterState.chosenStyles.all { it } || com.project.morestore.singletones.FilterState.chosenStyles.all { !it }
        binding.stylesGreenDotImageView.isVisible =
            (FilterState.filter.chosenStyles.all { it } || FilterState.filter.chosenStyles.all { !it }).not()
        binding.allStyles.isVisible = FilterState.filter.chosenStyles.all { it } || FilterState.filter.chosenStyles.all { !it }
        binding.conditionsGreenDotImageView.isVisible =
            (com.project.morestore.singletones.FilterState.filter.chosenConditions.all { it } || FilterState.filter.chosenConditions.all { !it }).not()
        binding.allConditions.isVisible =
            com.project.morestore.singletones.FilterState.filter.chosenConditions.all { it } || FilterState.filter.chosenConditions.all { !it }
        binding.sizesGreenDotImageView.isVisible =
            (com.project.morestore.singletones.FilterState.filter.chosenTopSizes.all { it.isSelected } || com.project.morestore.singletones.FilterState.filter.chosenTopSizes.all { !it.isSelected }).not() ||
                    (com.project.morestore.singletones.FilterState.filter.chosenBottomSizes.all { it.isSelected } || com.project.morestore.singletones.FilterState.filter.chosenBottomSizes.all { !it.isSelected }).not() ||
                    (com.project.morestore.singletones.FilterState.filter.chosenShoosSizes.all { it.isSelected } || com.project.morestore.singletones.FilterState.filter.chosenShoosSizes.all { !it.isSelected }).not()


        val sizes = FilterState.filter.chosenTopSizes + FilterState.filter.chosenBottomSizes + FilterState.filter.chosenShoosSizes
        if(sizes.all{ it.isSelected } || sizes.all{!it.isSelected})
            binding.allSizes.text = getString(R.string.all_sizes)
        else
            binding.allSizes.text = sizes.filter{it.isSelected}.joinToString(", "){it.int}

        //com.project.morestore.singletones.FilterState.filter.chosenTopSizes.all { it.isSelected } || com.project.morestore.singletones.FilterState.filter.chosenTopSizes.all { !it.isSelected }
        binding.materialsGreenDotImageView.isVisible =
            (com.project.morestore.singletones.FilterState.filter.chosenMaterials.all { it.isSelected } || com.project.morestore.singletones.FilterState.filter.chosenMaterials.all { !it.isSelected }).not()
        if(FilterState.filter.chosenMaterials.all { !it.isSelected } || FilterState.filter.chosenMaterials.all { it.isSelected })
            binding.allMaterials.text = getString(R.string.all_materials)
        else
          binding.allMaterials.text = FilterState.filter.chosenMaterials.filter{it.isSelected}.joinToString (", "){ it.name }
        binding.colorsGreenDotImageView.isVisible =
            (FilterState.filter.colors.all { it.isChecked } || FilterState.filter.colors.all { !it.isChecked }).not()
        if(FilterState.filter.colors.all { !it.isChecked } || FilterState.filter.colors.all { it.isChecked })
            binding.allColors.text = getString(R.string.all_colors)
        else
           binding.allColors.text = FilterState.filter.colors.filter{it.isChecked}.joinToString(", "){it.name}
    }


    private fun setClickListeners() {
        binding.categoryClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToCategoriesFragment())
        }
        binding.brandClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToBrandsFragment())
        }
        binding.regionClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToRegionsFragment())
        }

        binding.colorClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToColorsFragment())
        }

        binding.forWhoClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterForWhoFragment())
        }

        binding.showProductsClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterProductStatusFragment())
        }

        binding.stylesClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterStyleFragment())
        }

        binding.conditionClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterConditionFragment())
        }

        binding.sizeClickView.setOnClickListener {
            val isShoos =
                FilterState.filter.categories.any { it.name == "Обувь" && it.isChecked == true } && !FilterState.filter.categories.any { it.isChecked == true && it.name != "Обувь" }
            val isCloth =
                !FilterState.filter.categories.any { it.name == "Обувь" && it.isChecked == true } && FilterState.filter.categories.any { it.isChecked == true }

            if (isCloth) {
                findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterSizesFragment())
            }
            if (isShoos) {
                findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterShoosSizesFragment())

            }
            if (!isShoos && !isCloth) {
                findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterKidsSizesFragment())
            }

        }

        binding.materialsClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterMaterialsFragment())
        }

        binding.safeFilterBtn.setOnClickListener {
            saveFilter()
        }

        binding.sortingClickView.setOnClickListener {
            Log.d("Debug", "${binding.typeAutoCompleteTextView.isPopupShowing}")
            binding.typeAutoCompleteTextView.showDropDown()


        }
    }

    private fun initAutoCompleteTextView() {
        val types = resources.getStringArray(R.array.type_array)
        ArrayAdapter(requireContext(), R.layout.item_suggestion_textview, types).also { adapter ->
            binding.typeAutoCompleteTextView.setAdapter(adapter)

        }
    }

    private fun initToolBar() {
        binding.toolbarFilter.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    private fun saveFilter() {
        presenter.saveFilter()
    }

    override fun success(result: Any) {
        Toast.makeText(requireContext(), "Фильтр сохранен", Toast.LENGTH_LONG).show()
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun loading() {
        TODO("Not yet implemented")
    }

    override fun loaded(result: Any) {
        TODO("Not yet implemented")
    }

    override fun successNewCode() {
        TODO("Not yet implemented")
    }
}