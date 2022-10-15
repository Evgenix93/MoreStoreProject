package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterBinding
import com.project.morestore.models.Filter
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.SortingType
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterFragment : MvpAppCompatFragment(R.layout.fragment_filter), UserMvpView {
    private val binding: FragmentFilterBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)
        setClickListeners()
        initToolBar()
        loadFilter()
    }


    override fun onResume() {
        super.onResume()
        initAutoCompleteTextView()
    }

    override fun onStop() {
        super.onStop()
        savePriceFilter()
    }

    private fun configureFilterScreen(filter: Filter) {
        if (filter.chosenForWho.isNotEmpty()) {
            binding.forWomenTextView.text =
                if (filter.chosenForWho[0]) "Женщинам"
                else if (filter.chosenForWho[1]) "Мужчинам"
                else
                    "Детям"
        }

        binding.showProductsGreenDotImageView.isVisible =
            if (filter.chosenProductStatus.isEmpty()) true
            else (filter.chosenProductStatus.all { it } || filter.chosenProductStatus.all { !it }).not()
        val strings =   filter.chosenProductStatus.mapIndexedNotNull { index, b ->
            if (b) {
                when (index) {
                    0 -> "в наличии"
                    1 -> "забронированные"
                    2 -> "проданные"
                    else -> null
                }
            } else
                null
        }
        if (filter.chosenProductStatus.all{it} || filter.chosenProductStatus.all{!it} )
            binding.inStockTextView.text = "В наличии, забронированные, проданные"
        else
            binding.inStockTextView.text = strings.joinToString(", ")

        binding.categoriesGreenDotImageView.isVisible = (filter.categories.all {
            it.isChecked ?: false
        } || filter.categories.all { !(it.isChecked ?: false) }).not()
        if (filter.categories.all{it.isChecked == true} || filter.categories.all{it.isChecked == false || it.isChecked == null})
            binding.allCategories.text = getString(R.string.all_categories)
        else
            binding.allCategories.text =
                filter.categories.filter { it.isChecked ?: false }
                    .joinToString(", ") { it.name }

        binding.brandsGreenDotImageView.isVisible =
            filter.brands.any { it.isChecked == true } && filter.brands.all { it.isChecked == true }
                .not()
        if (filter.brands.all{it.isChecked == true} || filter.brands.all{it.isChecked == false || it.isChecked == null})
            binding.allBrands.text = getString(R.string.all_brands)
        else
            binding.allBrands.text = filter.brands.filter { it.isChecked ?: false }
                .joinToString(", ") { it.name }
        binding.regionsGreenDotImageView.isVisible =
            if(filter.regions.isNotEmpty())
                filter.regions.any { it.isChecked == true } && filter.regions.all { it.isChecked == true }.not()
            else
                filter.currentLocation != null

            if(filter.currentLocation == null && filter.regions.isEmpty())
                   binding.allRegions.text = getString(R.string.all_regions)
           else
               if(filter.regions.isEmpty())
                   binding.allRegions.text = filter.currentLocation!!.name
              else
                if (filter.regions.all { it.isChecked == false || it.isChecked == null } || filter.regions.all { it.isChecked == true })
                    binding.allRegions.text = getString(R.string.all_regions)
             else
                 binding.allRegions.text =
                    filter.regions.filter { it.isChecked ?: false }
                        .joinToString(", ") { it.name }

        binding.stylesGreenDotImageView.isVisible =
            (filter.chosenStyles.all { it.isChecked == true } || filter.chosenStyles.all { it.isChecked == false }).not()

       binding.allStyles.text = if(filter.chosenStyles.all{it.isChecked == true} || filter.chosenStyles.all{it.isChecked == false})
             getString(R.string.all_styles)
        else
            filter.chosenStyles.mapIndexedNotNull{index, style ->
                if(style.isChecked == true)
               when(index){
               0 -> "вечерний"
               1 -> "деловой"
               2 -> "повседневный"
               3 -> "спортивный"
               else -> null
           }else
               null
        }.joinToString(", ")

        binding.conditionsGreenDotImageView.isVisible =
            (filter.chosenConditions.all { it } || filter.chosenConditions.all { !it }).not()
       binding.allConditions.text = if(filter.chosenConditions.all{it} || filter.chosenConditions.all{!it})
            getString(R.string.all_conditions)
        else
            filter.chosenConditions.mapIndexedNotNull{index, isChecked ->
                if (isChecked)
                when(index){
                    0 -> "новое с биркой"
                    1 -> "новое без бирок"
                    2 -> "отличное"
                    3 -> "хорошее"
                    else -> null
                }else
                    null
            }.joinToString(", ")

        binding.sizesGreenDotImageView.isVisible =
            (filter.chosenTopSizesWomen.all { it.isSelected } || filter.chosenTopSizesWomen.all { !it.isSelected }).not() ||
                    (filter.chosenBottomSizesWomen.all { it.isSelected } || filter.chosenBottomSizesWomen.all { !it.isSelected }).not() ||
                    (filter.chosenShoosSizesWomen.all { it.isSelected } || filter.chosenShoosSizesWomen.all { !it.isSelected }).not()


        val sizes = when(filter.chosenForWho.indexOf(true)) {
            0 -> filter.chosenTopSizesWomen + filter.chosenBottomSizesWomen + filter.chosenShoosSizesWomen
            1 -> filter.chosenTopSizesMen + filter.chosenBottomSizesMen + filter.chosenShoosSizesMen
            2 -> filter.chosenTopSizesKids + filter.chosenBottomSizesKids + filter.chosenShoosSizesKids
            else -> emptyList()
        }
        if(sizes.all{ it.isSelected } || sizes.all{!it.isSelected})
            binding.allSizes.text = getString(R.string.all_sizes)
        else
            binding.allSizes.text = sizes.filter{it.isSelected}.map { it.int }.toSet().joinToString(", ")

        binding.materialsGreenDotImageView.isVisible =
            (filter.chosenMaterials.all { it.isSelected } || filter.chosenMaterials.all { !it.isSelected }).not()
        if(filter.chosenMaterials.all { !it.isSelected } || filter.chosenMaterials.all { it.isSelected })
            binding.allMaterials.text = getString(R.string.all_materials)
        else
          binding.allMaterials.text = filter.chosenMaterials.filter{it.isSelected}.joinToString (", "){ it.name }
        binding.colorsGreenDotImageView.isVisible =
            (filter.colors.all { it.isChecked == true } || filter.colors.all { it.isChecked == false }).not()
        if(filter.colors.all { it.isChecked == false} || filter.colors.all { it.isChecked == true })
            binding.allColors.text = getString(R.string.all_colors)
        else
           binding.allColors.text = filter.colors.filter{it.isChecked == true}.joinToString(", "){it.name}

        binding.priceFromEditText.setText(filter.fromPrice?.toString())
        binding.priceUntilEditText.setText(filter.untilPrice?.toString())

        binding.typeAutoCompleteTextView.setText(when(filter.sortingType){
            SortingType.NEW.value -> "Новое"
            SortingType.CHEAP.value -> "Дешевле"
            SortingType.EXPENSIVE.value -> "Дороже"
            else -> "Популярные"
        }
        )
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
            //findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterKidsSizesFragment())
            val isShoos =
                FilterState.filter.categories.any { it.name == "Обувь" && it.isChecked == true } && !FilterState.filter.categories.any { it.isChecked == true && it.name != "Обувь" }
            val isCloth =
                !FilterState.filter.categories.any { it.name == "Обувь" && it.isChecked == true } && FilterState.filter.categories.any { it.isChecked == true }

            if (isCloth && FilterState.filter.chosenForWho[2].not()) {
                findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterSizesFragment())
            }
            if (isShoos && FilterState.filter.chosenForWho[2].not()) {
                findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterShoosSizesFragment())

            }
            if ((!isShoos && !isCloth) || FilterState.filter.chosenForWho[2] ) {
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
            binding.typeAutoCompleteTextView.showDropDown()
        }

        binding.showOffersBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }


    private fun initAutoCompleteTextView() {
        val types = resources.getStringArray(R.array.type_array)
        ArrayAdapter(requireContext(), R.layout.item_suggestion_textview, types).also { adapter ->
            binding.typeAutoCompleteTextView.setAdapter(adapter)

        }
        binding.typeAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            presenter.changeSortingType(when(position){
                0 -> SortingType.NEW.value
                1 -> SortingType.CHEAP.value
                2 -> SortingType.EXPENSIVE.value
                else -> SortingType.POPULAR.value
            })
        }
    }

    private fun initToolBar() {
        binding.toolbarFilter.arrowBackImageView.setOnClickListener { findNavController().popBackStack() }
        binding.toolbarFilter.actionTextView.setOnClickListener{
            presenter.clearFilter()
        }
    }

    private fun loadFilter(){
        presenter.getFilter()
    }

    private fun saveFilter() {
        presenter.saveFilter()
        presenter.saveFavoriteSearch()
    }

    private fun savePriceFilter(){
        presenter.savePrices(
            binding.priceFromEditText.text.toString().toIntOrNull(),
            binding.priceUntilEditText.text.toString().toIntOrNull()
        )
    }

    override fun success(result: Any) {
        loadFilter()
        val message = result as String
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        configureFilterScreen(result as Filter)
    }

    override fun successNewCode() {
        TODO("Not yet implemented")
    }
}