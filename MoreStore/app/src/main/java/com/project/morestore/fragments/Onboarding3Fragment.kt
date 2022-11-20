package com.project.morestore.fragments

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
import com.project.morestore.adapters.CategoryAdapter
import com.project.morestore.databinding.FragmentOnboarding3Binding
import com.project.morestore.models.BrandsPropertiesDataWrapper
import com.project.morestore.models.Category
import com.project.morestore.models.ProductBrand
import com.project.morestore.mvpviews.OnBoardingMvpView

import com.project.morestore.presenters.OnboardingPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class Onboarding3Fragment : MvpAppCompatFragment(R.layout.fragment_onboarding3), OnBoardingMvpView {
    private val binding: FragmentOnboarding3Binding by viewBinding()
    private val args: Onboarding3FragmentArgs by navArgs()
    private var categoryAdapter: CategoryAdapter by autoCleared()
    @Inject
    lateinit var onboardingPresenter: OnboardingPresenter
    private val presenter by moxyPresenter { onboardingPresenter }
    private var onBoardingData: BrandsPropertiesDataWrapper? = null
    private var allBrands: List<ProductBrand> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setClickListeners()
        getCategories()
        getOnBoardingData()
    }

    private fun initRecyclerView() {
        categoryAdapter = CategoryAdapter(true, requireContext()) { id, isChecked ->
            presenter.addRemoveCategoryId(id, isChecked)
        }
        with(binding.categoriesRecyclerView) {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setClickListeners() {
        binding.continueBtn.setOnClickListener {
            if(args.fromProfile.not())
                presenter.saveCategories(categoryAdapter.loadSegments2Checked().also{Log.d("MyDebug", "segments checked = $it")})
            else{
                val luxBrands = if(categoryAdapter.loadSegments2Checked()[0])
                    allBrands.filter { it.idCategory == 1L }.map { it.id }
                else
                    emptyList()

                val middleBrands = if(categoryAdapter.loadSegments2Checked()[1])
                    allBrands.filter { it.idCategory == 2L }.map { it.id }
                else
                    emptyList()

                val massBrands = if(categoryAdapter.loadSegments2Checked()[2])
                    allBrands.filter { it.idCategory == 3L }.map { it.id }
                else
                    emptyList()

                val ecoBrands = if(categoryAdapter.loadSegments2Checked()[3])
                    allBrands.filter { it.idCategory == 4L }.map { it.id }
                else
                    emptyList()

                presenter.saveOnBoardingData(luxBrands + middleBrands + massBrands + ecoBrands,
                onBoardingData?.data?.property?.split(";")?.mapNotNull { it.toLongOrNull() }.orEmpty())
            }
        }
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun getCategories() {
        presenter.getCategories()
    }

    private fun showLoading(loading: Boolean){
        binding.continueBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }

    private fun getOnBoardingData(){
        if(args.fromProfile)
        presenter.loadOnboardingData()
    }

    private fun showChosenBrands(allBrands: List<ProductBrand>){
        showLoading(true)
        var luxSegment = false
        var middleSegment = false
        var massMarketSegment = false
        var ecoSegment = false
        Log.d("mylog", onBoardingData?.data?.brand.orEmpty())
        val brandIds = onBoardingData?.data?.brand?.split(";")?.mapNotNull { it.toLongOrNull() }
        brandIds?.forEach { brandId ->
            val brand = allBrands.find { it.id == brandId }
            when(brand?.idCategory){
                1L -> luxSegment = true
                2L -> middleSegment = true
                3L -> massMarketSegment = true
                4L -> ecoSegment = true
            }

        }
        categoryAdapter.updateSegmentsChecked(mutableListOf(luxSegment, middleSegment, massMarketSegment, ecoSegment))
        showLoading(false)
    }

    override fun success() {
        showLoading(false)
        if(args.fromProfile.not())
        findNavController().navigate(
            Onboarding3FragmentDirections.actionOnboarding3FragmentToOnboarding4Fragment(
                args.isMale
            )
        )
        else
            findNavController().popBackStack()

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading() {
        showLoading(true)
    }

    override fun loaded(result: List<Any>) {
       if(args.fromProfile.not())
           showLoading(false)
        when(result[0]) {
            is Category -> {
                val categories = result as List<Category>
                categoryAdapter.updateList(categories)
            }
            is BrandsPropertiesDataWrapper -> {
                onBoardingData = result.last() as BrandsPropertiesDataWrapper
                presenter.getAllBrands()
                binding.continueBtn.text = "Сохранить"

            }
            is ProductBrand -> {
                showChosenBrands(result as List<ProductBrand>)
                allBrands = result
            }
        }
    }
}