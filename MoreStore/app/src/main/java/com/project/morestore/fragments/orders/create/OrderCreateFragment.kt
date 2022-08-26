package com.project.morestore.fragments.orders.create

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Log
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOrderCreateBinding
import com.project.morestore.dialogs.MenuBottomDialogDateFragment
import com.project.morestore.fragments.ChatFragment
import com.project.morestore.fragments.MyAddressesFragment
import com.project.morestore.fragments.RaiseProductFragmentDirections
import com.project.morestore.models.*
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class OrderCreateFragment : MvpAppCompatFragment(R.layout.fragment_order_create), OrderCreateView {
    private val binding: FragmentOrderCreateBinding by viewBinding()

    private val presenter by moxyPresenter { OrderCreatePresenter(requireContext()) }
    private val args: OrderCreateFragmentArgs by navArgs()
    private var chosenAddress: MyAddress? = null
    private var chosenTime: Calendar? = null//Calendar.getInstance()
    private var chosenAddressStr = ""
    private var productPrice: Float? = null
    private var currentDeliveryPrice: Float? = null

    ///////////////////////////////////////////////////////////////////////////
    //                      View
    //////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.setProduct(args.product);
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        setClickListeners()
        initRadioPlaceButtons()
        initRadioDeliveryButtons()
        initRadioDeliveryVariantButtons()
        initViews()
        getSupportDialog()
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    override fun navigate(pageId: Int?) {
        binding.loader.isVisible = false
        when (pageId) {
            null -> {
                if(findNavController().previousBackStackEntry?.destination?.id != R.id.chatFragment)
                findNavController().popBackStack()
                else findNavController().navigate(R.id.ordersCartFragment)
            }
            R.id.ordersActiveFragment -> {
                findNavController().navigate(R.id.ordersActiveFragment)
            }
            R.id.chatFragment -> {
                findNavController().navigate(
                    R.id.chatFragment,
                    bundleOf(
                        ChatFragment.USER_ID_KEY to args.product.user?.id,
                        ChatFragment.PRODUCT_ID_KEY to args.product.id,
                        Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                    )
                )
            }
        }
    }

    override fun setProductInfo(product: Product) {
        productPrice = product.priceNew
        binding.loader.isVisible = false
        Glide.with(this)
                .load(product.user?.avatar?.photo.toString())
                .circleCrop()
                .into(binding.sellerAvatarImageView)
        binding.sellerNameTextView.text = product.user?.name


        Glide.with(this)
                .load(product.photo[0].photo)
                .into(binding.productImageView)

        binding.productNameTextView.text = product.name
        binding.productConditionTextView.text = product.property?.find {
            it.name == "Состояние"
        }?.value

        binding.sizeTextView.text = product.property?.find {
            Range.create(1, 9).contains(it.id.toInt())
        }?.value.orEmpty()

        val crossedStr = "${product.price} ₽".toSpannable().apply {
            setSpan(
                    StrikethroughSpan(), 0, length, 0
            )
        }
        binding.oldPriceTextView.text = crossedStr
        binding.oldPrice2TextView.text = crossedStr
        binding.oldPrice2WithDeliveryTextView.text = crossedStr
        binding.newPriceTextView.text = product.priceNew.toString()
        binding.newPrice2TextView.text = product.priceNew.toString()
        binding.price2WithDeliveryTextView.text = if(product.priceNew != null) product.priceNew.toString()
                                                  else product.price.toString()
        //val finalSum = getFinalSum(product.priceNew?.toInt() ?: product.price.toInt(), 3 )
        //binding.finalSumTextView.text =
            //getFinalSum(productPrice ?: 0f, currentDeliveryPrice ?: 0f).toString() //finalSum.toString()
        binding.finalSumWithoutDelivery.text = ((product.priceNew ?: product.price) + ((product.priceNew ?: product.price) * 0.05f)).toString()

        if(product.addressCdek == null || product.packageDimensions.length == null){
            binding.anotherCityRadioBtn.isEnabled = false
            binding.anotherCityRadioBtn.buttonDrawable?.alpha = 125
            binding.anotherCityRadioBtn.setTextColor(resources.getColor(R.color.gray1, null))
        }

        binding.yandexRadioBtn.isEnabled = false
        binding.yandexRadioBtn.buttonDrawable?.alpha = 125
        binding.yandexRadioBtn.setTextColor(resources.getColor(R.color.gray1, null))
    }

    override fun loading() {
        binding.loader.isVisible = true

    }

    override fun showMessage(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun supportDialogLoaded(chat: Chat) {
        binding.toolbar.actionIcon.setOnClickListener{
            findNavController().navigate(
                R.id.chatFragment,
                bundleOf(Chat::class.java.simpleName to Chat.Support::class.java.simpleName,
                    ChatFragment.DIALOG_ID_KEY to chat.id)
            )
        }
    }

    override fun payForOrder(paymentUrl: PaymentUrl, orderId: Long) {
        binding.loader.isVisible = false
        binding.webView.isVisible = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return if (request?.url.toString().contains("success")) {
                    view?.isVisible = false
                    findNavController().navigate(
                        OrderCreateFragmentDirections
                            .actionCreateOrderFragmentToSuccessOrderPaymentFragment(orderId = orderId))
                    true
                }else if(request?.url.toString().contains("failed")) {
                    findNavController().navigate(R.id.ordersActiveFragment)
                    true
                }else {
                    false
                }
            }

        }
        binding.webView.settings.userAgentString = "Chrome/56.0.0.0 Mobile"
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(paymentUrl.formUrl)
    }

    override fun setDeliveryPrice(price: DeliveryPrice?) {
        binding.loader.isVisible = false
        if(price == null){
            binding.payNowWithDeliveryButton.isEnabled = false
            binding.updateDeliveryPriceBtn.isVisible = true
            binding.updateDeliveryPriceBtn.setOnClickListener {
                if(binding.anotherCityRadioBtn.isChecked) getDeliveryPrice()
            }
        }else {
            binding.payNowWithDeliveryButton.isEnabled = true
            binding.updateDeliveryPriceBtn.isVisible = false
        }
        currentDeliveryPrice = price?.total_sum
        binding.deliveryPriceTextView.text = if(price != null) price.total_sum.toString() else "не удалось загрузить"
        val finalSum = getFinalSum(productPrice ?: 0f, price?.total_sum ?: 0f)
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        binding.finalSumTextView.text = df.format(finalSum).replace(',', '.')

        val calendar = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() + (172800 * 1000) }
        val month = when(calendar.get(Calendar.MONTH)){
            0 -> "Января"
            1 -> "Февраля"
            2 -> "Марта"
            3 -> "Апреля"
            4 -> "Мая"
            5 -> "Июня"
            6 -> "Июля"
            7 -> "Августа"
            8 -> "Сентября"
            9 -> "Октября"
            10 -> "Ноября"
            11 -> "Декабря"
            else -> ""
        }
        binding.deliveryDateTextView.text = "${calendar.get(Calendar.DAY_OF_MONTH)} $month"
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      Private
    ///////////////////////////////////////////////////////////////////////////

    private fun setClickListeners() {
        binding.dateEditText.setOnClickListener {
            MenuBottomDialogDateFragment(/*requireContext(),*/ false, { day, month, year ->
                val currentCalendar = Calendar.getInstance()
                val choiceCalendar = Calendar.getInstance().apply { set(year, month - 1, day) }
                val timeDiff = choiceCalendar.timeInMillis - currentCalendar.timeInMillis
                val currDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
                val currMonth = currentCalendar.get(Calendar.MONTH) + 1
                val currYear = currentCalendar.get(Calendar.YEAR)
                val dayStr = if(day < 10) "0$day" else day.toString()
                val monthStr = if(month < 10) "0$month" else month.toString()
                val yearStr = year.toString().takeLast(2)
                if(timeDiff > 0) {
                    binding.dateEditText.setText("$dayStr.$monthStr.$yearStr")
                    chosenTime?.set(year, month - 1, day)
                    if(chosenTime == null)
                        chosenTime = Calendar.getInstance().apply { set(year, month - 1, day) }

                }
                else{
                    val currDayStr = if(currDay < 10) "0$currDay" else currDay.toString()
                    val currMonthStr = if(currMonth < 10) "0$currMonth" else currMonth.toString()
                    val currYearStr = currYear.toString().takeLast(2)
                    binding.dateEditText.setText("$currDayStr.$currMonthStr.$currYearStr")
                    chosenTime?.set(currYear, currMonth - 1, currDay)

                    if((chosenTime != null) && chosenTime!!.timeInMillis - System.currentTimeMillis() < 3600 * 1000){
                        binding.timeEditText.setText(
                            "${currentCalendar.get(Calendar.HOUR_OF_DAY) + 1}:${
                                currentCalendar.get(
                                    Calendar.MINUTE
                                )
                            }"
                        )
                        chosenTime?.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY) + 1)
                        chosenTime?.set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
                    }
                    if(chosenTime == null)
                        chosenTime = Calendar.getInstance().apply { set(currYear, currMonth - 1, currDay) }
                }


            }, { _, _ ->

            }).show(childFragmentManager, null)
        }

        binding.timeEditText.setOnClickListener {
            MenuBottomDialogDateFragment(/*requireContext(),*/ true, { _, _, _ ->

            }, { hour, minute ->
                val currentCalendar = Calendar.getInstance()
                chosenTime?.set(Calendar.HOUR_OF_DAY, hour)
                chosenTime?.set(Calendar.MINUTE, minute)
                if(chosenTime != null) {
                    val timeDiff = chosenTime!!.timeInMillis - currentCalendar.timeInMillis
                    if(timeDiff > 0)
                        binding.timeEditText.setText("$hour:$minute")
                    else {
                        binding.timeEditText.setText(
                            "${currentCalendar.get(Calendar.HOUR_OF_DAY) + 1}:${
                                currentCalendar.get(
                                    Calendar.MINUTE
                                )
                            }"
                        )
                        chosenTime?.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY) + 1)
                        chosenTime?.set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
                    }

                }else {
                    binding.timeEditText.setText("$hour:$minute")
                    chosenTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }
                }

            }).show(childFragmentManager, null)
        }

        binding.cancelTextView.setOnClickListener {
            presenter.onCancelOrderCreateClick()
        }

        binding.payButton.setOnClickListener {
            val deliveryId = when(binding.deliveryVariantRadioGroup.checkedRadioButtonId){
                R.id.yandexRadioBtn -> YANDEX_GO
                R.id.anotherCityRadioBtn -> ANOTHER_CITY
                R.id.takeFromSellerRadioBtn -> TAKE_FROM_SELLER
                else -> -1
            }
            val placeId = when(binding.radioButtons.checkedRadioButtonId){
                R.id.onSellerChoiceRadioBtn -> PLACE_FROM_SELLER
                R.id.userVariantRadioBtn -> PLACE_FROM_ME
                else -> -1
            }
            val payId = when(binding.deliveryTypeRadioGroup.checkedRadioButtonId){
                R.id.onDealPlaceRadioButton -> PAY_ON_PLACE
                R.id.prepaymentRadioButton -> PAY_PREPAYMENT
                else -> -1
            }
            val place = if(placeId == PLACE_FROM_SELLER) OrderPlace(placeId.toLong(), null, null)
            else OrderPlace(placeId.toLong(),
                if(chosenAddressStr.isNotEmpty()) "$chosenAddressStr;${chosenTime?.timeInMillis}" else null,
                chosenTime?.timeInMillis ?: 0/1000)
            presenter.onCreateOrder(args.cartId, deliveryId, place, payId,
                findNavController().previousBackStackEntry?.destination?.id == R.id.chatFragment, args.product)


        }

        binding.chooseOnMapTextView.setOnClickListener {
            /*val deliveryId = when (binding.deliveryVariantRadioGroup.checkedRadioButtonId) {
                R.id.yandexRadioBtn -> YANDEX_GO
                R.id.anotherCityRadioBtn -> ANOTHER_CITY
                R.id.takeFromSellerRadioBtn -> TAKE_FROM_SELLER
                else -> -1
            }
            setFragmentResultListener(MyAddressesFragment.ADDRESS_REQUEST){_, bundle ->
                val address = bundle.getParcelable<MyAddress>(MyAddressesFragment.ADDRESS_KEY)
                Log.d("mylog", "address $address")
                chosenAddress = address
                val streetStr = address?.address?.street
                val houseStr = if(address?.address?.house != null) "дом.${address.address.house}" else null
                val housingStr = if(address?.address?.housing != null) "кп.${address.address.housing}" else null
                val buildingStr = if(address?.address?.building != null) "стр.${address.address.building}" else null
                val apartmentStr = if(address?.address?.apartment != null) "кв.${address.address.apartment}" else null
                val strings =
                    arrayOf(streetStr, houseStr, housingStr, buildingStr, apartmentStr).filterNotNull()
                binding.chosenAddressTextView.text = strings.joinToString(", ")
                chosenAddressStr = strings.joinToString(", ")
                binding.chosenAddressTextView.isVisible = true
                binding.chooseOnMapTextView.text = "Изменить"


            }
            findNavController().navigate(OrderCreateFragmentDirections
                .actionCreateOrderFragmentToMyAddressesFragment(true,
                if(deliveryId == ANOTHER_CITY) MyAddressesFragment.ADDRESSES_CDEK else MyAddressesFragment.ADDRESSES_HOME))*/

            getChosenAddress { _, address, _ ->
                binding.chosenAddressTextView.isVisible = true
                binding.chooseOnMapTextView.text = "Изменить"
                binding.chosenAddressTextView.text = address
            }
        }

        binding.chooseAddressBtn.setOnClickListener {
            getChosenAddress { name, address, type ->
                binding.myAddressBlock.isVisible = true
                binding.name.text = name
                binding.address.text = address
                if(type == 0){
                    binding.icon.setImageResource(R.drawable.ic_package)
                    binding.icon.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white, null))
                    binding.icon.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blue4, null))
                    binding.title.setText(R.string.myAddress_pickup)
                }else {
                    binding.icon.setImageResource(R.drawable.ic_envelope)
                    binding.icon.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green, null))
                    binding.title.setText(R.string.myAddress_delivery)
                }
                binding.chooseAddressBtn.setText("Выбрать другой адрес")
                binding.totalWithDeliveryCardView.isVisible = true
                if(binding.anotherCityRadioBtn.isChecked)
                    presenter.getCdekPrice(toAddress = address, product = args.product)
                else presenter.getYandexPrice(toAddress = address, product = args.product)


            }
        }

        binding.payNowWithDeliveryButton.setOnClickListener {
            val place = OrderPlace(PLACE_FROM_ME.toLong(), chosenAddressStr, null)
            val deliveryId = when(binding.deliveryVariantRadioGroup.checkedRadioButtonId){
                R.id.yandexRadioBtn -> YANDEX_GO
                R.id.anotherCityRadioBtn -> ANOTHER_CITY
                R.id.takeFromSellerRadioBtn -> TAKE_FROM_SELLER
                else -> -1
            }
            presenter.onCreateOrder(
                cartId = args.cartId,
                delivery = deliveryId,
                place = place,
                pay = PAY_PREPAYMENT,
                fromChat =  findNavController().previousBackStackEntry?.destination?.id == R.id.chatFragment,
                comment = if(binding.commentEditText.text?.isNotEmpty() == true) binding.commentEditText.text.toString()
                          else null,
                product = args.product,
                promo =  if(binding.promoEditText.text?.isNotEmpty() == true) binding.promoEditText.text.toString()
                         else null,
                sum = binding.finalSumTextView.text.toString().replace(',', '.').toFloat()
            )
        }

        binding.payNowButton.setOnClickListener {
            val deliveryId = when(binding.deliveryVariantRadioGroup.checkedRadioButtonId){
                R.id.yandexRadioBtn -> YANDEX_GO
                R.id.anotherCityRadioBtn -> ANOTHER_CITY
                R.id.takeFromSellerRadioBtn -> TAKE_FROM_SELLER
                else -> -1
            }
            val placeId = when(binding.radioButtons.checkedRadioButtonId){
                R.id.onSellerChoiceRadioBtn -> PLACE_FROM_SELLER
                R.id.userVariantRadioBtn -> PLACE_FROM_ME
                else -> -1
            }
            val payId = when(binding.deliveryTypeRadioGroup.checkedRadioButtonId){
                R.id.onDealPlaceRadioButton -> PAY_ON_PLACE
                R.id.prepaymentRadioButton -> PAY_PREPAYMENT
                else -> -1
            }
            val place = if(placeId == PLACE_FROM_SELLER) OrderPlace(placeId.toLong(), null, null)
            else OrderPlace(placeId.toLong(),
                if(chosenAddressStr.isNotEmpty()) "$chosenAddressStr;${chosenTime?.timeInMillis}" else null,
                chosenTime?.timeInMillis ?: 0/1000)
            presenter.onCreateOrder(args.cartId, deliveryId, place, payId,
                findNavController().previousBackStackEntry?.destination?.id == R.id.chatFragment,
                args.product,
            sum = binding.finalSumWithoutDelivery.text.toString().toFloat(),
            promo = if(binding.promoWithoutDeliveryEditText.text.isNotEmpty())
                       binding.promoWithoutDeliveryEditText.text.toString()
                       else null)

        }

    }

    private fun getChosenAddress(onAddressReceived: (addressName: String, address: String, type: Int) -> Unit){
        val deliveryId = when (binding.deliveryVariantRadioGroup.checkedRadioButtonId) {
            R.id.yandexRadioBtn -> YANDEX_GO
            R.id.anotherCityRadioBtn -> ANOTHER_CITY
            R.id.takeFromSellerRadioBtn -> TAKE_FROM_SELLER
            else -> -1
        }
        setFragmentResultListener(MyAddressesFragment.ADDRESS_REQUEST){_, bundle ->
            val address = bundle.getParcelable<MyAddress>(MyAddressesFragment.ADDRESS_KEY)
            Log.d("mylog", "address $address")
            chosenAddress = address
            val cityStr = address?.address?.city
            val streetStr = address?.address?.street
            val houseStr = if(address?.address?.house != null) "дом.${address.address.house}" else null
            val housingStr = if(address?.address?.housing != null) "кп.${address.address.housing}" else null
            val buildingStr = if(address?.address?.building != null) "стр.${address.address.building}" else null
            val apartmentStr = if(address?.address?.apartment != null) "кв.${address.address.apartment}" else null
            val cdekCode = if(address?.cdekCode != null) "cdek code:${address.cdekCode}" else null
            val strings =
                arrayOf(cityStr, streetStr, houseStr, housingStr, buildingStr, apartmentStr, cdekCode).filterNotNull()
            binding.chosenAddressTextView.text = strings.joinToString(", ")
            chosenAddressStr = strings.joinToString(", ")
            onAddressReceived(address?.name.orEmpty(), chosenAddressStr, address?.type ?: 0)
        }

        findNavController().navigate(OrderCreateFragmentDirections
            .actionCreateOrderFragmentToMyAddressesFragment(true,
                 if(deliveryId == ANOTHER_CITY) MyAddressesFragment.ADDRESSES_CDEK else MyAddressesFragment.ADDRESSES_HOME))



    }

    private fun initRadioPlaceButtons() {
        binding.radioButtons.setOnCheckedChangeListener { radioGroup, id ->
            binding.chooseOnMapTextView.isVisible = id == R.id.userVariantRadioBtn
            binding.placeIcon.isVisible = id == R.id.userVariantRadioBtn
            binding.whenReceiveTextView.isVisible = id == R.id.userVariantRadioBtn
            binding.pickers.isVisible = id == R.id.userVariantRadioBtn
            binding.chosenAddressTextView.isVisible = id == R.id.userVariantRadioBtn && chosenAddress != null
        }
    }

    private fun initCdekVariantButtons(){
        binding.deliveryCdekVariantsRadioGroup.setOnCheckedChangeListener { _, _ ->
            chosenAddress = null
            chosenAddressStr = ""
            binding.myAddressBlock.isVisible = false
        }

    }

    private fun initViews() {
        val oldPriceStr = binding.oldPriceTextView.text.toSpannable().apply { setSpan(StrikethroughSpan(), 0, length, 0) }
        binding.oldPriceTextView.text = oldPriceStr
        binding.oldPrice2TextView.text = oldPriceStr
        binding.oldPrice2WithDeliveryTextView.text = oldPriceStr
    }


    private fun initRadioDeliveryButtons() {
        binding.deliveryTypeRadioGroup.setOnCheckedChangeListener { _, radioButton ->
            binding.prepaymentInfoTextView.isVisible = radioButton == R.id.prepaymentRadioButton
            binding.totalCardView.isVisible = radioButton == R.id.prepaymentRadioButton
            binding.payNowButton.isVisible = radioButton == R.id.prepaymentRadioButton
            binding.payButton.isVisible = radioButton != R.id.prepaymentRadioButton
        }
    }

    private fun initRadioDeliveryVariantButtons() {
        binding.deliveryVariantRadioGroup.setOnCheckedChangeListener { _, radioButton ->
            showScreenWithDelivery(radioButton != R.id.takeFromSellerRadioBtn)
        }
    }

    private fun showScreenWithDelivery(show: Boolean ){
        if(binding.anotherCityRadioBtn.isChecked && chosenAddressStr.isNotEmpty())
            presenter.getCdekPrice(toAddress = chosenAddressStr, args.product)
        if(binding.yandexRadioBtn.isChecked && chosenAddressStr.isNotEmpty())
            presenter.getYandexPrice(toAddress = chosenAddressStr, args.product)
        binding.chosenDeliveryPlaceWindow.isVisible = show
        binding.commentWindow.isVisible = show
        binding.deliveryPriceInfoTextView.isVisible = show
        binding.totalWithDeliveryCardView.isVisible = show && binding.myAddressBlock.isVisible
        binding.payNowWithDeliveryButton.isVisible = show
        binding.dealPlaceWindow.isVisible = !show
        binding.paymentCardView.isVisible = !show
        binding.prepaymentInfoTextView.isVisible = !show
        binding.totalCardView.isVisible = !show && binding.prepaymentRadioButton.isChecked
        binding.payButton.isVisible = !show && binding.onDealPlaceRadioButton.isChecked
        //binding.deliveryCdekVariantCardView.isVisible = binding.anotherCityRadioBtn.isChecked
        binding.deliveryPriceTextView.text = getDeliveryPrice().toString()






    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Оформление заказа"
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_support_black)
        binding.toolbar.backIcon.setOnClickListener {
            presenter.onBackClick()
        }
    }

    private fun getSupportDialog(){
        presenter.getSupportDialog()
    }

    private fun getDeliveryPrice() = 340

    private fun getFinalSum(productPrice: Float, deliveryPrice: Float): Float{
        val sumWithDelivery = productPrice + deliveryPrice
        return sumWithDelivery + (sumWithDelivery * 0.05f)

    }

    companion object{
        const val TAKE_FROM_SELLER = 1
        const val YANDEX_GO = 2
        const val ANOTHER_CITY = 3

        const val PLACE_FROM_SELLER = 1
        const val PLACE_FROM_ME = 2

        const val PAY_ON_PLACE = 1
        const val PAY_PREPAYMENT = 2
    }
}