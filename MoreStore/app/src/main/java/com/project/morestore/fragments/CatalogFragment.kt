package com.project.morestore.fragments

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentCatalogBinding
import com.project.morestore.util.autoCleared

class CatalogFragment: Fragment(R.layout.fragment_catalog) {
    private val binding: FragmentCatalogBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolbar()
        setClickListeners()


    }

    private fun setClickListeners(){
        binding.changeRegionTextView.setOnClickListener { findNavController().navigate(CatalogFragmentDirections.actionCatalogFragmentToChangeRegionFragment()) }
    }



    private fun initList(){
        productAdapter = ProductAdapter(10){findNavController().navigate(CatalogFragmentDirections.actionCatalogFragmentToProductDetailsFragment())}
        with(binding.productList){
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
            productAdapter.notifyDataSetChanged()
        }
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

        binding.toolbarMain.filterBtn.isVisible = true
        binding.toolbarMain.materialToolbar.logo = null
        binding.toolbarMain.filterBtn.setOnClickListener {
            findNavController().navigate(CatalogFragmentDirections.actionCatalogFragmentToFilterFragment())
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
}