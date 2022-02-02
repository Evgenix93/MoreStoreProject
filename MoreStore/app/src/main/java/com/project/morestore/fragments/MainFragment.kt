package com.project.morestore.fragments

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.MainFragmenViewPagerAdapter
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.adapters.SuggestionArrayAdapter
import com.project.morestore.databinding.FragmentMainBinding
import com.project.morestore.models.Product
import com.project.morestore.models.User
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.AuthPresenter
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class MainFragment : MvpAppCompatFragment(R.layout.fragment_main), MainMvpView {
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private val binding: FragmentMainBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavBar()
        presenter.loadOnBoardingViewed()
        initToolbar()
        initLists()
        initViewPager()
        setClickListeners()
        loadFilter()
    }


    private fun initLists() {
        productAdapter =
            ProductAdapter(6) {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToProductDetailsFragment(
                        it
                    )
                )
            }
        with(binding.forWomenRecyclerView) {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            isNestedScrollingEnabled = false
        }

        with(binding.forKidsRecyclerView) {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            isNestedScrollingEnabled = false
        }

        presenter.getProducts(null)


    }

    private fun initViewPager() {
        binding.viewPager2.adapter = MainFragmenViewPagerAdapter(this)
        binding.dots.setViewPager2(binding.viewPager2)
    }

    private fun showBottomNavBar() {
        val mainActivity = activity as MainActivity
        mainActivity.showBottomNavBar(true)
    }

    private fun initToolbar() {
        val toolbar = binding.toolbarMain.materialToolbar
        val searchItem = toolbar.menu.findItem(R.id.search)
        searchItem.setOnMenuItemClickListener {
            binding.toolbarMain.searchFrameLayout.isVisible = true
            it.isVisible = false
            toolbar.menu.findItem(R.id.favorite).isVisible = false
            toolbar.menu.findItem(R.id.cart).isVisible = false
            toolbar.logo = null
            binding.toolbarMain.backImageView.isVisible = true
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
            toolbar.logo =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_logo_more_store, null)
        }


        val searchEditText = binding.toolbarMain.searchEditText
        searchEditText.dropDownAnchor = R.id.anchor

        val searchFlow = callbackFlow<String> {
            val searchListener = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
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

    private fun setClickListeners() {
        binding.searchBtn.setOnClickListener {
            val query = binding.toolbarMain.searchEditText.text.toString()
            findNavController().navigate(R.id.catalogFragment, Bundle().apply { putString("query", query) })
        }
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

    private fun loadFilter(){
        presenter.loadFilter()
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loaded(result: Any) {
        if(result is List<*>)
        productAdapter.updateList(result as List<Product>)

    }

    override fun loading() {

    }

    override fun showOnBoarding() {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToOnboarding1Fragment())
    }

    override fun loadedSuggestions(list: List<String>) {
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