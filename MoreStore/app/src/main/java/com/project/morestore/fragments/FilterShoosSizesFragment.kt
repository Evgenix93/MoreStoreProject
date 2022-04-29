package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentFilterSizesShoesBinding
import com.project.morestore.models.Filter
import com.project.morestore.models.Property
import com.project.morestore.models.SizeLine
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterShoosSizesFragment: MvpAppCompatFragment(R.layout.fragment_filter_sizes_shoes), UserMvpView {
    private val binding: FragmentFilterSizesShoesBinding by viewBinding()
    private var sizeAdapter: SizeLineAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }

    private var isForWomen = true
    private var isSizesLoaded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolbar()
        loadFilter()
        setClickListenrs()
    }

    private fun setClickListenrs(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }

    private fun initList(){
        sizeAdapter = SizeLineAdapter(true){}
        with(binding.sizesList) {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }




    }

    private fun getSizes(){
        if(isForWomen){
            presenter.getShoosSizesWomen()
        }else{
            presenter.getShoosSizesMen()
        }

    }

    private fun initToolbar(){
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

    private fun loadFilter(){
        presenter.getFilter()
    }

    private fun bindFilter(filter: Filter){
        isForWomen = filter.chosenForWho[0]

        if(!isSizesLoaded){
            getSizes()
        }

        if(isForWomen) {
            if (sizeAdapter.getChosenSizes().size == filter.chosenShoosSizesWomen.size || sizeAdapter.getChosenSizes().size == filter.chosenShoosSizesWomen.size + 1) {
                sizeAdapter.updateList(filter.chosenShoosSizesWomen, null)
            }
        }else{
            if (sizeAdapter.getChosenSizes().size == filter.chosenShoosSizesMen.size || sizeAdapter.getChosenSizes().size == filter.chosenShoosSizesMen.size + 1) {
                sizeAdapter.updateList(filter.chosenShoosSizesMen, null)
            }
        }

    }

    private fun saveShoosSizes(){
        if(isForWomen)
        presenter.saveShoosSizes(sizeAdapter.getChosenSizes())
        else presenter.saveShoosSizesMen(sizeAdapter.getChosenSizes())
    }

    override fun onStop() {
        super.onStop()

        saveShoosSizes()
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {
        showLoading(false)

    }

    override fun loading() {
        showLoading(true)

    }

    override fun loaded(result: Any){
        showLoading(false)
        if(result is List<*>) {
            val listShoosSizes =
                (result as List<Property>)
                    .map {
                        val list = it.ico?.split(';').orEmpty()
                        SizeLine(it.id.toInt(), it.name, "", list[0].removePrefix("FR").removeSurrounding("'"), list[1].removePrefix("US").removeSurrounding("'"), list[2].removePrefix("UK").removeSurrounding("'"), false, idCategory = it.idCategory?.toInt()!!)

                    }
                //(result as List<Size>).filter { it.id_category == 3 }.sortedBy { it.toInt() }
                  //  .ma
            //if(FilterState.filter.chosenShoosSizes.size == listShoosSizes.size + 1){
              //  sizeAdapter.updateList(FilterState.filter.chosenShoosSizes, null)//+ listOf(SizeLine(0, "", "", "", "", "", false)))
            //}else {
                sizeAdapter.updateList(listShoosSizes + listOf(SizeLine(0, "", "", "", "", "", false, idCategory = -1)), null)
            //}
            //shoosSizes = listShoosSizes
            isSizesLoaded = true
            loadFilter()

        }//else{
           // val sizes = result as List<SizeLine>
           // if(shoosSizes.size == sizes.size){
             //   sizeAdapter.updateList(sizes, null)
            //}
        //}

        if(result is Filter){
            bindFilter(result)

        }


    }



    override fun successNewCode() {

    }
}