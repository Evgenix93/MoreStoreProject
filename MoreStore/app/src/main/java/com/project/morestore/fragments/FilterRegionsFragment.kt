package com.project.morestore.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.RegionsAdapter
import com.project.morestore.databinding.FragmentRegionsBinding
import com.project.morestore.models.Address
import com.project.morestore.models.Region
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

open class FilterRegionsFragment: MvpAppCompatFragment(R.layout.fragment_regions), UserMvpView {
   protected val binding: FragmentRegionsBinding by viewBinding()
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
        setClickListeners()


    }

    private fun saveRegions(regions: List<Region>){
        presenter.saveRegions(regions)
    }

    private fun getCurrentUserAddress(){
        presenter.getCurrentUserAddress()
    }

    private fun setClickListeners(){
        binding.showOffersBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
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
        regionsAdapter = RegionsAdapter(false, true) {}
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

    private fun initToolbar() {
        binding.toolbarFilter.titleTextView.text = "Регион поиска"
        binding.toolbarFilter.actionTextView.isVisible = false
        binding.toolbarFilter.arrowBackImageView.setOnClickListener {
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
        if(result is Address){
            if(result.fullAddress?.isNotEmpty() == true) {
                binding.imageView25.isVisible = true
                binding.textView42.isVisible = true
                binding.textView42.text = result.fullAddress.substringBefore(",")
                val region = regions.find { it.name == result.fullAddress.substringBefore(",") }
                if(region != null)
                binding.textView42.setOnClickListener {
                    //regions.forEach { it.isChecked = false }
                    //regions[regions.indexOf(region)].isChecked = true
                        //saveRegions(regions)

                    regions.forEach { it.isChecked = false }
                    regions[regions.indexOf(region)].isChecked = true
                    regionsAdapter.updateList(listOf(Region(-1, "Все города", 1, false)) + regions)
                    findNavController().popBackStack()
                }

            }
            return
        }
        if(regions.isEmpty()){
            regions = result as List<Region>
            presenter.collectRegionSearchFlow(searchFlow, regions)
            getCurrentUserAddress()
        }
        val list = (result as List<Region>)
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