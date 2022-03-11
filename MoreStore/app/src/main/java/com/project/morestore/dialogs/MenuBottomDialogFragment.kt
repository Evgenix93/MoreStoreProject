package com.project.morestore.dialogs

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.morestore.R
import com.project.morestore.databinding.BottomdialogMenuBinding
import com.project.morestore.fragments.ChatFragment
import com.project.morestore.util.createRect
import com.project.morestore.util.dp
import com.project.morestore.util.setStartDrawable

class MenuBottomDialogFragment() :BottomSheetDialogFragment(){
    constructor(type :Type): this(){
        arguments = bundleOf("type" to type.ordinal)
    }
    private lateinit var views :BottomdialogMenuBinding
    private val bar = 1
    enum class Type{ MEDIA, GEO, PROFILE}

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
        }
        if(type == Type.PROFILE){
            Glide.with(requireContext())
                .load(R.drawable.user1)
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
                    gravity = Gravity.CENTER_VERTICAL
                    setOnClickListener {
                        (parentFragment as Callback).selectAction(menuItem)
                        dismiss()
                    }
                }
            views.root.addView(item)
        }
    }

    //todo delete stubs
    private val media :List<MenuItem> by lazy {
        listOf(MenuItem(drb(R.drawable.ic_camera), R.string.chat_menu_addMedia))
    }

    private val geoMedia :List<MenuItem> by lazy { listOf(
       // MenuItem(drb(R.drawable.ic_arrow_geo), R.string.chat_menu_setGeoDeal),
        MenuItem(drb(R.drawable.ic_camera), R.string.chat_menu_addMedia)
    )}

    private val profile :List<MenuItem> by lazy { listOf(
        MenuItem(drb(R.drawable.ic_arrow_geo), R.string.chat_menu_profile),
        //MenuItem(drb(R.drawable.ic_phone), R.string.chat_menu_call),
        MenuItem(drb(R.drawable.ic_star_bubble), R.string.chat_menu_feedback),
        MenuItem(drb(R.drawable.ic_circle_crossed), R.string.chat_menu_block),
        MenuItem(drb(R.drawable.ic_exclamation_round), R.string.chat_menu_report),
        MenuItem(drb(R.drawable.ic_trash), R.string.chat_menu_delete)
    )}

    private fun drb(@DrawableRes drwbId :Int) = ContextCompat.getDrawable(requireContext(), drwbId)!!

    inner class MenuItem(val drawable :Drawable, @StringRes val titleId :Int)

    interface Callback{
        fun selectAction(item :MenuItem)
    }
}