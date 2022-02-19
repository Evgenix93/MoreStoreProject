package com.project.morestore.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.BrandCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep5Binding
import com.project.morestore.models.ProductBrand
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductStep5Fragment: MvpAppCompatFragment(R.layout.fragment_create_product_step5), MainMvpView {
    private val binding: FragmentCreateProductStep5Binding by viewBinding()
    private var brandAdapter: BrandCreateProductAdapter by autoCleared()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private lateinit var searchFlow: Flow<String>
    private var brands = listOf<ProductBrand>()
    private val args: CreateProductStep5FragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initSearch()
        initToolbar()
        setClickListeners()
        getBrands()

    }




    private fun initList(){
        brandAdapter = BrandCreateProductAdapter{
            findNavController().navigate(CreateProductStep5FragmentDirections.actionCreateProductStep5FragmentToCreateProductFragment(args.category))
        }
        with(binding.brandList){
            adapter = brandAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

    }

    private fun getBrands(){
        presenter.getBrands()
    }

    private fun initSearch(){
        val searchEditText = binding.editText3
        searchFlow = kotlinx.coroutines.flow.callbackFlow<kotlin.String> {
            val searchListener =
                object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {

                    }

                    override fun onTextChanged(
                        newText: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {
                        sendBlocking(newText.toString())

                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }

                }
            searchEditText.addTextChangedListener(searchListener)
            awaitClose { searchEditText.removeTextChangedListener(searchListener) }
        }

    }

    private fun setClickListeners(){
        binding.crossIcon.setOnClickListener {
            binding.editText3.setText("")
        }

        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep5FragmentDirections.actionCreateProductStep5FragmentToCreateProductHowToSellFragment())
        }

        binding.addBrandTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep5FragmentDirections.actionCreateProductStep5FragmentToCreateProductAddBrandFragment())
        }

        binding.skipBtn.setOnClickListener { findNavController().navigate(R.id.skipStepDialog) }
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 5 из 6"
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }







    override fun loaded(result: Any) {
        if(brands.isEmpty()){
            brands = result as List<ProductBrand>
            presenter.collectBrandSearchFlow(searchFlow, brands)
        }
        brandAdapter.updateList(result as List<ProductBrand>)


    }

    override fun loading() {
    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>) {

    }

    override fun success() {

    }


}