package com.project.morestore.fragments.product.details

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.style.StrikethroughSpan
import android.util.Log
import android.util.Range
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.PhotoViewPagerAdapter
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentProductBinding
import com.project.morestore.fragments.CabinetGuestFragment
import com.project.morestore.fragments.ChatFragment
import com.project.morestore.fragments.LotChatsFragment
import com.project.morestore.models.*
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.singletones.Token
import com.project.morestore.util.autoCleared
import com.redmadrobot.inputmask.MaskedTextChangedListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ProductDetailsFragment : MvpAppCompatFragment(R.layout.fragment_product), MainMvpView {

    private val binding: FragmentProductBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()
    private val args: ProductDetailsFragmentArgs by navArgs()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var isLiked = false
    private var product: Product? = null
    private var userId: Long = 0
    private var dialogs: List<DialogWrapper>? = null
    private var currentState: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        initViews()
        setClickListeners()
        hideBottomNav()
        showDialog()
        bind(args.product, userId, null)
        getProduct(args.productId?.toLong())
        if (args.product != null)
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

    private fun messageLike() {
        /*if (ids.contains(args.product?.id ?: args.productId)) {
            isLiked = !isLiked
        }
        binding.heartIcon.setImageResource(if (isLiked) R.drawable.ic_wished else R.drawable.ic_heart)*/
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
            if (Token.token.isNotEmpty())
                findNavController().navigate(
                    R.id.action_productDetailsFragment_to_chatFragment,
                    bundleOf(
                        ChatFragment.USER_ID_KEY to product?.user?.id,
                        ChatFragment.PRODUCT_ID_KEY to product?.id,
                        Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                    )
                )
            else {
                findNavController().navigate(R.id.cabinetGuestFragment, bundleOf(CabinetGuestFragment.FRAGMENT_ID to R.id.productDetailsFragment))
            }
        }

        binding.openChatsButton.setOnClickListener {
            findNavController().navigate(R.id.chatLotsFragment,
                Bundle().apply {
                    putLong(LotChatsFragment.PRODUCT_ID_KEY, product!!.id)
                    putString(LotChatsFragment.PRODUCT_NAME, product!!.name)
                    putFloat(LotChatsFragment.PRODUCT_PRICE_KEY, product!!.price)
                    putString(LotChatsFragment.PRODUCT_IMAGE_KEY, product!!.photo[0].photo)
                })
        }

        binding.raiseProductButton.setOnClickListener {

            findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToRaiseProductFragment())
        }

        binding.addToCartBtn.setOnClickListener {
            if (product != null) {
                if (!currentState) {
                    presenter.addProductToCart(productId = product!!.id, userId)
                }else{
                    presenter.removeProductFromCart(productId = product!!.id, userId)
                }
            }
        }
    }

    private fun getProductWishList() {
        presenter.getProductWishList()
    }

    private fun getProduct(id: Long?) {
        id ?: return
        presenter.getProducts(productId = id, isFiltered = false, productCategories = null, status = null)
    }

    private fun initShare(id: Long) {
        binding.shareIcon.setOnClickListener {
            presenter.shareProduct(id)
        }
    }

    private fun bind(product: Product?, userId: Long, dialogWrappers: List<DialogWrapper>?) {
        product ?: return
        isLiked = product.wishlist ?: false
        this.product = product
        initShare(product.id)
        val photoVideoFilesUris =
            product.photo.map { it.photo } + product.video?.map { it.video }.orEmpty()
        initViewPager(photoVideoFilesUris, product.status == 8)
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

        binding.productCityTextView.text = product.address?.fullAddress ?: "-"
        binding.productBrandTextView.text =
            if (product.brand == null || product.brand.status == 1) "Другое" else product.brand.name
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
        binding.productDescriptionTextView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(product.about, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(product.about)
        }
        binding.productNumberTextView.text = product.id.toString()
        binding.productUpLoadDateTextView.text =
            "${(System.currentTimeMillis() / 1000 - product.date) / 86400} дня назад"
        Glide.with(this)
            .load(product.user?.avatar?.photo.toString())
            .into(binding.avatarImageView)

        binding.userClickableView.setOnClickListener {
            findNavController().navigate(
                ProductDetailsFragmentDirections.actionProductDetailsFragmentToSellerProfileFragment(
                   user = product.user!!,
                   toReviews = false
                )
            )
        }
        binding.chosenBrandTextView.setOnClickListener {
            if (product.brand == null)
                return@setOnClickListener
            else brandClick(product.brand)
        }

        binding.sellerPhoneTextView.setText(product.phoneShow)
        if (product.user?.id == userId)
            setSellerProduct(dialogWrappers, product)

        binding.heartIcon.setImageResource(if(product.wishlist == true) R.drawable.ic_heart_red
        else R.drawable.ic_heart)
        if(product.wishlist == true)
            binding.heartIcon.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.red, null))
        else binding.heartIcon.imageTintList = null
       when(product.status){
           0 -> {
             if(product.commentModeration != null || product.property?.any{
                   it.comment != null
                 } == true){
               binding.moderationBackgroundView.isVisible = true
               binding.moderationTextView.isVisible = true
               binding.moderationBackgroundView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.gray2, null))
               binding.moderationTextView.text = "Отклонено модератором"
                if(product.commentModeration != null) {
                    binding.error2ImageView.isVisible = true
                    binding.error2TextView.isVisible = true
                    binding.error2TextView.text = product.commentModeration
                }
               product.property?.filter{it.comment != null}?.forEach{ property ->
                   property.id.let{
                       when{
                           it < 10 -> {
                               binding.sizeErrorImageView.isVisible = true
                               binding.sizeErrorTextView.isVisible = true
                               binding.sizeErrorTextView.text = property.comment
                           }
                           it == 11L -> {
                               binding.conditionErrorImageView.isVisible = true
                               binding.conditionErrorTextView.isVisible = true
                               binding.conditionErrorTextView.text = property.comment
                           }
                           it == 12L -> {
                               binding.errorImageView.isVisible = true
                               binding.errorTextView.isVisible = true
                               binding.errorTextView.text = property.comment
                           }
                       }
                   }
               }
             }else{
                 binding.moderationBackgroundView.isVisible = true
                 binding.moderationTextView.isVisible = true
             }
           }
           8 -> {
               binding.productSoldCardView.isVisible = true
               binding.addToCartBtn.isVisible = false
               binding.chatBtn.isVisible = false
           }
           7 -> {
               binding.productIsBookedCardView.isVisible = true
               binding.addToCartBtn.isVisible = false
           }
           6 -> {
               binding.dealIsSubmittedCardView.isVisible = true
               binding.addToCartBtn.isVisible = false
           }
       }
       if(product.statusUser?.read == false)
           viewProduct(product.id)

    }


    private fun initViewPager(filesUriList: List<String>, isSold: Boolean) {
        val photoAdapter = PhotoViewPagerAdapter(this, isSold) { fileUri ->
            Log.d("mylog", fileUri)
            if (fileUri.contains("mp4"))
                presenter.playVideo(fileUri = fileUri.toUri())

        }
        photoAdapter.updateList(filesUriList)
        binding.productPhotoViewPager.adapter = photoAdapter
        binding.viewPagerDots.setViewPager2(binding.productPhotoViewPager)

    }

    private fun initToolBar() {
        binding.toolbar.titleTextView.text = args.product?.name
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_cart)
        binding.toolbar.backIcon.setOnClickListener {
            if (findNavController().previousBackStackEntry?.destination?.id == R.id.createProductStep6Fragment)
                findNavController().navigate(R.id.catalogFragment, null,
                NavOptions.Builder().setPopUpTo(R.id.mainFragment, false).build())
            else
                findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.setOnClickListener {
           /* if (product != null) {
                if (!currentState) {
                    presenter.addProductToCart(productId = product!!.id, userId)
                }else{
                    presenter.removeProductFromCart(productId = product!!.id, userId)
                }
            }*/
            findNavController().navigate(R.id.ordersCartFragment)
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
        val listener =
            MaskedTextChangedListener("+7([000]) [000]-[00]-[00]", binding.sellerPhoneTextView)
        binding.sellerPhoneTextView.addTextChangedListener(listener)


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
        if (dialogWrappers != null) {
            if (dialogWrappers.none { it.product?.id == product.id } && product.status != 8)
                binding.promoteInfoCard.isVisible = true
            else if(dialogWrappers.find { it.product?.id == product.id } != null) {
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

    private fun getDialogs() {
        presenter.getDialogs()
    }

    private fun viewProduct(idProduct: Long){
      presenter.viewProduct(idProduct)
    }

    override fun loaded(result: Any) {
        when (result) {
            is List<*> -> {
                if(result.isEmpty()) return
                //if (result.isNotEmpty() && result[0] is Long) {
                // messageLike(result as List<Long>)
                if (result[0] is Product) {
                    productAdapter.updateList(result as List<Product>)
                }
                if(result[0] is DialogWrapper){
                    loadYouMayLikeProducts(null)
                    bind(product, userId, result as List<DialogWrapper>)
                    dialogs = result
                }
                if(result[0] is Long){
                    isLiked = isLiked.not()
                    getProduct(result[0] as Long)
                    messageLike()

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

            is Intent -> {
                Log.d("MyDebug", "startIntent")
                startActivity(result)
            }

            is Long -> {
                userId = result
                if (product != null) {
                    presenter.loadCart(userId, product!!.id) {
                        currentState = it;
                        if (currentState) {
                            binding.addToCartBtn.text = getText(R.string.product_remove_from_cart)
                        }
                    }
                }
                Log.d("MyDebug", "userId = $result")
                if (result == 0) {
                    loadYouMayLikeProducts(args.product?.category)
                    bind(product, userId, null)
                } else
                    getDialogs()
            }
            is String -> {
                currentState = !currentState
                if (currentState) {
                    findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToOrdersCartFragment())
                } else {
                    binding.addToCartBtn.text = getText(R.string.product_add_cart)
                }
            }
            is Boolean -> {
                if(result) {
                    Toast.makeText(requireContext(), "Просмотрено", Toast.LENGTH_SHORT).show()
                    bind(product, userId, dialogs)
                }
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

    override fun loginFailed() {
        TODO("Not yet implemented")
    }

    override fun success() {
        findNavController().navigate(R.id.catalogFragment)
    }

}