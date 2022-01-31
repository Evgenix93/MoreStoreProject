package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.SuggestionArrayAdapter
import com.project.morestore.databinding.FragmentChangeRegionBinding
import com.project.morestore.models.Region
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ChangeRegionFragment : MvpAppCompatFragment(R.layout.fragment_change_region), UserMvpView {
    private val binding: FragmentChangeRegionBinding by viewBinding()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        getCities()
        hideBottomNav()
        setClickListeners()
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

        binding.view12.setOnClickListener { findNavController().navigate(ChangeRegionFragmentDirections.actionChangeRegionFragmentToAutoLocationFragment()) }
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        val citiesList = (result as List<Region>).filter { it.idCountry == 1.toLong() }.map { it.name }
        Log.d("mylog", citiesList.toString())
        binding.searchEditText.setAdapter(
            SuggestionArrayAdapter(
                requireContext(),
                R.layout.item_suggestion_textview,
                //listOf("каол", "fdf", "defoi")
            citiesList
            )
        )

    }

    override fun successNewCode() {

    }

}
