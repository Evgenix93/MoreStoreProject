package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.OptionsAdapter
import com.project.morestore.databinding.FragmentAddProductDetailsBinding
import com.project.morestore.dialogs.ArchiveProductDialog
import com.project.morestore.dialogs.DeleteProductDialog
import com.project.morestore.models.*

import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File

class CreateProductStep6Fragment : MvpAppCompatFragment(R.layout.fragment_add_product_details),
    MainMvpView {
    private val binding: FragmentAddProductDetailsBinding by viewBinding()
    private var optionsAdapter: OptionsAdapter by autoCleared()
    private val args: CreateProductStep6FragmentArgs by navArgs()
    private val presenter: MainPresenter by moxyPresenter { MainPresenter(requireContext()) }
    private var firstLaunch = true
    private var isChangingProduct = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("MyDebug", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        initOptionsRecyclerView()
        setClickListeners()
        initToolbar()
        initChips()
        initDeleteArchiveButtons()
        loadCreateProductData()
        if(args.product != null)
            binding.placeProductButton.text = "Сохранить изменения"
    }





    private fun initOptionsRecyclerView() {
        optionsAdapter = OptionsAdapter(requireContext()) { position ->

            when (position) {
                0 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductAddPhotoFragment(args.category?.name == "Обувь"))
                1 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductConditionFragment())
                2 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductPriceFragment())
                3 -> {
                    if(args.product == null)
                        findNavController().navigate(if (args.category!!.name == "Обувь") CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesShoosFragment(args.forWho) else CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesClothFragment(args.forWho, args.category!!.id))
                    else {
                        val forWho = when(args.product!!.property?.first{it.id == 14L}?.value){
                            "Женщинам" -> 0
                            "Мужчинам" -> 1
                            "Детям" -> 2
                            else -> null
                        }
                        findNavController().navigate(
                            if (args.product!!.category?.name == "Обувь") CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesShoosFragment(forWho!!) else CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesClothFragment(
                                forWho!!,
                                args.product!!.category?.id ?: 0
                            )
                        )
                    }
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
            if(args.product == null)
            getUserData()
            else
                changeProduct()
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
            idCategory = args.category?.id,
            idBrand = args.brand?.id,
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
        if(firstLaunch)
        if(args.product == null) {
            presenter.loadCreateProductData()
            presenter.loadCreateProductPhotosVideos()
            firstLaunch = false
        }
        else {
            binding.placeProductButton.text = "Сохранить изменения"

           val property = args.product!!.property?.map{ Property2(it.idProperty!!, it.id) }?.toMutableList()
            presenter.updateCreateProductData(
                idCategory = args.product!!.category?.id,
                //idBrand = args.product!!.brand.toString().split(" ")[0].removePrefix("{id=").removeSuffix(",").toFloat().toLong(),
                idBrand = args.product!!.brand!!.id,
                address = args.product!!.address.fullAddress,
                price = args.product!!.price.toString(),
                sale = args.product!!.sale,
                about = args.product!!.about,
                phone = args.product!!.phone,
                extProperties = property,
                id = args.product!!.id
            )
           presenter.loadCreateProductData()
            val map = mutableMapOf<Int, File>()
            map[1] = File("")
            optionsAdapter.updatePhotoInfo(map)
            firstLaunch = false
        }else {
            presenter.loadCreateProductData()
            presenter.loadCreateProductPhotosVideos()
        }
    }

    private fun initChips(){
        binding.forWhoChip.text = when(args.forWho){
            0 -> "Для нее"
            1 -> "Для него"
            2 -> "Детям"
            else -> ""
        }
        if(args.category == null)
            binding.categoryChip.text = args.product!!.category?.name
        else
           binding.categoryChip.text = args.category!!.name
        if(args.brand == null)
            binding.brandChip.isVisible = false
        else
            binding.brandChip.text = args.brand!!.name
    }

    private fun clearCreateProductData(){
        presenter.clearCreateProductData()
    }

    private fun initCreateProductButton(){
        val options = optionsAdapter.getList().toMutableList()
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

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }




    private fun initDeleteArchiveButtons(){
        if(args.product != null){
            binding.deleteButton.isVisible = true
            binding.archiveButton.isVisible = true
        }
        binding.deleteButton.setOnClickListener{
            DeleteProductDialog().show(childFragmentManager, null)
            // findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductStep6FragmentToDeleteProductDialog())
        }
        binding.archiveButton.setOnClickListener{
            ArchiveProductDialog().show(childFragmentManager, null)
        }
    }

     fun changeProductStatus(status: Int){
      presenter.changeProductStatus(args.product!!.id, status)
    }

    private fun changeProduct(){
        presenter.changeProduct()
    }


    override fun loaded(result: Any) {
        showLoading(false)
        if(result is User) {
            updateCreateProductData(result.phone!!)
            createProduct()
        }else if(result is CreateProductData) {
            optionsAdapter.updateList(result)
            initCreateProductButton()
        }
        else if(result is Product) {
            clearCreateProductData()
            findNavController().navigate(
                CreateProductStep6FragmentDirections.actionCreateProductStep6FragmentToProductDetailsFragment(
                    result,
                    null,
                    true
                )
            )
        }
        else if(result is MutableMap<*,*>) {
            val map = mutableMapOf<Int, File>()
            map[0] = File("")
            optionsAdapter.updatePhotoInfo(if(args.product != null) map else result as MutableMap<Int, File>)
            initCreateProductButton()
        }
        else if(result is String){
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
            findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductStep6FragmentToCabinetFragment())
        }
    }





    override fun loading() {
        showLoading(true)

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showOnBoarding() {
        TODO("Not yet implemented")
    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {
        TODO("Not yet implemented")
    }

    override fun success() {

    }

}