package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.OptionsAdapter
import com.project.morestore.databinding.FragmentAddProductDetailsBinding
import com.project.morestore.models.CreateProductData
import com.project.morestore.models.User
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductStep6Fragment : MvpAppCompatFragment(R.layout.fragment_add_product_details),
    MainMvpView {
    private val binding: FragmentAddProductDetailsBinding by viewBinding()
    private var optionsAdapter: OptionsAdapter by autoCleared()
    private val args: CreateProductStep6FragmentArgs by navArgs()
    private val presenter: MainPresenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOptionsRecyclerView()
        setClickListeners()
        initToolbar()
        initChips()
        initCreateProductButton()
        loadCreateProductData()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearCreateProductData()
    }

    private fun initOptionsRecyclerView() {
        optionsAdapter = OptionsAdapter(requireContext()) { position ->

            when (position) {
                0 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductAddPhotoFragment())
                1 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductConditionFragment())
                2 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductPriceFragment())
                3 -> {
                    findNavController().navigate(if (args.category.name == "Обувь") CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesShoosFragment(args.forWho) else CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesClothFragment(args.forWho, args.category.id))
                }
                4 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductDescriptionFragment())
                5 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductColorsFragment())
                6 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductMaterialsFragment())
                7 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductRegionsFragment())
            }

        }
        binding.listRecyclerView.adapter = optionsAdapter
        binding.listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setClickListeners() {
        binding.addCardButton.setOnClickListener {
            findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToAddCardFragment())
        }
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductHowToSellFragment())
        }
        binding.placeProductButton.setOnClickListener {
            getUserData()
        }
    }

    private fun getUserData() {
        presenter.getUserData()
    }

    private fun createProduct() {
        presenter.createProduct()
    }

    private fun updateCreateProductData(phone: String) {
        presenter.updateCreateProductData(
            forWho = args.forWho,
            idCategory = args.category.id,
            idBrand = args.brand.id,
            phone = phone
        )
    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Шаг 6 из 6"
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }

    private fun loadCreateProductData(){
        presenter.loadCreateProductData()
    }

    private fun initChips(){
        binding.forWhoChip.text = when(args.forWho){
            0 -> "Для нее"
            1 -> "Для него"
            2 -> "Детям"
            else -> ""
        }
        binding.categoryChip.text = args.category.name
        binding.brandChip.text = args.brand.name
    }

    private fun clearCreateProductData(){
        presenter.clearCreateProductData()
    }

    private fun initCreateProductButton(){
        val options = optionsAdapter.getList()
        if (options.all {it.isChecked}) {
            binding.addPhotoInfoTextView.text = "Отлично! Всё заполнено"
            binding.placeProductButton.isEnabled = true
            binding.placeProductButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
        }else{
            binding.addPhotoInfoTextView.text = "Добавьте фото и описание"
            binding.placeProductButton.isEnabled = false
            binding.placeProductButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }
    }

    override fun loaded(result: Any) {
        if(result is User) {
            updateCreateProductData(result.phone!!)
            createProduct()
        }else
          optionsAdapter.updateList(result as CreateProductData)
    }

    override fun loading() {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showOnBoarding() {
        TODO("Not yet implemented")
    }

    override fun loadedSuggestions(list: List<String>) {
        TODO("Not yet implemented")
    }

    override fun success() {
        TODO("Not yet implemented")
    }

}