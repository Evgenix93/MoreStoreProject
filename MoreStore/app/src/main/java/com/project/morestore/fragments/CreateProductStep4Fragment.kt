package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.CloathStyleCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep3Binding
import com.project.morestore.databinding.FragmentCreateProductStep4Binding
import com.project.morestore.models.Property
import com.project.morestore.models.Property2
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductStep4Fragment: MvpAppCompatFragment(R.layout.fragment_create_product_step4), MainMvpView {
    private val binding: FragmentCreateProductStep4Binding by viewBinding()
    private var styleAdapter: CloathStyleCreateProductAdapter by autoCleared()
    private val args: CreateProductStep4FragmentArgs by navArgs()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        setClickListeners()
        initToolbar()
        getStyles()
    }


    private fun setClickListeners(){
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep4FragmentDirections.actionCreateProductStep4FragmentToCreateProductHowToSellFragment())
        }

        binding.skipBtn.setOnClickListener { findNavController().navigate(R.id.skipStepDialog) }
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 4 из 6"
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }


    private fun initList(){
        styleAdapter = CloathStyleCreateProductAdapter{ property ->
            presenter.updateCreateProductData(extProperty = Property2(property.id, property.idCategory ?: 0))
            findNavController().navigate(CreateProductStep4FragmentDirections.actionCreateProductStep4FragmentToCreateProductStep5Fragment(args.category, args.forWho))
        }
        with(binding.itemsList){
            adapter = styleAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }



    }

    private fun getStyles(){
        if(args.category.name == "Джинсы"){
            presenter.getJeansStyles()
        }else{
            presenter.getTopClotStyles()
        }
    }



    override fun loaded(result: Any) {
        styleAdapter.updateList(result as List<Property>)

    }

    override fun loading() {

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun success() {

    }


}