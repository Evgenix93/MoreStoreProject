package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.CloathStyleCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep4Binding
import com.project.morestore.data.models.Property
import com.project.morestore.data.models.Property2
import com.project.morestore.domain.presenters.CreateProductPresenter
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductStep4Fragment: MvpAppCompatFragment(R.layout.fragment_create_product_step4), CreateProductMvpView {
    private val binding: FragmentCreateProductStep4Binding by viewBinding()
    private var styleAdapter: CloathStyleCreateProductAdapter by autoCleared()
    private val args: CreateProductStep4FragmentArgs by navArgs()
    @Inject
    lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }

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

        binding.skipBtn.setOnClickListener { findNavController().navigate(CreateProductStep4FragmentDirections.actionCreateProductStep4FragmentToSkipStepDialog(args.category, args.forWho, false)) }
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 2 из 5"
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.mainFragment) }

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
        binding.loader.isVisible = false
        styleAdapter.updateList(result as List<Property>)

    }

    override fun loading() {
        binding.loader.isVisible = true

    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


}