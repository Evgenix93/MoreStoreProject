package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.presentation.adapters.ProductAdapter
import com.project.morestore.presentation.adapters.SuggestionArrayAdapter
import com.project.morestore.databinding.FragmentCatalogBinding
import com.project.morestore.data.models.Address
import com.project.morestore.data.models.Filter
import com.project.morestore.data.models.Product
import com.project.morestore.data.models.SuggestionModels
import com.project.morestore.domain.presenters.CatalogPresenter
import com.project.morestore.presentation.mvpviews.CatalogMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CatalogFragment : MvpAppCompatFragment(R.layout.fragment_catalog), CatalogMvpView {
    private val binding: FragmentCatalogBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()
    @Inject lateinit var catalogPresenter: CatalogPresenter
    private val presenter by moxyPresenter { catalogPresenter }
    private val args: CatalogFragmentArgs? by navArgs()
    private var currentSuggestionModels: SuggestionModels? = null
    private var queryStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queryStr = arguments?.getString("query")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFilter()
        initList()
        initToolbar()
        setClickListeners()
        showBottomNav()
        loadProducts( queryStr)
        loadForWho()
        getCurrentUserAddress()
    }

    private fun getCurrentUserAddress(){
        presenter.getCurrentUserAddress()
    }

    private fun loadForWho(){

        args?.forWho?.let {
        var list = listOf(false, false, false)
        if(it == "women")
            list = listOf(true, false, false)

            if(it == "men")
                list = listOf(false, true, false)

            if(it == "kids")
                list = listOf(false, false, true)
            presenter.getProducts(isFiltered = true, forWho = list) }
    }

    private fun setClickListeners() {
        binding.changeRegionTextView.setOnClickListener {
            findNavController().navigate(
                CatalogFragmentDirections.actionCatalogFragmentToChangeRegionFragment()
            )
        }


        binding.AllRegionsTextView.setOnClickListener {
            presenter.changeUserCity(null)

        }

        binding.searchBtn.setOnClickListener {
            val queryStr  = binding.toolbarMain.searchEditText.text.toString()
            Log.d("mylog", "query: $queryStr")
            if(currentSuggestionModels != null)
                presenter.getSuggestionProducts(currentSuggestionModels!!)
            else
                presenter.getProducts(queryStr = queryStr, isFiltered = true)
            (requireActivity() as MainActivity).hideKeyboard()
            binding.searchBtn.isVisible = false
        }
    }


    private fun initList() {
        productAdapter = ProductAdapter(null) {
            findNavController().navigate(
                CatalogFragmentDirections.actionCatalogFragmentToProductDetailsFragment(it, null, false)
            )
        }
        with(binding.productList) {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)

        }
    }

    private fun initToolbar() {
        val toolbar = binding.toolbarMain.materialToolbar
        val searchItem = toolbar.menu.findItem(R.id.search)
        searchItem.setOnMenuItemClickListener {
            binding.toolbarMain.searchFrameLayout.isVisible = true
            it.isVisible = false
            toolbar.menu.findItem(R.id.favorite).isVisible = false
            toolbar.menu.findItem(R.id.cart).isVisible = false
            binding.toolbarMain.backImageView.isVisible = true
            binding.toolbarMain.filterBtn.isVisible = false
            true
        }
        binding.toolbarMain.clearImageView.setOnClickListener {
            binding.toolbarMain.searchEditText.text.clear()
        }

        binding.toolbarMain.backImageView.setOnClickListener {
            binding.toolbarMain.backImageView.isVisible = false
            binding.toolbarMain.searchFrameLayout.isVisible = false
            searchItem.isVisible = true
            toolbar.menu.findItem(R.id.favorite).isVisible = true
            toolbar.menu.findItem(R.id.cart).isVisible = true
            binding.toolbarMain.filterBtn.isVisible = true
        }

        binding.toolbarMain.searchEditText.setAdapter(
            SuggestionArrayAdapter(
                requireContext(),
                R.layout.item_suggestion_textview,
                listOf("Плащ мужской", "Плащ женский", "Плащ-палатка", "Плащ дождевик")
            ){_, _ ->

            }
        )

        binding.toolbarMain.searchEditText.dropDownAnchor = R.id.anchor

        binding.toolbarMain.filterBtn.isVisible = true
        binding.toolbarMain.materialToolbar.logo = null
        binding.toolbarMain.filterBtn.setOnClickListener {
            findNavController().navigate(CatalogFragmentDirections.actionCatalogFragmentToFilterFragment())
        }

        val searchEditText = binding.toolbarMain.searchEditText

        val searchFlow = kotlinx.coroutines.flow.callbackFlow<kotlin.String> {
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
                        currentSuggestionModels = null

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        binding.searchBtn.isVisible = !p0.isNullOrEmpty()
                    }

                }
            searchEditText.addTextChangedListener(searchListener)
            awaitClose { searchEditText.removeTextChangedListener(searchListener) }
        }
        presenter.collectSearchFlow(searchFlow)


        binding.toolbarMain.materialToolbar.setOnMenuItemClickListener { menuItem ->
            return@setOnMenuItemClickListener when(menuItem.itemId){
                R.id.favorite -> {
                    findNavController().navigate(CatalogFragmentDirections.actionCatalogFragmentToFavoritesFragment())
                    true
                }
                R.id.cart -> {
                    findNavController().navigate(R.id.ordersCartFragment)
                    true
                }
                else -> true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.cancelSearchJob()
        queryStr = null
    }

    private fun showBottomNav() {
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun loadProducts(queryStr: String?) {
        if(args?.forWho != null)
            return
        val useDefaultFilter = arguments?.getBoolean(USE_FILTER, false) ?: false
        presenter.getProducts(queryStr = queryStr, isFiltered = true, productCategories = null, useDefaultFilter = useDefaultFilter)

    }

    private fun loadFilter(){
        presenter.getFilter()
    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        if(result is Unit){
            binding.changeRegionCard.isVisible = false
            loadProducts(args?.query)
        }
        if(result is List<*>) {
            productAdapter.updateList(result as List<Product>)
            binding.noProductsFoundTextView.isVisible = result.isEmpty()
        }

        if(result is Filter)
            binding.changeRegionCard.isVisible = !result.isCurrentLocationChosen

        if (result is Address)
            if(result.fullAddress?.isNotEmpty() == true) {
                binding.regionInfoTextView.text =
                    "Хотите искать объявления в ${result.fullAddress.substringBefore(",")}?"
                binding.yesTextView.isVisible = true
                binding.yesTextView.setOnClickListener {
                    presenter.changeUserCity(result.fullAddress.substringBefore(","))
                }
            }
    }

    override fun loading() {
        binding.loader.isVisible = true
    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {
        binding.loader.isVisible = false
        binding.toolbarMain.searchEditText.setAdapter(
            SuggestionArrayAdapter(
                requireContext(),
                R.layout.item_suggestion_textview,
                list
            ){ position, string ->
                binding.toolbarMain.searchEditText.dismissDropDown()
                presenter.cancelSearchJob()
                binding.toolbarMain.searchEditText.setAdapter(null)
                binding.toolbarMain.searchEditText.setText(string)
                initToolbar()
                binding.toolbarMain.filterBtn.isVisible = false
                currentSuggestionModels = objectList[position]

            }
        )
        binding.toolbarMain.searchEditText.showDropDown()
    }

    companion object {
        const val USE_FILTER = "use_filter"
    }
}