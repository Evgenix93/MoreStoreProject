package com.project.morestore.util

import android.widget.EditText

fun String.isEmailValid(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPhoneValid(): Boolean {
    return android.util.Patterns.PHONE.matcher(this).matches() && (this.length == 12 || this.length == 11)
}