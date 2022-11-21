package com.project.morestore.util

import com.project.morestore.data.singletones.Token
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
            .addHeader("Authorization", Token.token)
            .build()

        return chain.proceed(newRequest)
    }
}