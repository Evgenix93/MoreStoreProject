package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.OptionsAdapter
import com.project.morestore.databinding.FragmentAddProductDetailsBinding
import com.project.morestore.presentation.dialogs.ArchiveProductDialog
import com.project.morestore.presentation.dialogs.DeleteProductDialog
import com.project.morestore.presentation.dialogs.SaveProductDialog
import com.project.morestore.data.models.*
import com.project.morestore.domain.presenters.CreateProductPresenter
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductStep6Fragment : MvpAppCompatFragment(R.layout.fragment_add_product_details),
    CreateProductMvpView {
    private val binding: FragmentAddProductDetailsBinding by viewBinding()
    private var optionsAdapter: OptionsAdapter by autoCleared()
    private val args: CreateProductStep6FragmentArgs by navArgs()
    @Inject
    lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }
    private var firstLaunch = true
    private var isSavingDraftProduct = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOptionsRecyclerView()
        setClickListeners()
        initToolbar()
        initChips()
        initDeleteArchiveButtons()
        loadCreateProductData()
        if (args.product != null)
            binding.placeProductButton.text = getString(R.string.save_changes)
        getUserData()
        getActiveCard()
    }


    private fun initOptionsRecyclerView() {
        optionsAdapter = OptionsAdapter(requireContext()) { position ->

            when (position) {
                0 -> findNavController().navigate(
                    CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductAddPhotoFragment(
                        args.category?.name == "Обувь",
                        args.product
                    )
                )
                1 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductConditionFragment())

                2 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductPriceFragment())
                3 -> {
                    if (args.product == null)
                        findNavController().navigate(
                            if (args.category!!.name == "Обувь") CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesShoosFragment(
                                args.forWho
                            ) else CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesClothFragment(
                                args.forWho,
                                args.category!!.id
                            )
                        )
                    else {
                        val forWho = when (args.product!!.property?.first { it.id == 14L }?.value) {
                            "Женщинам" -> 0
                            "Мужчинам" -> 1
                            "Детям" -> 2
                            else -> null
                        }
                        findNavController().navigate(
                            if (args.product!!.category?.name == "Обувь") CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesShoosFragment(
                                forWho!!
                            ) else CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductSizesClothFragment(
                                forWho!!,
                                args.product!!.category?.id ?: 0
                            )
                        )
                    }
                }
                4 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductDescriptionFragment())
                5 -> {


                    setFragmentResultListener(MyAddressesFragment.ADDRESS_REQUEST){_, bundle ->
                        val address = bundle.getParcelable<MyAddress>(MyAddressesFragment.ADDRESS_KEY)
                        Log.d("mylog", "address $address")
                        val cityStr = address?.address?.city
                        val streetStr = "ул. ${address?.address?.street}"
                        val houseStr = if(address?.address?.house != null) "дом ${address.address.house}" else null

                        val strings =
                            arrayOf(cityStr, streetStr, houseStr).filterNotNull()
                        val chosenAddressStr = strings.joinToString(", ")

                        presenter.updateCreateProductData(address = chosenAddressStr)
                        loadCreateProductData()


                    }
                    findNavController().navigate(
                        CreateProductStep6FragmentDirections
                        .actionCreateProductStep6FragmentToMyAddressesFragment(true,
                             MyAddressesFragment.ADDRESSES_HOME))
                }
                6 -> {
                    getChosenAddress { address ->
                     presenter.updateCreateProductData(addressCdek = address)
                    loadCreateProductData()}
                }
                7 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductColorsFragment())
                8 -> findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductFragmentToCreateProductMaterialsFragment())
                9 -> findNavController().navigate(R.id.createProductStylesFragment)
                10 -> {findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductStep6FragmentToCreateProductPackageFragment())}
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
            if (args.product == null)

                createProduct()
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

    fun createDraftProduct() {
        isSavingDraftProduct = true
        presenter.createDraftProduct()
    }

    private fun updateCreateProductData(phone: String?) {

        if(args.product == null)
        presenter.updateCreateProductData(
            forWho = args.forWho,
            idCategory = args.category?.id,
            idBrand = args.brand?.id,
            phone = phone
        )

    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Шаг 5 из 5"
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.actionIcon.setOnClickListener {
            if(args.product == null)
            SaveProductDialog { createDraftProduct() }.show(childFragmentManager, null)
            else
                findNavController().popBackStack()
        }

    }

    private fun loadCreateProductData() {
        if (firstLaunch)
            if (args.product == null) {
                presenter.loadCreateProductData()
                presenter.loadCreateProductPhotosVideos()
                firstLaunch = false
            } else {
                binding.placeProductButton.text = getString(R.string.save_changes)

                val property = args.product!!.property?.map { Property2(it.idProperty!!, it.id) }
                    ?.toMutableList()
                presenter.clearCreateProductData()

                presenter.updateCreateProductData(
                    idCategory = args.product!!.category?.id,
                    idBrand = args.product!!.brand?.id,
                    address = args.product!!.address?.fullAddress,
                    addressCdek = args.product!!.addressCdek,
                    price = args.product!!.price.toString(),
                    newPrice = args.product!!.priceNew.toString(),
                    sale = args.product!!.sale,
                    about = args.product!!.about,
                    phone = args.product!!.phone,
                    extProperties = property,
                    id = args.product!!.id,
                    name = args.product!!.name,
                    dimensions = args.product!!.packageDimensions
                )
                presenter.loadCreateProductData()
                val map = mutableMapOf<Int, File>()
                map[1] = File("")
                optionsAdapter.updatePhotoInfo(map)
                if(args.product?.photo?.first()?.photo?.contains("no_photo") == true)
                    optionsAdapter.updatePhotoInfo(emptyMap<Int,File>().toMutableMap())

                firstLaunch = false
            } else {
            presenter.loadCreateProductData()
            presenter.loadCreateProductPhotosVideos()
        }
    }

    private fun initChips() {
        if(args.product == null)
        binding.forWhoChip.text = when (args.forWho) {
            0 -> "Для нее"
            1 -> "Для него"
            2 -> "Детям"
            else -> ""
        }
        else{
            Log.d("mylog", args.product.toString())
            val forWhoProperty = args.product?.property?.find { it.id == 14L }
            when(forWhoProperty?.idProperty){
                140L -> binding.forWhoChip.text = "Для нее"
                141L -> binding.forWhoChip.text = "Для него"
                142L -> binding.forWhoChip.text = "Детям"
            }
        }
        if (args.category == null)
            if(args.product?.category == null)
                binding.categoryChip.isVisible = false
             else
                binding.categoryChip.text = args.product!!.category!!.name
        else
            binding.categoryChip.text = args.category!!.name

        if (args.brand == null)
            if (args.product?.brand == null)
                binding.brandChip.isVisible = false
            else binding.brandChip.text = args.product!!.brand?.name
        else
            binding.brandChip.text = args.brand!!.name

    }

    private fun clearCreateProductData() {
        presenter.clearCreateProductData()
    }

    private fun initCreateProductButton() {
        val options = optionsAdapter.getList().toMutableList()
        if (options.take(6).all { it.isChecked }) {
            binding.addPhotoInfoTextView.text = getString(R.string.excellent_all_filled)
            binding.placeProductButton.isClickable = true
            binding.placeProductButton.backgroundTintList =
                ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
        } else {
            binding.addPhotoInfoTextView.text = getString(R.string.add_photo_and_description)
            binding.placeProductButton.isClickable = false
            binding.placeProductButton.backgroundTintList =
                ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.loader.isVisible = loading
        binding.placeProductButton.isEnabled = !loading
    }

    private fun initDeleteArchiveButtons() {
        if (args.product != null) {
            binding.deleteButton.isVisible = true
            binding.archiveButton.isVisible = true
        }
        binding.deleteButton.setOnClickListener {
            DeleteProductDialog().show(childFragmentManager, null)

        }
        binding.archiveButton.setOnClickListener {
            ArchiveProductDialog().show(childFragmentManager, null)
        }
    }

    fun changeProductStatus(status: Int) {
        presenter.changeProductStatus(args.product!!.id, status)
    }

    private fun changeProduct() {
        presenter.changeProductAndPublish()
    }

    private fun getActiveCard(){
        presenter.getActiveCard()
    }

    private fun getChosenAddress(onAddressReceived: (address: String) -> Unit){

        setFragmentResultListener(MyAddressesFragment.ADDRESS_REQUEST){_, bundle ->
            val address = bundle.getParcelable<MyAddress>(MyAddressesFragment.ADDRESS_KEY)
            Log.d("mylog", "address $address")
           // chosenAddress = address
            val cityStr = address?.address?.city
            val streetStr = address?.address?.street
            val houseStr = if(address?.address?.house != null) "дом.${address.address.house}" else null
            val housingStr = if(address?.address?.housing != null) "кп.${address.address.housing}" else null
            val buildingStr = if(address?.address?.building != null) "стр.${address.address.building}" else null
            val apartmentStr = if(address?.address?.apartment != null) "кв.${address.address.apartment}" else null
            val cdekCode = "cdek code:${address?.cdekCode}"
            val strings =
                arrayOf(cityStr, streetStr, houseStr, housingStr, buildingStr, apartmentStr, cdekCode).filterNotNull()

           val chosenAddressStr = strings.joinToString(", ")
            Log.d("MyTestTag", chosenAddressStr)
            onAddressReceived(chosenAddressStr)
        }

        findNavController().navigate(
            CreateProductStep6FragmentDirections
                .actionCreateProductStep6FragmentToMyAddressesFragment(true,
                    MyAddressesFragment.ADDRESSES_CDEK))
    }


    override fun loaded(result: Any) {
        showLoading(false)
        if (result is User) {
            updateCreateProductData(result.phone)

        } else if (result is CreateProductData) {
            optionsAdapter.updateList(result)
            initCreateProductButton()
        } else if (result is CreatedProductId) {
            clearCreateProductData()
            if (isSavingDraftProduct.not())
                findNavController().navigate(
                    CreateProductStep6FragmentDirections.actionCreateProductStep6FragmentToProductDetailsFragment(
                        null,
                        result.id.toString(),
                        true
                    )
                )
            else findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductStep6FragmentToMainFragment())
        } else if (result is MutableMap<*, *>) {
            val map = mutableMapOf<Int, File>()
            map[0] = File("")
            optionsAdapter.updatePhotoInfo(if (args.product != null) map else result as MutableMap<Int, File>)
            if(args.product?.photo?.first()?.photo?.contains("no_photo") == true && result.isEmpty())
                optionsAdapter.updatePhotoInfo(emptyMap<Int,File>().toMutableMap())
            initCreateProductButton()
        } else if (result is String) {
            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
            findNavController().navigate(CreateProductStep6FragmentDirections.actionCreateProductStep6FragmentToCabinetFragment())
        }else if (result is Card){
            binding.warningImageView.setImageResource(R.drawable.ic_check_round_fill)
            binding.warningImageView.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.green, null))
            binding.cardInfoTitleTextView.text = resources.getString(R.string.card_added)
            binding.warningInfoTextView.text = resources.getString(R.string.card_added_info)
            binding.addCardButton.isVisible = false
            binding.creditCardImageView.isVisible = true
            binding.starsTextView.isVisible = true
            binding.cardLastNumbersTextView.isVisible = true
            binding.cardLastNumbersTextView.text = result.number.takeLast(4)


        }
    }

    override fun loading() {
        showLoading(true)
    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }



}