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
import com.project.morestore.presentation.adapters.ShoosTypeCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep3Binding
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
class CreateProductStep3Fragment: MvpAppCompatFragment(R.layout.fragment_create_product_step3), CreateProductMvpView {
    private val binding: FragmentCreateProductStep3Binding by viewBinding()
    private var shoosTypeAdapter: ShoosTypeCreateProductAdapter by autoCleared()
    private val args: CreateProductStep3FragmentArgs by navArgs()
    @Inject
    lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        setClickListeners()
        initToolbar()
        getShoosTypes()
    }


    private fun setClickListeners(){
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep3FragmentDirections.actionCreateProductStep3FragmentToCreateProductHowToSellFragment())
        }
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 2 из 5"
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.mainFragment) }

    }


    private fun initList(){
        shoosTypeAdapter = ShoosTypeCreateProductAdapter { property ->
            presenter.updateCreateProductData(extProperty = Property2(property.id, property.idCategory ?: 0))
            findNavController().navigate(CreateProductStep3FragmentDirections.actionCreateProductStep3FragmentToCreateProductStep5Fragment(args.category, args.forWho))
        }
        with(binding.itemsList){
            adapter = shoosTypeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }


    }

    private fun getShoosTypes(){
        presenter.getShoosTypes()


    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        shoosTypeAdapter.updateList(result as List<Property>)
    }

    override fun loading() {
        binding.loader.isVisible = true

    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }



}