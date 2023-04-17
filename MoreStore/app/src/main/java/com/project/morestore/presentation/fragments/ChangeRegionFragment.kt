package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.presentation.adapters.RegionsAdapter

import com.project.morestore.data.models.Address
import com.project.morestore.data.models.Region
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class ChangeRegionFragment : MvpAppCompatFragment(R.layout.fragment_change_region), UserMvpView {
    private val binding: com.project.morestore.databinding.FragmentChangeRegionBinding by viewBinding()
    @Inject lateinit var userPresenter: UserPresenter
    private val presenter by moxyPresenter { userPresenter }
    private var cities = listOf<Region>()
    private var cityAdapter: RegionsAdapter by autoCleared()
    private lateinit var searchFlow: kotlinx.coroutines.flow.Flow<String>
    private val args: ChangeRegionFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        getCities()
        hideBottomNav()
        setClickListeners()
        initList()
        initEditText()
    }

    private fun initEditText(){
        val searchEditText = binding.searchEditText
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

    private fun initList(){
        cityAdapter = RegionsAdapter(true, true) { city ->
            binding.searchEditText.setText(city)
        }
        with(binding.citiesList){
            adapter = cityAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun initToolbar(){
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun getCities(){
        presenter.getAllCities()
    }

    private fun hideBottomNav(){
        (activity as MainActivity).showBottomNavBar(false)
    }

    private fun setClickListeners(){
        binding.crossIcon.setOnClickListener {
            binding.searchEditText.setText("")
        }

        binding.view12.setOnClickListener { findNavController().navigate(ChangeRegionFragmentDirections.actionChangeRegionFragmentToAutoLocationFragment(isForFilter = args.isForFilter)) }
        binding.chooseRegionBtn.setOnClickListener {
            if(binding.searchEditText.text.isNullOrEmpty()){
                return@setOnClickListener
            }
            if(args.isForFilter)
            presenter.changeUserCity(binding.searchEditText.text.toString())
            else
                presenter.changeCurrentUserAddress(Address(binding.searchEditText.text.toString(), -1))

        }
    }

    override fun success(result: Any) {
        binding.loader.isVisible = false
        findNavController().popBackStack()

    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {
        binding.loader.isVisible = true

    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        val citiesList = (result as List<Region>)
        if(cities.isEmpty()) {
            cities = citiesList
        }
            presenter.collectRegionSearchFlow(searchFlow, cities)

        cityAdapter.updateList(citiesList)
    }
}
