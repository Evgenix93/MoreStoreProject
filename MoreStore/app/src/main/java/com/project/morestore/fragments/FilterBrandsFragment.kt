package com.project.morestore.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.BrandsAdapter
import com.project.morestore.adapters.CategoryAdapter
import com.project.morestore.databinding.FragmentBrandsBinding
import com.project.morestore.models.Category
import com.project.morestore.models.Filter
import com.project.morestore.models.ProductBrand
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterBrandsFragment : MvpAppCompatFragment(R.layout.fragment_brands), UserMvpView {
    private val binding: FragmentBrandsBinding by viewBinding()
    private var segmentsAdapter: CategoryAdapter by autoCleared()
    private var brandsAdapter: BrandsAdapter by autoCleared()
    //private var brandsAAdapter: BrandsAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
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
        segmentsAdapter = CategoryAdapter(false, requireContext()) { _, _ -> }
        with(binding.segmentsRecyclerView) {
            adapter = segmentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun initBrandsRecyclerView() {
        brandsAdapter = BrandsAdapter(){ brandId ->
            presenter.addBrandsToWishList(listOf(brandId))

        }
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
                        sendBlocking(newText.toString())

                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }

                }
            searchEditText.addTextChangedListener(searchListener)
            awaitClose { searchEditText.removeTextChangedListener(searchListener) }
        }

    }

    private fun saveFilter() {
        com.project.morestore.singletones.FilterState.filter.brands = brandsAdapter.getCurrentList()
        com.project.morestore.singletones.FilterState.filter.segments = segmentsAdapter.loadSegments2Checked()
        //FilterState.segments = segmentsAdapter.loadSegmentsChecked()
       // FilterState.brands9 = brands9Adapter.loadBrands9Checked()
       // FilterState.brandsA = brandsAAdapter.loadBrandsAChecked()
        //FilterState.isAllBrands = (segmentsAdapter.loadSegmentsChecked()
           // .all { !it } && brands9Adapter.loadBrands9Checked().all { !it } && brandsAAdapter.loadBrandsAChecked().all { !it }) || (segmentsAdapter.loadSegmentsChecked()
           // .all { it } && brands9Adapter.loadBrands9Checked().all { it } && brandsAAdapter.loadBrandsAChecked().all { it })
    }

    private fun initToolbar() {
        binding.toolbarFilter.titleTextView.text = "Бренд или сегмент"
        binding.toolbarFilter.actionTextView.text = "Сбросить"
        binding.toolbarFilter.actionTextView.setOnClickListener {
            brandsAdapter.clearCheckboxes()
            segmentsAdapter.clearCheckboxes()
        }
        binding.toolbarFilter.imageView2.setOnClickListener {
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
        brandsAdapter.updateWishedInfo(result as List<Long>, false)
        val brand = brandsAdapter.getCurrentList().find { it.id == result.first() }
        val message = if(brand?.isWished == true) "Добавлено в избранное" else "Удалено из избранного"
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).apply {
            view = LayoutInflater.from(requireContext()).inflate(R.layout.toast, null)
            (view as TextView).text = message
        }
        toast.show()
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        when(result){
            is Boolean -> {
                if (!result) {
                    getBrandWishList()
                }
            }
            is List<*> -> {
                if (result.isNotEmpty()) {
                    if (result[0] is ProductBrand) {
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
                        //getBrandWishList()
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

    override fun successNewCode() {

    }
}