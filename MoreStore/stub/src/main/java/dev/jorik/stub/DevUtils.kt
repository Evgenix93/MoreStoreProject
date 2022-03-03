package dev.jorik.stub

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.defToast(text :String){
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.defToast(@StringRes textId :Int){
    Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
}