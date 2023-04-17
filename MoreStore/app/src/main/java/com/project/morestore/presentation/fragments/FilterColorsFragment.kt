package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.ColorsAdapter
import com.project.morestore.databinding.FragmentColorsBinding
import com.project.morestore.data.models.Filter
import com.project.morestore.data.models.Property
import com.project.morestore.domain.presenters.FilterPresenter
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.presentation.mvpviews.FilterView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FilterColorsFragment: MvpAppCompatFragment(R.layout.fragment_colors), FilterView {
    private val binding:FragmentColorsBinding by viewBinding()
    private var colorsAdapter: ColorsAdapter by autoCleared()
    @Inject
    lateinit var filterPresenter: FilterPresenter
    private val presenter by moxyPresenter { filterPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initColorsRecyclerView()
        getColors()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.showOffersBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        saveColors()
    }

    private fun initToolbar(){
        binding.toolbarFilter.titleTextView.text = "Цвет"
        binding.toolbarFilter.actionTextView.isVisible = false
        binding.toolbarFilter.arrowBackImageView.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun initColorsRecyclerView(){
      colorsAdapter = ColorsAdapter(requireContext(), true){}
      binding.colorsRecyclerView.adapter = colorsAdapter
      binding.colorsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun saveColors(){
       presenter.saveColors(colorsAdapter.getColors())
    }

    private fun getFilter(){
      presenter.getFilter()
    }

    private fun getColors(){
      presenter.getColors()
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
        when(result){
            is List<*> -> {
                val properties = result as List<Property>
                colorsAdapter.updateColors(listOf(Property(0, "Все цвета", null, 0, false)) + properties)
                getFilter()
            }

            is Filter ->{
                if(result.colors.size == colorsAdapter.getColors().size)
                    colorsAdapter.updateColors(result.colors)
            }
        }
    }
}