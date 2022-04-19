package com.project.morestore.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.BrandCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep5Binding
import com.project.morestore.models.ProductBrand
import com.project.morestore.models.SuggestionModels
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
    private var brandName = ""


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
            findNavController().navigate(CreateProductStep5FragmentDirections.actionCreateProductStep5FragmentToCreateProductNameFragment(category = args.category, forWho = args.forWho, brand = it))
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
            setFragmentResultListener(AddBrandFragment.REQUEST_BRAND){_, bundle ->
                brandName = bundle.getString(AddBrandFragment.BRAND)!!
            }
            findNavController().navigate(R.id.addBrandFragment)
        }

        binding.skipBtn.setOnClickListener { findNavController().navigate(CreateProductStep5FragmentDirections.actionCreateProductStep5FragmentToSkipStepDialog(args.category, args.forWho, true)) }
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 5 из 6"
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.mainFragment) }

    }







    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        if(brands.isEmpty()){
            brands = result as List<ProductBrand>

        }
        brandAdapter.updateList(result as List<ProductBrand>)
        presenter.collectBrandSearchFlow(searchFlow, brands)
    }

    override fun loading() {
        binding.loader.isVisible = true
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