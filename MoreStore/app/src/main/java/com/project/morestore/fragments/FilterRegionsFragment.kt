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

class FilterRegionsFragment: MvpAppCompatFragment(R.layout.fragment_regions), UserMvpView {
   private val binding: FragmentRegionsBinding by viewBinding()
   private var regionsAdapter: RegionsAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private var regions = listOf<Region>()
    private lateinit var searchFlow: Flow<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRegionsRecyclerView()
        initEditText()
        initViews()

    }

    override fun onStop() {
        super.onStop()
        safeFilter()
    }

    private fun initViews(){
        if(com.project.morestore.singletones.FilterState.filter.currentLocation != null){
            binding.textView42.text = com.project.morestore.singletones.FilterState.filter.currentLocation!!.name
        }
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
        //if (regions.all{ it.isChecked != true })
            //regions.forEachIndexed{index,_->
               // regions[index].isChecked = true
           // }
        com.project.morestore.singletones.FilterState.filter.regions = regions
    }
   private fun loadFilter(){
       // if(FilterState.regions.isNotEmpty()) {
           // Log.d("Debug", "loadFilter")
           // regionsAdapter.updateList(FilterState.regions)

        //}
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
        if(list.size + 1 == com.project.morestore.singletones.FilterState.filter.regions.size){
            if(com.project.morestore.singletones.FilterState.filter.currentLocation != null && !com.project.morestore.singletones.FilterState.filter.isCurrentLocationFirstLoaded){
                com.project.morestore.singletones.FilterState.filter.regions.first { it.id == com.project.morestore.singletones.FilterState.filter.currentLocation?.id }.apply { isChecked = true }
                com.project.morestore.singletones.FilterState.filter.isCurrentLocationFirstLoaded = true
            }
            Log.d("mylog3", "updateList")
            regionsAdapter.updateList(com.project.morestore.singletones.FilterState.filter.regions)
        }else {
            if(com.project.morestore.singletones.FilterState.filter.currentLocation != null && !com.project.morestore.singletones.FilterState.filter.isCurrentLocationFirstLoaded){
                list.first { it.id == com.project.morestore.singletones.FilterState.filter.currentLocation?.id }.apply { isChecked = true }
                com.project.morestore.singletones.FilterState.filter.isCurrentLocationFirstLoaded = true
            }

            regionsAdapter.updateList(listOf(Region(0, "Все города", 1, false)) + list)
        }



    }

    override fun successNewCode() {

    }
}