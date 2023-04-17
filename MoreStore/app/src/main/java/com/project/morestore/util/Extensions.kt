package com.project.morestore.util

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun String.isEmailValid(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPhoneValid(): Boolean {
    return android.util.Patterns.PHONE.matcher(this).matches() && (this.length == 12 || this.length == 11)
}

fun AutoCompleteTextView.showDropdown(adapter: ArrayAdapter<String>?) {
    if(!TextUtils.isEmpty(this.text.toString())){
        adapter?.filter?.filter(null)
    }
}

fun Calendar.diffInDays(after :Calendar) :Int{
    return TimeUnit.MILLISECONDS.toDays(abs(timeInMillis - after.timeInMillis)).toInt()
}

fun String.isFilled(minLength :Int = 1) :Boolean{
    return this.trim().length >= minLength
}

fun Resources.getDrawableRes(id: Int): Drawable?{
    return ResourcesCompat.getDrawable(this, id, null)
}

suspend fun ResponseBody.getStringFromResponse(): String{
    return withContext(Dispatchers.IO) {
        val str = this@getStringFromResponse.string()
        Log.d("mylog", str)
        str
    }
}

suspend fun errorMessage(response: Response<*>?): String{
  return  when(response?.code()){
        400 -> response.errorBody()!!.getStringFromResponse()
        500 -> "500 Internal Server Error"
        null -> "нет сети"
        else -> "неизвестная ошибка"
    }
}