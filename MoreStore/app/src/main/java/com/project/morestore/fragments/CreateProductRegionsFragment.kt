package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.RegionsAdapter
import com.project.morestore.databinding.FragmentRegionsBinding
import com.project.morestore.models.Region
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductRegionsFragment: MvpAppCompatFragment(R.layout.fragment_regions), MainMvpView {
    private val binding: FragmentRegionsBinding by viewBinding()
    private var regionsAdapter: RegionsAdapter by autoCleared()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var regions = listOf<Region>()
    private lateinit var searchFlow: Flow<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initToolbar()
        initRegionsRecyclerView()
        initEditText()
        initViews()

    }


    private fun initViews(){
        if(com.project.morestore.singletones.FilterState.filter.currentLocation != null){
            binding.textView42.text = com.project.morestore.singletones.FilterState.filter.currentLocation!!.name
        }
        binding.imageView25.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.black))
        binding.textView42.setTextColor(resources.getColor(R.color.black))
        binding.showOffersBtn.isVisible = false
    }

    private fun initRegionsRecyclerView(){
        regionsAdapter = RegionsAdapter(false, false) {saveRegion(it)}
        binding.regionsRecyclerView.adapter = regionsAdapter
        binding.regionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        presenter.getAllCities()
    }


    /*private fun initToolbar() {
        binding.toolbarCreateProduct.titleTextView.text = "Поиск города"
        binding.toolbarCreateProduct.actionIcon.isVisible = false
        binding.toolbarCreateProduct.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbarFilter.root.isVisible = false

    }*/

    private fun saveRegion(region: String){
        presenter.updateCreateProductData(address = region)
        findNavController().popBackStack()
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
                        trySendBlocking(newText.toString())

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

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun showOnBoarding() {
        TODO("Not yet implemented")
    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {
        TODO("Not yet implemented")
    }

    override fun loginFailed() {
        TODO("Not yet implemented")
    }

    override fun success() {
        TODO("Not yet implemented")
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
        regionsAdapter.updateList(regions)
    }
}