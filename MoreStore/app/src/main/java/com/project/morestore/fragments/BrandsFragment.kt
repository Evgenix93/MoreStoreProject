package com.project.morestore.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.FilterState
import com.project.morestore.R
import com.project.morestore.adapters.BrandsAdapter
import com.project.morestore.adapters.CategoryAdapter
import com.project.morestore.databinding.FragmentBrandsBinding
import com.project.morestore.models.Category
import com.project.morestore.models.ProductBrand
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class BrandsFragment : MvpAppCompatFragment(R.layout.fragment_brands), UserMvpView {
    private val binding: FragmentBrandsBinding by viewBinding()
    private var segmentsAdapter: CategoryAdapter by autoCleared()
    private var brandsAdapter: BrandsAdapter by autoCleared()
    //private var brandsAAdapter: BrandsAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private lateinit var searchFlow: Flow<String>
    private var brands = listOf<ProductBrand>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSegmentsRecyclerView()
        initBrandsRecyclerView()
        //initABrandsRecyclerView()
        loadFilter()
        getCategorySegments()
        initSearch()
        getBrands()
    }

    override fun onStop() {
        super.onStop()
        safeFilter()
    }

    private fun initSegmentsRecyclerView() {
        segmentsAdapter = CategoryAdapter(false, requireContext()) { _, _ -> }
        with(binding.segmentsRecyclerView) {
            adapter = segmentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun initBrandsRecyclerView() {
        brandsAdapter = BrandsAdapter(true)
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

   // private fun initABrandsRecyclerView() {
      //  brandsAAdapter = BrandsAdapter(false)
       // with(binding.brandsARecyclerView) {
        //    adapter = brandsAAdapter
         //   layoutManager = LinearLayoutManager(requireContext())
       // }
    //}

    private fun safeFilter() {
        FilterState.segments = segmentsAdapter.loadSegmentsChecked()
       // FilterState.brands9 = brands9Adapter.loadBrands9Checked()
       // FilterState.brandsA = brandsAAdapter.loadBrandsAChecked()
        //FilterState.isAllBrands = (segmentsAdapter.loadSegmentsChecked()
           // .all { !it } && brands9Adapter.loadBrands9Checked().all { !it } && brandsAAdapter.loadBrandsAChecked().all { !it }) || (segmentsAdapter.loadSegmentsChecked()
           // .all { it } && brands9Adapter.loadBrands9Checked().all { it } && brandsAAdapter.loadBrandsAChecked().all { it })
    }

    private fun loadFilter() {

        if (FilterState.segments.isNotEmpty()) {
            Log.d("Debug", "loadFilter")
            segmentsAdapter.updateSegmentsChecked(FilterState.segments.toMutableList())
        }

        //if (FilterState.brands9.isNotEmpty()) {
           // Log.d("Debug", "loadFilter brands9")
          //  brands9Adapter.updateBrands9Checked(FilterState.brands9.toMutableList())
       // }
        //if (FilterState.brandsA.isNotEmpty()) {
          //  brandsAAdapter.updateBrandsAChecked(FilterState.brandsA.toMutableList())
        //}
    }

    private fun initToolbar() {
        binding.toolbarFilter.titleTextView.text = "Бренд или сегмент"
        binding.toolbarFilter.actionTextView.text = "Сбросить"
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

    override fun success(result: Any) {


    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        val list = result as List<*>
        if(list.isNotEmpty()){
            if(list[0] is Category){
                segmentsAdapter.updateList(list as List<Category>)
            }else{
                if(brands.isEmpty()){
                    brands = list as List<ProductBrand>
                    presenter.collectBrandsSearchFlow(searchFlow, brands)
                }
                brandsAdapter.updateList(list as List<ProductBrand>)
            }
        }

    }

    override fun successNewCode() {

    }
}