package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ColorsAdapter
import com.project.morestore.databinding.FragmentColorsBinding
import com.project.morestore.models.Filter
import com.project.morestore.models.Property
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterColorsFragment: MvpAppCompatFragment(R.layout.fragment_colors), UserMvpView {
    private val binding:FragmentColorsBinding by viewBinding()
    private var colorsAdapter: ColorsAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initColorsRecyclerView()
        getColors()
        //loadColors()
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
        binding.toolbarFilter.imageView2.setOnClickListener{
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

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
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
        //colorsAdapter.updateColors(result as List<Color>)
    }

    override fun successNewCode() {

    }
}