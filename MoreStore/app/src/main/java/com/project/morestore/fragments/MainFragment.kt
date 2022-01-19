package com.project.morestore.fragments

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CursorAdapter
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.MainFragmenViewPagerAdapter
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentMainBinding
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.presenters.AuthPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class MainFragment: MvpAppCompatFragment(R.layout.fragment_main), AuthMvpView {
    private val presenter by moxyPresenter { AuthPresenter(requireContext()) }
    private val binding: FragmentMainBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavBar()
        presenter.loadOnBoardingViewed()
        initToolbar()
        initLists()
        initViewPager()
    }


    private fun initLists(){
        productAdapter = ProductAdapter(6){findNavController().navigate(MainFragmentDirections.actionMainFragmentToProductDetailsFragment())}
        with(binding.forWomenRecyclerView){
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)

        }

        with(binding.forKidsRecyclerView){
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)

        }


    }

    private fun initViewPager(){
        binding.viewPager2.adapter = MainFragmenViewPagerAdapter(this)
        binding.dots.setViewPager2(binding.viewPager2)
    }

    private fun showBottomNavBar(){
        val mainActivity = activity as MainActivity
        mainActivity.showBottomNavBar(true)
    }

    private fun initToolbar(){
        val searchView = binding.toolbarMain.materialToolbar.menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchView.suggestionsAdapter = initSuggestions()
                return true
            }

        })
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




        return object : androidx.cursoradapter.widget.CursorAdapter(requireContext(), cursor){
            override fun newView(p0: Context?, p1: Cursor?, p2: ViewGroup?): View {
                return LayoutInflater.from(p0).inflate(R.layout.item_suggestion, p2, false)

            }

            override fun bindView(p0: View?, p1: Context?, p2: Cursor?) {
                val textView =p0?.findViewById<TextView>(R.id.suggestionTextView)

                val str = p2?.getString(1)
                textView?.text = str

            }

        }
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun showOnBoarding() {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToOnboarding1Fragment())

    }
}