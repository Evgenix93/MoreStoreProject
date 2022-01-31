package com.project.morestore.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.FilterState
import com.project.morestore.R
import com.project.morestore.adapters.RegionsAdapter
import com.project.morestore.databinding.FragmentRegionsBinding
import com.project.morestore.models.Region
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class RegionsFragment: MvpAppCompatFragment(R.layout.fragment_regions), UserMvpView {
   private val binding: FragmentRegionsBinding by viewBinding()
   private var regionsAdapter: RegionsAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private var regions = listOf<Region>()
    private lateinit var searchFlow: Flow<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRegionsRecyclerView()
        loadFilter()
        initEditText()
    }

    override fun onStop() {
        super.onStop()
        safeFilter()
    }

    private fun initRegionsRecyclerView(){
        regionsAdapter = RegionsAdapter()
        binding.regionsRecyclerView.adapter = regionsAdapter
        binding.regionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        presenter.getAllCities()
    }


   private fun safeFilter(){
       Log.d("Debug", "safeFilter")
       //Log.d("Debug", "${regionsAdapter.regionsChecked}")
         val regions = regionsAdapter.getCurrentRegions()
        if (regions.all{ it.isChecked != true })
            regions.forEachIndexed{index,_->
                regions[index].isChecked = true
            }
        FilterState.regions = regions
    }
   private fun loadFilter(){
        if(FilterState.regions.isNotEmpty()) {
            Log.d("Debug", "loadFilter")
            regionsAdapter.updateList(FilterState.regions)

        }
    }

    private fun initToolbar() {
        binding.toolbarFilter.titleTextView.text = "Регион поиска"
        binding.toolbarFilter.actionTextView.isVisible = false
        binding.toolbarFilter.imageView2.setOnClickListener {
            findNavController().popBackStack()
        }
    }


   private fun initEditText() {
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

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }


    override fun success(result: Any) {

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {
        showLoading(true)

    }

    override fun loaded(result: Any) {
        showLoading(false)
        if(regions.isEmpty()){
            regions = result as List<Region>
            presenter.collectRegionSearchFlow(searchFlow, regions)
        }
        val list = (result as List<Region>).filter { it.idCountry == 1.toLong() }
        regionsAdapter.updateList(list)



    }

    override fun successNewCode() {

    }
}