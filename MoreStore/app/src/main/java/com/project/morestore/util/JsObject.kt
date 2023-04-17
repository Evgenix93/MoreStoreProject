package com.project.morestore.util

import android.app.Application
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.core.view.isVisible

class JsObject {
    @JavascriptInterface
    fun receiveMessage(json: String?) {
        Log.d("addCard", json.toString() )

       //if(json != null) webView?.isVisible = false                                  // here we return true if we handled the post.
    }
}