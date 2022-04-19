package com.project.morestore.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import kotlin.math.roundToInt

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

val Context.inflater :LayoutInflater get() = LayoutInflater.from(this)

val ViewGroup.inflater :LayoutInflater get() = LayoutInflater.from(context)

fun ViewGroup.createView(@LayoutRes layoutId :Int) :View{
    return inflater.inflate(layoutId, this, false)
}

val Fragment.args get() = requireArguments()

fun EditText.addTextChangeListener(callback :(String) -> Unit){
    addTextChangedListener { callback(it!!.toString()) }
}

fun RecyclerView.setSpace(px :Int){
    addItemDecoration(
        DividerItemDecoration(context, RecyclerView.VERTICAL)
            .apply { setDrawable(createRect(0, px)) }
    )
}

fun RecyclerView.setDivider(drawable :Drawable){
    val divider = DividerItemDecoration(context, RecyclerView.VERTICAL)
    divider.setDrawable(drawable)
    addItemDecoration(divider)
}

fun TextView.setStartDrawable(@DrawableRes drawableId :Int){
    ContextCompat.getDrawable(context, drawableId)
        .also { setCompoundDrawablesWithIntrinsicBounds(it, null, null, null) }
}

fun TextView.setStartDrawable(drawable :Drawable, greenCircle: Drawable? = null){
    setCompoundDrawablesWithIntrinsicBounds(drawable, null, greenCircle, null)
}

fun TextView.setEndDrawable(drawable :Drawable){
    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun createRect(width :Int, height :Int, argbColor :Int = Color.TRANSPARENT) :Drawable{
    return GradientDrawable().apply{
        shape = GradientDrawable.RECTANGLE
        setColor(argbColor)
        setSize(width, height)
    }
}

fun Context.makeView(@LayoutRes layoutId :Int) : View {
    return LayoutInflater.from(this).inflate(layoutId, null, false)
}

fun ViewGroup.makeView(@LayoutRes layoutId :Int) : View {
    return LayoutInflater.from(context).inflate(layoutId, this, false)
}

fun TabLayout.setSelectListener(callback :(TabLayout.Tab) -> Unit){
    addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
        override fun onTabSelected(tab: TabLayout.Tab?) { tab?.let(callback) }

        override fun onTabUnselected(tab: TabLayout.Tab?) {/* skip */}

        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })
}

fun EditText.setPhoneField(){
    MaskImpl(PredefinedSlots.RUS_PHONE_NUMBER, true)
        .also{ MaskFormatWatcher(it).installOn(this) }
}

fun Toolbar.attachNavigation(){
    setNavigationOnClickListener { findNavController().popBackStack() }//todo apply for whole app
}