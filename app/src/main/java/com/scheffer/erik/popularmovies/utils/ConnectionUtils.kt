package com.scheffer.erik.popularmovies.utils

import android.content.Context
import android.net.ConnectivityManager

fun isConnected(context: Context): Boolean {
    val activeNetwork =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                    .activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}
