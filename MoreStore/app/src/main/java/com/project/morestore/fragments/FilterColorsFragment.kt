package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ColorsAdapter
import com.project.morestore.databinding.FragmentColorsBinding
import com.project.morestore.models.Color
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
        loadColors()
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
      colorsAdapter = ColorsAdapter(requireContext())
      binding.colorsRecyclerView.adapter = colorsAdapter
      binding.colorsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun saveColors(){
        presenter.saveColors(colorsAdapter.getColors())
    }

    private fun loadColors(){
      presenter.loadColors()
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        colorsAdapter.updateColors(result as List<Color>)
    }

    override fun successNewCode() {

    }
}