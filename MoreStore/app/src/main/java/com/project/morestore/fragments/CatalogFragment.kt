package com.project.morestore.fragments

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.adapters.SuggestionArrayAdapter
import com.project.morestore.databinding.FragmentCatalogBinding
import com.project.morestore.models.Product
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
    //private val args: CatalogFragmentArgs? by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolbar()
        setClickListeners()
        showBottomNav()
        loadProducts( arguments?.getString("query"), listOf("dfd", "fdf"))
    }

    private fun setClickListeners() {
        binding.changeRegionTextView.setOnClickListener {
            findNavController().navigate(
                CatalogFragmentDirections.actionCatalogFragmentToChangeRegionFragment()
            )
        }

        binding.searchBtn.setOnClickListener {
            presenter.getProducts(binding.toolbarMain.searchEditText.text.toString(), listOf())
        }
    }


    private fun initList() {
        productAdapter = ProductAdapter(10) {
            findNavController().navigate(
                CatalogFragmentDirections.actionCatalogFragmentToProductDetailsFragment(it)
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
            )
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

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        binding.searchBtn.isVisible = !p0.isNullOrEmpty()
                    }

                }
            searchEditText.addTextChangedListener(searchListener)
            awaitClose { searchEditText.removeTextChangedListener(searchListener) }
        }
        presenter.collectSearchFlow(searchFlow)




    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.cancelSearchJob()
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

    private fun loadProducts(queryStr: String?, filter: List<String>) {
        presenter.getProducts(queryStr, filter)

    }

    private fun loadCities(){

    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        productAdapter.updateList(result as List<Product>)


    }

    override fun loading() {
        binding.loader.isVisible = true

    }

    override fun error(message: String) {
        binding.loader.isVisible = false

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>) {
        binding.loader.isVisible = false
        binding.toolbarMain.searchEditText.setAdapter(
            SuggestionArrayAdapter(
                requireContext(),
                R.layout.item_suggestion_textview,
                list
            )
        )
        binding.toolbarMain.searchEditText.showDropDown()
    }
}