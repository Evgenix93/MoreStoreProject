package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.morestore.R
import com.project.morestore.databinding.FragmentEditFavoriteSearchBinding
import com.project.morestore.dialogs.DeleteDialog
import com.project.morestore.dialogs.SuccessSaveSearchDialog
import com.project.morestore.fragments.base.FullscreenMvpFragment
import com.project.morestore.models.FavoriteSearch
import com.project.morestore.models.Filter
import com.project.morestore.models.SizeLine
import com.project.morestore.mvpviews.FavoritesMvpView
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.FavoritesPresenter
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.NotificationType
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class EditFavoriteSearchFragment : FullscreenMvpFragment(), MainMvpView {
    private lateinit var binding: FragmentEditFavoriteSearchBinding
    @Inject lateinit var favoritesPresenter: FavoritesPresenter
    private val presenter by moxyPresenter { favoritesPresenter }
    private var filter = Filter()
    private val args: EditFavoriteSearchFragmentArgs by navArgs()
    private var firstTimeLoaded = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentEditFavoriteSearchBinding.inflate(inflater).also { binding = it }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)
        initToolBar()
        initViews()
        loadFilter()
        getFavoriteSearch()
        firstTimeLoaded = true


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.reserveFilter()



    }



    override fun onResume() {
        super.onResume()
        initAutoCompleteTextView()
    }



    override fun onDestroy() {
        presenter.restoreFilter()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FilterState.filter.name = binding.searchNameEditText.text.toString()
        if(binding.priceFromEditText.text.isNotEmpty())
            FilterState.filter.fromPrice = binding.priceFromEditText.text.toString().toInt()
        if(binding.priceUntilEditText.text.isNotEmpty())
            FilterState.filter.untilPrice = binding.priceUntilEditText.text.toString().toInt()
    }

    private fun initViews(){
        binding.deleteTextView.isVisible = args.favoriteSearchId != 0L
        binding.notificationTypeAutoComplete.setAdapter(ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line,
        arrayOf(NotificationType.DAILY.value, NotificationType.WEEKLY.value, NotificationType.DISABLED.value)))

    }



    private fun loadFilter(){
        if(firstTimeLoaded)
        presenter.getFilter()
    }

    private fun getFavoriteSearch() {
        if(firstTimeLoaded)
            return
        if (args.favoriteSearchId == 0L) {
            configureFilterScreen(filter)
            return
        }

        presenter.getFavoriteSearchById(args.favoriteSearchId)


    }

    private fun configureFilterScreen(filter: Filter) {
        setClickListeners()
        presenter.updateFilter(filter)
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
        val strings = filter.chosenProductStatus.mapIndexedNotNull { index, b ->
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
        if (filter.chosenProductStatus.all { it } || filter.chosenProductStatus.all { !it })
            binding.inStockTextView.text = "В наличии, забронированные, проданные"
        else
            binding.inStockTextView.text = strings.joinToString(", ")

        binding.categoriesGreenDotImageView.isVisible = (filter.categories.all {
            it.isChecked ?: false
        } || filter.categories.all { !(it.isChecked ?: false) }).not()
        if (filter.categories.all { it.isChecked == true } || filter.categories.all { it.isChecked == false || it.isChecked == null })
            binding.allCategories.text = getString(R.string.all_categories)
        else
            binding.allCategories.text =
                filter.categories.filter { it.isChecked ?: false }
                    .joinToString(", ") { it.name }

        binding.brandsGreenDotImageView.isVisible =
            filter.brands.any { it.isChecked == true } && filter.brands.all { it.isChecked == true }
                .not()
        if (filter.brands.all { it.isChecked == true } || filter.brands.all { it.isChecked == false || it.isChecked == null })
            binding.allBrands.text = getString(R.string.all_brands)
        else
            binding.allBrands.text = filter.brands.filter { it.isChecked ?: false }
                .joinToString(", ") { it.name }


        binding.stylesGreenDotImageView.isVisible =
            (filter.chosenStyles.all { it.isChecked == true } || filter.chosenStyles.all { it.isChecked == false }).not()

        binding.allStyles.text =
            if (filter.chosenStyles.all { it.isChecked == true } || filter.chosenStyles.all { it.isChecked == false })
                getString(R.string.all_styles)
            else
                filter.chosenStyles.mapIndexedNotNull { index, style ->
                    if (style.isChecked == true)
                        when (index) {
                            0 -> "вечерний"
                            1 -> "деловой"
                            2 -> "повседневный"
                            3 -> "спортивный"
                            else -> null
                        } else
                        null
                }.joinToString(", ")

        binding.conditionsGreenDotImageView.isVisible =
            (filter.chosenConditions.all { it } || filter.chosenConditions.all { !it }).not()
        binding.allConditions.text =
            if (filter.chosenConditions.all { it } || filter.chosenConditions.all { !it })
                getString(R.string.all_conditions)
            else
                filter.chosenConditions.mapIndexedNotNull { index, isChecked ->
                    if (isChecked)
                        when (index) {
                            0 -> "новое с биркой"
                            1 -> "новое без бирок"
                            2 -> "отличное"
                            3 -> "хорошее"
                            else -> null
                        } else
                        null
                }.joinToString(", ")

        binding.sizesGreenDotImageView.isVisible =
            (filter.chosenTopSizesWomen.all { it.isSelected } || filter.chosenTopSizesWomen.all { !it.isSelected }).not() ||
                    (filter.chosenBottomSizesWomen.all { it.isSelected } || filter.chosenBottomSizesWomen.all { !it.isSelected }).not() ||
                    (filter.chosenShoosSizesWomen.all { it.isSelected } || filter.chosenShoosSizesWomen.all { !it.isSelected }).not()


        val sizes: List<SizeLine> = when (filter.chosenForWho.indexOf(true)) {
            0 -> filter.chosenTopSizesWomen + filter.chosenBottomSizesWomen + filter.chosenShoosSizesWomen
            1 -> filter.chosenTopSizesMen + filter.chosenBottomSizesMen + filter.chosenShoosSizesMen
            2 -> filter.chosenTopSizesKids + filter.chosenBottomSizesKids + filter.chosenShoosSizesKids
            else -> emptyList<SizeLine>()
        }
        if (sizes.all { it.isSelected } || sizes.all { !it.isSelected })
            binding.allSizes.text = getString(R.string.all_sizes)
        else
            binding.allSizes.text =
                sizes.filter { it.isSelected }.map { it.int }.toSet().joinToString(", ")

        binding.materialsGreenDotImageView.isVisible =
            (filter.chosenMaterials.all { it.isSelected } || filter.chosenMaterials.all { !it.isSelected }).not()
        if (filter.chosenMaterials.all { !it.isSelected } || filter.chosenMaterials.all { it.isSelected })
            binding.allMaterials.text = getString(R.string.all_materials)
        else
            binding.allMaterials.text =
                filter.chosenMaterials.filter { it.isSelected }.joinToString(", ") { it.name }
        binding.colorsGreenDotImageView.isVisible =
            (filter.colors.all { it.isChecked == true } || filter.colors.all { it.isChecked == false }).not()
        if (filter.colors.all { it.isChecked == false } || filter.colors.all { it.isChecked == true })
            binding.allColors.text = getString(R.string.all_colors)
        else
            binding.allColors.text =
                filter.colors.filter { it.isChecked == true }.joinToString(", ") { it.name }

        binding.priceFromEditText.setText(filter.fromPrice?.toString())
        binding.priceUntilEditText.setText(filter.untilPrice?.toString())

        binding.typeAutoCompleteTextView.setText(filter.sortingType,false)
        binding.notificationTypeAutoComplete.setSelection(if(filter.notificationType == NotificationType.DAILY.value) 0
        else if(filter.notificationType == NotificationType.WEEKLY.value) 1 else 2)
        binding.searchNameEditText.setText(filter.name)

        binding.priceFromEditText.setText(if(filter.fromPrice == null) "" else filter.fromPrice.toString())
        binding.priceUntilEditText.setText(if(filter.untilPrice == null) "" else filter.untilPrice.toString())
    }

    private fun setClickListeners() {
        binding.categoryClickView.setOnClickListener {
            findNavController().navigate(R.id.categoriesFragment)
        }
        binding.brandClickView.setOnClickListener {
            findNavController().navigate(R.id.brandsFragment)
        }

        binding.colorClickView.setOnClickListener {
            findNavController().navigate(R.id.colorsFragment)
        }

        binding.forWhoClickView.setOnClickListener {
            findNavController().navigate(R.id.filterForWhoFragment)
        }

        binding.showProductsClickView.setOnClickListener {
            findNavController().navigate(R.id.filterProductStatusFragment)
        }

        binding.stylesClickView.setOnClickListener {
            findNavController().navigate(R.id.filterStyleFragment)
        }

        binding.conditionClickView.setOnClickListener {
            findNavController().navigate(R.id.filterConditionFragment)
        }

        binding.sizeClickView.setOnClickListener {
            val isShoos =
                filter.categories.any { it.name == "Обувь" && it.isChecked == true } && !filter.categories.any { it.isChecked == true && it.name != "Обувь" }
            val isCloth =
                !filter.categories.any { it.name == "Обувь" && it.isChecked == true } && filter.categories.any { it.isChecked == true }

            if (isCloth && filter.chosenForWho[2].not()) {
                findNavController().navigate(R.id.filterSizesFragment)
            }
            if (isShoos && filter.chosenForWho[2].not()) {
                findNavController().navigate(R.id.filterShoosSizesFragment)

            }
            if ((!isShoos && !isCloth) || filter.chosenForWho[2]) {
                findNavController().navigate(R.id.filterKidsSizesFragment)
            }

        }

        binding.materialsClickView.setOnClickListener {
            findNavController().navigate(R.id.filterMaterialsFragment)
        }

        binding.safeFilterBtn.setOnClickListener {

            saveFilter()
        }

        binding.sortingClickView.setOnClickListener {
            binding.typeAutoCompleteTextView.showDropDown()
        }

        binding.showOffersBtn.setOnClickListener {
            presenter.reserveFilter()
            findNavController().navigate(R.id.catalogFragment)
        }

        binding.deleteTextView.setOnClickListener {
            DeleteDialog(confirmCallback = {deleteFavoriteSearch()}, context = requireContext()).show()
        }
    }


    private fun initAutoCompleteTextView() {
        val types = resources.getStringArray(R.array.type_array)
        ArrayAdapter(requireContext(), R.layout.item_suggestion_textview, types).also { adapter ->
            binding.typeAutoCompleteTextView.setAdapter(adapter)

        }
    }

    private fun initToolBar() {
        binding.toolbarFilter.titleTextView.text = "Редактирование поиска"
        binding.toolbarFilter.backIcon.setOnClickListener { findNavController().popBackStack() }
        binding.toolbarFilter.actionIcon.isVisible = false
    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
        binding.showOffersBtn.isEnabled = !loading
        binding.safeFilterBtn.isEnabled = !loading
    }


    private fun saveFilter() {
        if(binding.searchNameEditText.text.isNullOrBlank()){
            Toast.makeText(requireContext(), "Укажите название", Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.priceFromEditText.text.isNotEmpty() && binding.priceUntilEditText.text.isNotEmpty()) {
            filter.fromPrice = binding.priceFromEditText.text.toString().toInt()
            filter.untilPrice = binding.priceUntilEditText.text.toString().toInt()
        }

        filter.notificationType = (binding.notificationTypeAutoComplete.selectedView as TextView).text.toString()
        filter.sortingType = binding.typeAutoCompleteTextView.text.toString()
        filter.name = binding.searchNameEditText.text.toString()
        /*filter.chosenTopSizes = filter.chosenTopSizes.filter { it.id != 0 }//filter.chosenTopSizes.toMutableList().apply { removeLast() }
        filter.chosenBottomSizes = filter.chosenBottomSizes.filter { it.id != 0 }//toMutableList().apply { removeLast() }
        filter.chosenShoosSizes = filter.chosenShoosSizes.filter { it.id != 0 }//toMutableList().apply { removeLast() }
        filter.chosenTopSizesMen = filter.chosenTopSizesMen.filter { it.id != 0 }//toMutableList().apply { removeLast() }
        filter.chosenBottomSizesMen = filter.chosenBottomSizesMen.filter { it.id != 0 }//toMutableList().apply { removeLast() }
        filter.chosenShoosSizesMen = filter.chosenShoosSizesMen.filter { it.id != 0 }//toMutableList().apply { removeLast() }
        filter.chosenTopSizesKids = filter.chosenTopSizesKids.filter { it.id != 0 }//toMutableList().apply { removeLast() }
        filter.chosenBottomSizesKids = filter.chosenBottomSizesKids.filter { it.id != 0 }//toMutableList().apply { removeLast() }
        filter.chosenShoosSizesKids = filter.chosenShoosSizesKids.filter { it.id != 0 }//toMutableList().apply { removeLast() }
*/
        if(args.favoriteSearchId == 0L)
        presenter.saveFavoriteSearch(filter)
        else
            presenter.editFavoriteSearch(args.favoriteSearchId, filter)

    }

    private fun deleteFavoriteSearch(){
        presenter.deleteFavoriteSearch(args.favoriteSearchId)
    }

    override fun loading() {
        showLoading(true)


    }

    override fun loaded(result: Any) {
        val list = result as List<*>
        showLoading(false)
        if(list.first() is FavoriteSearch) {
            val filter = (list.first() as FavoriteSearch).value
            this.filter = filter
            configureFilterScreen(this.filter)
        }
        if(list.first() is Filter){
            this.filter = list.first() as Filter
            configureFilterScreen(this.filter)
        }

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun success() {
        showLoading(false)
        SuccessSaveSearchDialog(requireContext()).show()
        findNavController().popBackStack()
    }

}






