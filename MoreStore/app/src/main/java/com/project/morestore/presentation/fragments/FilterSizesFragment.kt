package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentFilterSizesColthesBinding
import com.project.morestore.data.models.Filter
import com.project.morestore.data.models.Property

import com.project.morestore.data.models.SizeLine
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
class FilterSizesFragment : MvpAppCompatFragment(R.layout.fragment_filter_sizes_colthes),
    FilterView {
    private val binding: FragmentFilterSizesColthesBinding by viewBinding()
    private var sizeAdapter: SizeLineAdapter by autoCleared()
    private var isForWomen = true
    private var isSizesLoaded = false
    @Inject
    lateinit var filterPresenter: FilterPresenter
    private val presenter by moxyPresenter { filterPresenter }
    private val args: FilterSizesFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        loadFilter()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }


    private fun initList() {
        sizeAdapter = SizeLineAdapter(false){}
        with(binding.sizesList) {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun initToolBar() {
        binding.toolbar.titleTextView.text = "Размер"
        binding.toolbar.actionTextView.text = "Сбросить"
        binding.toolbar.actionTextView.setOnClickListener {
            sizeAdapter.cleanCheckBoxes()
        }
        binding.toolbar.arrowBackImageView.setOnClickListener { findNavController().popBackStack() }
    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }



    private fun getSizes(){
        if (isForWomen) {
            if(args.idCategory < 3 || args.idCategory == 10 || args.idCategory > 11)
            presenter.getTopSizesWomen()
            else
              presenter.getBottomSizesWomen()
        }
        else {
            if(args.idCategory < 3 || args.idCategory == 10 || args.idCategory > 11)
            presenter.getTopSizesMen()
            else
            presenter.getBottomSizesMen()
        }
    }

    private fun loadFilter(){
        presenter.getFilter()
    }

    private fun bindFilter(filter: Filter){
        isForWomen = filter.chosenForWho[0]

        if(!isSizesLoaded)
        getSizes()
        when {
         filter.chosenForWho[0] ->{
             if(args.idCategory < 3 || args.idCategory == 10 || args.idCategory > 11) {
                 if (sizeAdapter.getChosenSizes().size == filter.chosenTopSizesWomen.size) {
                     sizeAdapter.updateList(filter.chosenTopSizesWomen, null)
                 }
             }else {
                 if (sizeAdapter.getChosenSizes().size == filter.chosenBottomSizesWomen.size) {
                     sizeAdapter.updateList(filter.chosenBottomSizesWomen, null)
                 }
             }
         }
            filter.chosenForWho[1] -> {
                if(args.idCategory < 3 || args.idCategory == 10 || args.idCategory > 11) {
                    if (sizeAdapter.getChosenSizes().size == filter.chosenTopSizesMen.size) {
                        sizeAdapter.updateList(filter.chosenTopSizesMen, null)
                    }
                }else{
                    if (sizeAdapter.getChosenSizes().size == filter.chosenBottomSizesMen.size) {
                        sizeAdapter.updateList(filter.chosenBottomSizesMen, null)
                }
            }
        }
    }
    }

    private fun saveSizes(){
        val chosenSizes = sizeAdapter.getChosenSizes()
        if(isForWomen) {
            if(args.idCategory < 3 || args.idCategory == 10 || args.idCategory > 11)
            presenter.saveTopSizes(chosenSizes)
            else
              presenter.saveBottomSizes(chosenSizes)
        }else{
            if(args.idCategory < 3 || args.idCategory == 10 || args.idCategory > 11)
            presenter.saveTopSizesMen(chosenSizes)
            else
            presenter.saveBottomSizesMen(chosenSizes)
        }
    }
    override fun onStop() {
        super.onStop()
        saveSizes()

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
        if(result is List<*>) {
            Log.d("MyDebug", "idCategory = ${args.idCategory}")
            val sizes = (result as List<Property>)

            val sizeLines = sizes.map {
                        val list = it.ico?.split(';').orEmpty()
                        SizeLine(it.id.toInt(), it.name, list[0].removePrefix("W").removeSurrounding("'"), list[1].removePrefix("IT/RU/FR").removeSurrounding("'"), list[2].removePrefix("US").removeSurrounding("'"), list[3].removePrefix("UK").removeSurrounding("'"), false, idCategory = it.idCategory?.toInt()!!)
                    }

                sizeAdapter.updateList(sizeLines + listOf(SizeLine(0, "", "", "", "", "", false, -1)), null)
            isSizesLoaded = true
            loadFilter()
        }

        if(result is Filter){
            bindFilter(result)
        }
    }
}