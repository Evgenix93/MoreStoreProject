package dev.jorik.stub

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.defToast(text :String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.defToast(@StringRes textId :Int){
    Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
}

fun View.defToast(text :String) = context.defToast(text)
fun View.defToast(@StringRes textId :Int) = context.defToast(textId)