package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentFilterSizesColthesBinding
import com.project.morestore.models.Filter
import com.project.morestore.models.Property

import com.project.morestore.models.SizeLine
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter

import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterSizesFragment : MvpAppCompatFragment(R.layout.fragment_filter_sizes_colthes),
    UserMvpView {
    private val binding: FragmentFilterSizesColthesBinding by viewBinding()
    private var sizeAdapter: SizeLineAdapter by autoCleared()
    private var isForWomen = true
    private var isSizesLoaded = false
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }

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
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }



    private fun getSizes(){
        if (isForWomen) {
            presenter.getTopSizesWomen()
            presenter.getBottomSizesWomen()
        }
        else {
            presenter.getTopSizesMen()
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

        Log.d("mytest", filter.chosenTopSizes.size.toString())



        when {
         filter.chosenForWho[0] ->   if (sizeAdapter.getChosenSizes().size == filter.chosenTopSizes.size) {
                sizeAdapter.updateList(filter.chosenTopSizes, null)
            }

            filter.chosenForWho[1] ->  if (sizeAdapter.getChosenSizes().size == filter.chosenTopSizesMen.size) {
                sizeAdapter.updateList(filter.chosenTopSizesMen, null)
            }


        }
    }

    private fun saveSizes(){
        val chosenTopSizes = sizeAdapter.getChosenSizes()
        val chosenBottomSizes = sizeAdapter.getChosenBottomSizes()


        if(isForWomen) {
            presenter.saveTopSizes(chosenTopSizes)
            presenter.saveBottomSizes(chosenBottomSizes)
        }else{
            presenter.saveTopSizesMen(chosenTopSizes)
            presenter.saveBottomSizesMen(chosenBottomSizes)
        }

    }



    override fun onStop() {
        super.onStop()
        saveSizes()

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
        if(result is List<*> ) {
            val sizes =
                (result as List<Property>)
                    .map {
                        val list = it.ico?.split(';').orEmpty()
                        SizeLine(it.id.toInt(), it.name, list[0].removePrefix("W").removeSurrounding("'"), list[1].removePrefix("IT/RU/FR").removeSurrounding("'"), list[2].removePrefix("US").removeSurrounding("'"), list[3].removePrefix("UK").removeSurrounding("'"), false, idCategory = it.idCategory?.toInt()!!)
                    }

            if(result[0].idCategory?.toInt() == 1 || result[0].idCategory?.toInt() == 4){
                sizeAdapter.updateList(sizes + listOf(SizeLine(0, "", "", "", "", "", false, -1)), null)
            }else{
                sizeAdapter.updateList( sizeAdapter.getChosenSizes(), sizes + listOf(SizeLine(0, "", "", "", "", "", false, -1)) )
            }




            isSizesLoaded = true
            loadFilter()



        }

        if(result is Filter){
            bindFilter(result)


        }


    }

    override fun successNewCode() {

    }
}