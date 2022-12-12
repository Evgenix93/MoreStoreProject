package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.BrandsAdapter
import com.project.morestore.presentation.adapters.CategoryAdapter
import com.project.morestore.databinding.FragmentBrandsBinding
import com.project.morestore.data.models.Filter
import com.project.morestore.data.models.ProductBrand
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FilterBrandsFragment : MvpAppCompatFragment(R.layout.fragment_brands), UserMvpView {
    private val binding: FragmentBrandsBinding by viewBinding()
    private var segmentsAdapter: CategoryAdapter by autoCleared()
    private var brandsAdapter: BrandsAdapter by autoCleared()
    @Inject lateinit var userPresenter: UserPresenter
    private val presenter by moxyPresenter { userPresenter }
    private lateinit var searchFlow: Flow<String>
    private var brands = listOf<ProductBrand>()
    private val args: FilterBrandsFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSegmentsRecyclerView()
        initBrandsRecyclerView()
        getCategorySegments()
        initSearch()
        getBrands()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.crossIcon.setOnClickListener {
            binding.editText3.setText("")
        }
        binding.showOffersBtn.setOnClickListener {
            findNavController().navigate(FilterBrandsFragmentDirections.actionBrandsFragmentToCatalogFragment())
        }
    }

    override fun onStop() {
        super.onStop()
        saveFilter()
    }

    private fun initSegmentsRecyclerView() {
        segmentsAdapter = CategoryAdapter(false) { position, checked ->
            Log.d("segments", position.toString())
             val updatedBrands = brandsAdapter.getCurrentList()
                 updatedBrands.forEach  {
                     it.isChecked = false
                when(position){
                    1 -> if(it.idCategory == 1L) it.isChecked = checked
                    2 -> if(it.idCategory == 2L) it.isChecked = checked
                    3 -> if(it.idCategory == 3L) it.isChecked = checked
                    4 -> if(it.idCategory == 4L) it.isChecked = checked
                }
            }
            brandsAdapter.updateList(updatedBrands)
        }
        with(binding.segmentsRecyclerView) {
            adapter = segmentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun initBrandsRecyclerView() {
        brandsAdapter = BrandsAdapter({ brandId ->
            presenter.addBrandsToWishList(listOf(brandId))

        }, {segmentsAdapter.clearCheckboxes()})
        with(binding.brandsRecyclerView) {
            adapter = brandsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initSearch(){
        val searchEditText = binding.editText3
        searchFlow = kotlinx.coroutines.flow.callbackFlow<kotlin.String> {
            val searchListener =
                object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {

                    }

                    override fun onTextChanged(
                        newText: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {
                        trySendBlocking(newText.toString())

                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }

                }
            searchEditText.addTextChangedListener(searchListener)
            awaitClose { searchEditText.removeTextChangedListener(searchListener) }
        }

    }

    private fun saveFilter() {
        com.project.morestore.data.singletones.FilterState.filter.brands = brandsAdapter.getCurrentList()
        com.project.morestore.data.singletones.FilterState.filter.segments = segmentsAdapter.loadSegments2Checked()
    }

    private fun initToolbar() {
        binding.toolbarFilter.titleTextView.text = "Бренд или сегмент"
        binding.toolbarFilter.actionTextView.text = "Сбросить"
        binding.toolbarFilter.actionTextView.setOnClickListener {
            brandsAdapter.clearCheckboxes()
            segmentsAdapter.clearCheckboxes()
        }
        binding.toolbarFilter.arrowBackImageView.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getCategorySegments(){
        presenter.getAllCategorySegments()
    }

    private fun getBrands(){
        presenter.getAllBrands()
    }

    private fun getBrandWishList(){
        presenter.getBrandWishList()
    }

    private fun checkToken(){
        presenter.checkToken()
    }

    override fun success(result: Any) {
        binding.loader.isVisible = false
        brandsAdapter.updateWishedInfo(result as List<Long>, false)
        val brand = brandsAdapter.getCurrentList().find { it.id == result.first() }
        val message = if(brand?.isWished == true) getString(R.string.added_to_favorites) else getString(R.string.deleted_from_favorites)
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).apply {
            view = LayoutInflater.from(requireContext()).inflate(R.layout.toast, null)
            (view as TextView).text = message
        }
        toast.show()
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
            is Boolean -> {
                if (!result) {
                    getBrandWishList()
                }
            }
            is List<*> -> {
                if (result.isNotEmpty()) {
                    if (result[0] is ProductBrand) {
                        binding.loader.isVisible = false
                        if (brands.isEmpty()) {
                            brands = result as List<ProductBrand>
                            presenter.collectBrandsSearchFlow(searchFlow,  result as List<ProductBrand>)
                        }
                        brandsAdapter.updateList(result as List<ProductBrand>)
                        if(args.brands == null) {
                            checkToken()
                            presenter.getFilter()
                        }else
                          brandsAdapter.updateList2(args.brands!!.toList())
                    }

                    if (result[0] is Long) {
                        brandsAdapter.updateWishedInfo(result as List<Long>, true)
                    }
                }
            }
            is Filter -> {
                if (result.segments.isNotEmpty())
                    segmentsAdapter.updateSegmentsChecked(result.segments.toMutableList())
                if(result.brands.size == brandsAdapter.getCurrentList().size)
                    brandsAdapter.updateList(result.brands)
                else
                    brandsAdapter.updateList2(result.brands)
            }
        }
    }
}