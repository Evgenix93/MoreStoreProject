package com.project.morestore.dialogs

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton
import com.project.morestore.R
import com.project.morestore.databinding.BottomdialogMenuBinding
import com.project.morestore.fragments.ChatFragment
import com.project.morestore.util.createRect
import com.project.morestore.util.dp
import com.project.morestore.util.setEndDrawable
import com.project.morestore.util.setStartDrawable
import com.tbuonomo.viewpagerdotsindicator.setPaddingVertical

class MenuBottomDialogFragment(val avatar: String?, private val showReviewBtn: Boolean) :BottomSheetDialogFragment(){
    constructor(type :Type, isMediaLoaded: Boolean? = null, avatar: String? = null, showReviewBtn: Boolean = false): this(avatar, showReviewBtn){
        arguments = bundleOf("type" to type.ordinal, "media" to isMediaLoaded)
    }
    private lateinit var views :BottomdialogMenuBinding
    enum class Type{ MEDIA, GEO, PROFILE, PAYMENT}
    private var cardItems = mutableListOf<MenuItemCard>()

    override fun getTheme() = R.style.App_Dialog_Transparent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomdialogMenuBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.root.dividerDrawable = createRect(0, 20.dp)
        val type = Type.values()[requireArguments().getInt("type")]
        when(type){
            Type.MEDIA -> inflateItems(media)
            Type.GEO -> inflateItems(geoMedia)
            Type.PROFILE -> inflateItems(profile)
            Type.PAYMENT -> inflateCardItems(payment)
        }
        if(type == Type.PROFILE){
            Glide.with(requireContext())
                .load(avatar)
                .apply { override(24.dp) }
                .circleCrop()
                .into(object :CustomTarget<Drawable>(){
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        (views.root.getChildAt(0) as TextView).setStartDrawable(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    private fun inflateItems(items :List<MenuItem>){
        items.forEach { menuItem ->
            val item = (layoutInflater.inflate(R.layout.item_bottommenu, views.root, false) as TextView)
                .apply {
                    setText(menuItem.titleId)
                    setStartDrawable(menuItem.drawable)
                    if(menuItem.titleId == R.string.chat_menu_addMedia && menuItem.isMediaLoaded == true)
                    setStartDrawable(menuItem.drawable, menuItem.greenCircle!!)
                    gravity = Gravity.CENTER_VERTICAL
                    setOnClickListener {
                        (parentFragment as Callback).selectAction(menuItem)
                        dismiss()
                    }
                }
            views.root.addView(item)
        }
    }

    private fun inflateCardItems(items: List<MenuItemCard>){
        views.root.removeAllViews()
        views.root.setPaddingVertical(0)
        views.root.dividerDrawable = null
        val title = layoutInflater.inflate(R.layout.bottom_menu_title, views.root, false)
        val addCardView = layoutInflater.inflate(R.layout.bottom_menu_add_card, views.root, false).apply {
            setOnClickListener {
                (parentFragment as AddCardCallback).onAddCard()
                dismiss()

            }
        }
        val payBtnItem = layoutInflater.inflate(R.layout.bottom_menu_button, views.root, false)
        views.root.addView(title)
        cardItems = items.toMutableList()
        items.forEach { menuItem ->
            val item = (layoutInflater.inflate(R.layout.item_card_bottom_menu, views.root, false) as FrameLayout )
                .apply {

                    val cardTextView = findViewById<TextView>(R.id.cardTextView)
                    val radioBtn = findViewById<MaterialRadioButton>(R.id.radioBtn)
                    cardTextView.text = menuItem.cardNumber
                    cardTextView.setStartDrawable(menuItem.drawable)
                    radioBtn.isChecked = menuItem.isChecked
                    radioBtn.setOnClickListener {
                        cardItems.forEach { it.isChecked = false }
                        val card = cardItems.find { cardItem -> cardItem == menuItem }
                        card?.isChecked = true
                        inflateCardItems(cardItems)
                    }
                    radioBtn.isUseMaterialThemeColors = false
                }
            views.root.addView(item)
        }
        views.root.addView(addCardView)
        views.root.addView(payBtnItem.apply {
            setOnClickListener { (parentFragment as PayCallback).onPayPressed(cardItems.find { it.isChecked }!!)
            dismiss()}
        })





    }

    //todo delete stubs
    private val media :List<MenuItem> by lazy {
        listOf(MenuItem(drb(R.drawable.ic_camera), R.string.chat_menu_addMedia, arguments?.getBoolean("media"), drb(R.drawable.ic_ellipse)))
    }

    private val geoMedia :List<MenuItem> by lazy { listOf(
       // MenuItem(drb(R.drawable.ic_arrow_geo), R.string.chat_menu_setGeoDeal),
        MenuItem(drb(R.drawable.ic_camera), R.string.chat_menu_addMedia, arguments?.getBoolean("media"), drb(R.drawable.ic_ellipse))
    )}

    private val profile :List<MenuItem> by lazy {
        listOfNotNull(
            MenuItem(drb(R.drawable.ic_arrow_geo), R.string.chat_menu_profile),
            //MenuItem(drb(R.drawable.ic_phone), R.string.chat_menu_call),
            if (showReviewBtn) MenuItem(
                drb(R.drawable.ic_star_bubble),
                R.string.chat_menu_feedback
            ) else null,
            MenuItem(drb(R.drawable.ic_circle_crossed), R.string.chat_menu_block),
            MenuItem(drb(R.drawable.ic_exclamation_round), R.string.chat_menu_report),
            MenuItem(drb(R.drawable.ic_trash), R.string.chat_menu_delete)
        )
    }

    private val payment: List<MenuItemCard> by lazy { listOf(
        MenuItemCard(drb(R.drawable.ic_google_pay), "Google Pay", true),
        MenuItemCard(drb(R.drawable.ic_visa), "• 1687")
    ) }

    private fun drb(@DrawableRes drwbId :Int) = ContextCompat.getDrawable(requireContext(), drwbId)!!

    inner class MenuItem(val drawable :Drawable, @StringRes val titleId :Int,  val isMediaLoaded: Boolean? = null, val greenCircle: Drawable? = null)
    inner class MenuItemCard(val drawable: Drawable, val cardNumber: String, var isChecked: Boolean = false)

    interface Callback{
        fun selectAction(item :MenuItem)
    }
    interface PayCallback {
        fun onPayPressed(item: MenuItemCard)
    }

    interface AddCardCallback{
        fun onAddCard()
    }
}