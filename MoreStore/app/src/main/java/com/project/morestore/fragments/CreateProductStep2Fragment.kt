package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.CategoryCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep2Binding
import com.project.morestore.models.Category
import com.project.morestore.models.ProductCategory
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductStep2Fragment: MvpAppCompatFragment(R.layout.fragment_create_product_step2), MainMvpView {
    private val binding: FragmentCreateProductStep2Binding by viewBinding()
    private var categoryAdapter: CategoryCreateProductAdapter by autoCleared()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
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
        binding.toolbar.titleTextView.text = "Шаг 2 из 6"
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }

    private fun initList(){
        categoryAdapter = CategoryCreateProductAdapter { category ->
            if(category.name == "Джинсы"){
                findNavController().navigate(CreateProductStep2FragmentDirections.actionCreateProductStep2FragmentToCreateProductStep4Fragment(category))
                return@CategoryCreateProductAdapter
            }
            if(category.name == "Обувь"){
                findNavController().navigate(CreateProductStep2FragmentDirections.actionCreateProductStep2FragmentToCreateProductStep3Fragment(category))
                return@CategoryCreateProductAdapter
            }

            findNavController().navigate(CreateProductStep2FragmentDirections.actionCreateProductStep2FragmentToCreateProductStep5Fragment(category))

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
        categoryAdapter.updateList(result as List<ProductCategory>)

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