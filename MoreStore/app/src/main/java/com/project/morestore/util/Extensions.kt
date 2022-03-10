package com.project.morestore.util

import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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