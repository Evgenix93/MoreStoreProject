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
import com.project.morestore.presentation.adapters.CategoryCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep2Binding
import com.project.morestore.data.models.ProductCategory
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
class CreateProductStep2Fragment: MvpAppCompatFragment(R.layout.fragment_create_product_step2), CreateProductMvpView {
    private val binding: FragmentCreateProductStep2Binding by viewBinding()
    private var categoryAdapter: CategoryCreateProductAdapter by autoCleared()
    @Inject
    lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }
    private val args: CreateProductStep2FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        getCategories()
        setClickListeners()
        initToolbar()
    }

    private fun setClickListeners(){
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep2FragmentDirections.actionCreateProductStep2FragmentToCreateProductHowToSellFragment())
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
        categoryAdapter = CategoryCreateProductAdapter { category ->
            if(category.name == "Джинсы" || category.name == "Верхняя одежда"){
                findNavController().navigate(CreateProductStep2FragmentDirections.actionCreateProductStep2FragmentToCreateProductStep4Fragment(category, args.forWho))
                return@CategoryCreateProductAdapter
            }
            if(category.name == "Обувь"){
                findNavController().navigate(CreateProductStep2FragmentDirections.actionCreateProductStep2FragmentToCreateProductStep3Fragment(category, args.forWho))
                return@CategoryCreateProductAdapter
            }

            findNavController().navigate(CreateProductStep2FragmentDirections.actionCreateProductStep2FragmentToCreateProductStep5Fragment(category, args.forWho))

        }
        with(binding.itemsList){
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

    }

    private fun getCategories(){
        presenter.getCategories(args.forWho)

    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        categoryAdapter.updateList(result as List<ProductCategory>)

    }

    override fun loading() {
        binding.loader.isVisible = true

    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }



}