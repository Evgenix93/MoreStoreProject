package com.project.morestore.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Log
import android.util.Range
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.PhotoViewPagerAdapter
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentProductBinding
import com.project.morestore.models.*
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.singletones.Token
import com.project.morestore.util.autoCleared
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*

class ProductDetailsFragment : MvpAppCompatFragment(R.layout.fragment_product), MainMvpView {
    private val binding: FragmentProductBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()
    private val args: ProductDetailsFragmentArgs by navArgs()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var suggestedProductsLoaded = false
    private var wishListLoaded = false
    private var isLiked = false
    private var product: Product? = null
    private var userId: Long = 0
    private var dialogs: List<DialogWrapper>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        setClickListeners()
        hideBottomNav()
        showDialog()
        //getCurrentUser()
        bind(args.product, userId, null)
        getProduct(args.productId?.toLong())
        if(args.product != null)
            getCurrentUser()

    }

    private fun brandClick(brand: ProductBrand) {
        binding.chosenBrandTextView.setOnClickListener { presenter.updateBrand(brand) }
    }

    private fun hideBottomNav() {
        (activity as MainActivity).showBottomNavBar(false)
    }

    private fun showLikeInfo(products: List<Product>) {
        val id = args.product?.id ?: args.productId

        isLiked = products.any { it.id == id }

        binding.heartIcon.setImageResource(if (isLiked) R.drawable.ic_wished else R.drawable.ic_heart)

    }

    private fun messageLike(ids: List<Long>) {
        if (ids.contains(args.product?.id ?: args.productId)) {
            isLiked = !isLiked
        }
        binding.heartIcon.setImageResource(if (isLiked) R.drawable.ic_wished else R.drawable.ic_heart)
        val messageStr = if (isLiked) "Добавлено в избранное" else "Удалено из избранного"
        Toast.makeText(requireContext(), messageStr, Toast.LENGTH_SHORT).show()
    }


    private fun setClickListeners() {
        binding.heartIcon.setOnClickListener {
            presenter.addProductToWishList(
                (args.product?.id ?: args.productId?.toLong()) ?: 0.toLong()
            )
        }

        binding.chatBtn.setOnClickListener {
            if(Token.token.isNotEmpty())
            findNavController().navigate(
                R.id.action_productDetailsFragment_to_chatFragment,
                bundleOf(
                    ChatFragment.USER_ID_KEY to product?.user?.id,
                    ChatFragment.PRODUCT_ID_KEY to product?.id,
                    Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                )
            )
            else
                findNavController().navigate(R.id.cabinetGuestFragment)
        }

        binding.openChatsButton.setOnClickListener{
            findNavController().navigate(R.id.chatLotsFragment,
            Bundle().apply {
                putLong(LotChatsFragment.PRODUCT_ID_KEY, product!!.id)
                putString(LotChatsFragment.PRODUCT_NAME, product!!.name)
                putFloat(LotChatsFragment.PRODUCT_PRICE_KEY, product!!.price)
                putString(LotChatsFragment.PRODUCT_IMAGE_KEY, product!!.photo[0].photo)
            })
        }
    }

    private fun getProductWishList() {
        presenter.getProductWishList()
    }


    private fun getProduct(id: Long?) {
        id ?: return
        presenter.getProducts(productId = id, isFiltered = false, productCategories = null)
    }

    private fun initShare(id: Long) {
        binding.shareIcon.setOnClickListener {
            presenter.shareProduct(id)
        }
    }

    private fun bind(product: Product?, userId: Long, dialogWrappers: List<DialogWrapper>?) {
        product ?: return
        this.product = product
        initShare(product.id)
        val photoVideoFilesUris = product.photo.map { it.photo } + product.video?.map { it.video }.orEmpty()
        initViewPager(photoVideoFilesUris)
        binding.toolbar.titleTextView.text = product.name
        binding.chosenBrandTextView.text =
            if (product.brand == null) "Другое" else product.brand.name

        binding.productConditionTextView.text =
            product.property?.find { it.name == "Состояние" }?.value
        binding.sizeTextView.text =
            product.property?.find { Range.create(1, 9).contains(it.id.toInt()) }?.value.orEmpty()
        Log.d("product", product.property.toString())
        val colors = product.property?.filter { it.name == "Цвет" }

        colors?.forEachIndexed { index, property ->
            val colorValue = property.ico
            when (index) {
                0 -> {
                    binding.productColorTextView.text = property.value
                    if (colorValue == null)
                        binding.colorCircle.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.color2, null)
                    else
                        binding.colorCircle.background.setTint(Color.parseColor(colorValue))
                }

                1 -> {
                    binding.productColor2TextView.text = property.value
                    binding.productColor2TextView.isVisible = true
                    binding.colorCircle2.isVisible = true
                    if (colorValue == null)
                        binding.colorCircle2.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.color2, null)
                    else
                        binding.colorCircle2.background.setTint(Color.parseColor(colorValue))
                }

                2 -> {
                    binding.productColor3TextView.text = property.value
                    binding.productColor3TextView.isVisible = true
                    binding.colorCircle3.isVisible = true
                    if (colorValue == null)
                        binding.colorCircle3.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.color2, null)
                    else
                        binding.colorCircle3.background.setTint(Color.parseColor(colorValue))
                }

                3 -> {
                    binding.productColor4TextView.text = property.value
                    binding.productColor4TextView.isVisible = true
                    binding.colorCircle4.isVisible = true
                    if (colorValue == null)
                        binding.colorCircle4.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.color2, null)
                    else
                        binding.colorCircle4.background.setTint(Color.parseColor(colorValue))
                }
            }
        }

        binding.productCityTextView.text = product.address?.fullAddress
        binding.productBrandTextView.text =
            if (product.brand == null) "Другое" else product.brand.name
        binding.productSizeTextView.text =
            product.property?.find { Range.create(1, 9).contains(it.id.toInt()) }?.value
        binding.sizeChar.isVisible = false



        binding.productPriceTextView.text =
            "${product.priceNew} ₽"
        val crossedStr = "${product.price} ₽".toSpannable().apply {
            setSpan(
                StrikethroughSpan(), 0, length, 0
            )
        }
        binding.productOldPriceTextView.text = crossedStr

        binding.productNameTextView.text = product.name
        binding.likesCountTextView.text = product.statistic?.wishlist?.total.toString()
        binding.discountTextView.text = "-${product.sale}%"
        binding.sellerNameTextView.text = product.user?.name
        binding.productDescriptionTextView.text = product.about
        binding.productNumberTextView.text = product.id.toString()
        binding.productUpLoadDateTextView.text =
            "${(System.currentTimeMillis() / 1000 - product.date) / 86400} дня назад"
        Glide.with(this)
            .load(product.user?.avatar?.photo.toString())
            .into(binding.avatarImageView)

        binding.userClickableView.setOnClickListener {
            findNavController().navigate(
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToSellerProfileFragment(
                    product.user!!,
                    false
                )
            )
        }
        binding.chosenBrandTextView.setOnClickListener {
            if (product.brand == null)
                return@setOnClickListener
            else brandClick(product.brand)
        }
        val listener =
            MaskedTextChangedListener("+7([000]) [000]-[00]-[00]", binding.sellerPhoneTextView)
        binding.sellerPhoneTextView.addTextChangedListener(listener)
        binding.sellerPhoneTextView.setText(product.phoneShow)
        if (product.user?.id == userId)
            setSellerProduct(dialogWrappers, product)
    }


    private fun initViewPager(filesUriList: List<String>) {
        val photoAdapter = PhotoViewPagerAdapter(this) { fileUri ->
            Log.d("mylog", fileUri)
            if(fileUri.contains("mp4"))
                presenter.playVideo(fileUri = fileUri.toUri())

        }
        photoAdapter.updateList(filesUriList)
        binding.productPhotoViewPager.adapter = photoAdapter
        binding.viewPagerDots.setViewPager2(binding.productPhotoViewPager)

        //TabLayoutMediator(binding.viewPagerDots, binding.productPhotoViewPager) { tab, position ->
        ////Some implementation
        //}.attach()


    }

    private fun initToolBar() {
        binding.toolbar.titleTextView.text = args.product?.name
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_cart)
        binding.toolbar.backIcon.setOnClickListener {
            if(findNavController().previousBackStackEntry?.destination?.id == R.id.createProductStep6Fragment)
                findNavController().navigate(R.id.catalogFragment)
            else
                findNavController().popBackStack()
        }
    }


    private fun initList() {
        productAdapter = ProductAdapter(null) {}
        with(binding.productList) {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }

    }

    private fun loadYouMayLikeProducts(category: Category?) {
        Log.d("MyDebug", "category = ${category?.name}")
        presenter.getYouMayLikeProducts(category)
    }

    private fun startIntent(intent: Intent) {
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }

    }

    private fun getCurrentUser() {
        presenter.getUserId()
    }

    private fun initViews() {
        val crossedStr = binding.productOldPriceTextView.text.toSpannable().apply {
            setSpan(
                StrikethroughSpan(), 0, binding.productOldPriceTextView.text.length, 0
            )
        }
        binding.productOldPriceTextView.text = crossedStr
    }

    private fun setSellerProduct(dialogWrappers: List<DialogWrapper>?, product: Product) {
        binding.chatBtn.isVisible = false
        binding.toolbar.actionIcon.setOnClickListener {
            findNavController().navigate(
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToCreateProductStep6Fragment(
                    product = args.product ?: product
                )
            )
        }
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_edit)
        binding.firstPurchaseInfoCard.isVisible = false
        binding.addToCartBtn.isVisible = false
        binding.avatarImageView.isVisible = false
        binding.sellerNameTextView.isVisible = false
        binding.sellerPhoneTextView.isVisible = false
        binding.starIcon.isVisible = false
        binding.ratingTextView.isVisible = false
        binding.userClickableView.isVisible = false
        binding.textView17.isVisible = false
        binding.productList.isVisible = false
         if(dialogWrappers != null) {
             if (dialogWrappers.none { it.product?.id == product.id })
                 binding.promoteInfoCard.isVisible = true
             else {
                 binding.buyersCard.isVisible = true
                 binding.buyersCount.text =
                     dialogWrappers.filter { it.product?.id == product.id }.size.toString()
             }
         }
    }


    private fun showDialog() {
        val isNew =
            findNavController().previousBackStackEntry?.destination?.id == R.id.createProductStep6Fragment
        if (isNew)
            findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToProductIsCreatedDialog())
    }

    private fun getDialogs(){
        presenter.getDialogs()
    }

    override fun loaded(result: Any) {
        when (result) {
            is List<*> -> {
                //if (result.isNotEmpty() && result[0] is Long) {
                   // messageLike(result as List<Long>)
                if(result[0] is Product) {
                    productAdapter.updateList(result as List<Product>)
                }
                else{
                    loadYouMayLikeProducts(null)
                   bind(product, userId, result as List<DialogWrapper>)
                    dialogs = result
                }
                //}
                /*if (!suggestedProductsLoaded) {
                    productAdapter.updateList(result as List<Product>)
                    suggestedProductsLoaded = true
                }
                if (!wishListLoaded) {
                    getProductWishList()
                    wishListLoaded = true
                }
                showLikeInfo(result as List<Product>)*/
            }
            is Product -> {
                bind(result, userId, dialogs)
                getCurrentUser()
            }

            is Intent -> {Log.d("MyDebug", "startIntent")
                startActivity(result)
            }

            is Long -> {
                userId = result
                Log.d("MyDebug", "userId = $result")
                if(result == 0) {
                    loadYouMayLikeProducts(args.product?.category)
                    bind(product, userId, null)
                }
                else
                    getDialogs()
            }
        }
    }

    override fun loading() {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun success() {
        findNavController().navigate(R.id.catalogFragment)
    }
}