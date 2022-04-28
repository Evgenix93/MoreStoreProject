package com.project.morestore.fragments

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.adapters.SuggestionArrayAdapter
import com.project.morestore.databinding.FragmentCatalogBinding
import com.project.morestore.models.Address
import com.project.morestore.models.Filter
import com.project.morestore.models.Product
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CatalogFragment : MvpAppCompatFragment(R.layout.fragment_catalog), MainMvpView {
    private val binding: FragmentCatalogBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
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
                        sendBlocking(newText.toString())
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
                else -> true
            }
        }





    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.cancelSearchJob()
        queryStr = null
    }

    private fun initSuggestions(): androidx.cursoradapter.widget.CursorAdapter {

        val items = mutableListOf<String>();

        //проверка
        items.add("Плащ мужской");
        items.add("Плащ женский");
        items.add("Плащ-палатка");
        items.add("Плащ дождевик");


        // Cursor
        val columns = arrayOf("_id", "text");
        val temp = arrayOf<Any>(0, "default")

        val cursor = MatrixCursor(columns);


        for (i in 0 until items.size) {

            temp[0] = i;
            temp[1] = items.get(i);

            cursor.addRow(temp);
        }




        return object : androidx.cursoradapter.widget.CursorAdapter(requireContext(), cursor) {
            override fun newView(p0: Context?, p1: Cursor?, p2: ViewGroup?): View {
                return LayoutInflater.from(p0).inflate(R.layout.item_suggestion, p2, false)

            }

            override fun bindView(p0: View?, p1: Context?, p2: Cursor?) {
                val textView = p0?.findViewById<TextView>(R.id.suggestionTextView)

                val str = p2?.getString(1)
                textView?.text = str

            }

        }
    }

    private fun showBottomNav() {
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun loadProducts(queryStr: String?) {
        if(args?.forWho != null)
            return
        presenter.getProducts(queryStr = queryStr, isFiltered = true, productCategories = null)

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
        if(result is List<*>)
        productAdapter.updateList(result as List<Product>)

        if(result is Filter)
            binding.changeRegionCard.isVisible = !result.isCurrentLocationChosen

        if (result is Address)
            if(result.fullAddress.isNotEmpty()) {
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

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {
        binding.loader.isVisible = false
        binding.toolbarMain.searchEditText.setAdapter(
            SuggestionArrayAdapter(
                requireContext(),
                R.layout.item_suggestion_textview,
                list
            ){ position, string ->
                Log.d("mylog", "click position $position")
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

    override fun success() {
        TODO("Not yet implemented")
    }

}