package com.example.myapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ConnectivityCheckInspector constructor(context: Context) : Interceptor {
    private var context: Context = context;

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline(context)){
            throw NoConnectivityException();
        }
        var  builder : Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager?.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}