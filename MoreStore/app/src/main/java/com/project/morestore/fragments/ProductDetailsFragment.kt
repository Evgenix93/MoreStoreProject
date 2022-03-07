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
    private var currentUserId = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        loadYouMayLikeProducts()
        bind(args.product)
        setClickListeners()
        getProductWishList()
        hideBottomNav()
        showDialog()
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
            findNavController().navigate(
                R.id.action_productDetailsFragment_to_chatFragment,
                bundleOf(
                    ChatFragment.USER_ID_KEY to product?.user?.id,
                    ChatFragment.PRODUCT_ID_KEY to product?.id,
                    Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                )
            )
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

    private fun bind(product: Product?) {
        product ?: return
        this.product = product
        initShare(product.id)
        initViewPager(product.photo)
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
            when(index) {
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

        binding.productCityTextView.text = product.address.fullAddress
        binding.productBrandTextView.text =
            if (product.brand == null) "Другое" else product.brand.name
        binding.productSizeTextView.text =
            product.property?.find { Range.create(1, 9).contains(it.id.toInt()) }?.value
        binding.sizeChar.isVisible = false



        binding.productPriceTextView.text =
            "${product.price - ((product.price / 100) * product.sale)} ₽"
        val crossedStr = "${product.price} ₽".toSpannable().apply {
            setSpan(
                StrikethroughSpan(), 0, length, 0
            )
        }
        binding.productOldPriceTextView.text = crossedStr

        binding.productNameTextView.text = product.name
        binding.likesCountTextView.text = product.statistic?.wishlist?.total.toString()
        //binding.shareCountTextView.text = product.statistic.share.total.toString()
        binding.discountTextView.text = "-${product.sale}%"
        binding.sellerNameTextView.text = product.user?.name
        binding.productDescriptionTextView.text = product.about
        binding.productNumberTextView.text = product.id.toString()
        // binding.productCityTextView.text = product.address.fullCity.name
        val calendar = Calendar.getInstance()
            .apply { timeInMillis = System.currentTimeMillis() - product.date * 1000 }
        binding.productUpLoadDateTextView.text =
            "${(System.currentTimeMillis() / 1000 - product.date) / 86400} дня назад"
        Glide.with(this)
            .load(product.user?.avatar?.photo)
            .into(binding.avatarImageView)

        binding.userClickableView.setOnClickListener {
            findNavController().navigate(
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToSellerProfileFragment(
                    product.user!!
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


    }

    private fun initViewPager(photoList: List<ProductPhoto>) {
        val photoAdapter = PhotoViewPagerAdapter(this)
        photoAdapter.updateList(photoList)
        binding.productPhotoViewPager.adapter = photoAdapter
        binding.viewPagerDots.setViewPager2(binding.productPhotoViewPager)

        //TabLayoutMediator(binding.viewPagerDots, binding.productPhotoViewPager) { tab, position ->
        ////Some implementation
        //}.attach()


    }

    private fun initToolBar() {
        binding.toolbar.titleTextView.text = args.product?.name
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_cart)
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }


    private fun initList() {
        productAdapter = ProductAdapter(null) {}
        with(binding.productList) {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }

    }

    private fun loadYouMayLikeProducts() {
        presenter.getYouMayLikeProducts()
    }

    private fun startIntent(intent: Intent) {
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }

    }

    private fun getCurrentUser() {
        presenter.getUserData()
    }

    private fun initViews() {
        val crossedStr = binding.productOldPriceTextView.text.toSpannable().apply {
            setSpan(
                StrikethroughSpan(), 0, binding.productOldPriceTextView.text.length, 0
            )
        }
        binding.productOldPriceTextView.text = crossedStr
    }

    private fun setSellerProduct() {
        binding.chatBtn.isVisible = false
        binding.toolbar.actionIcon.setOnClickListener {
            findNavController().navigate(
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToCreateProductStep6Fragment(
                    product = args.product ?: product
                )
            )
        }
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_edit)
    }


    private fun showDialog() {
        val isNew =
            findNavController().previousBackStackEntry?.destination?.id == R.id.createProductStep6Fragment
        if (isNew)
            findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToProductIsCreatedDialog())
    }


    override fun loaded(result: Any) {
        when (result) {
            is List<*> -> {
                if (result.isNotEmpty() && result[0] is Long) {
                    messageLike(result as List<Long>)
                    return
                }
                if (!suggestedProductsLoaded) {
                    productAdapter.updateList(result as List<Product>)
                    suggestedProductsLoaded = true
                }
                if (!wishListLoaded) {
                    getProductWishList()
                    wishListLoaded = true
                }
                showLikeInfo(result as List<Product>)
            }
            is Intent -> startIntent(result)
            is Product -> {
                if (result.user?.id == currentUserId)
                    setSellerProduct()
                bind(result)
                product = result
            }
            is User -> {
                Log.d("MyDebug", "id = $result")
                currentUserId = result.id
                if (args.product?.user?.id == result.id)
                    setSellerProduct()
                bind(args.product)
                getProduct(args.productId?.toLong())
            }
        }

    }

    override fun loading() {

    }

    override fun error(message: String) {
        //Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun success() {
        findNavController().navigate(R.id.catalogFragment)
    }
}