package com.scheffer.erik.popularmovies.utils

import android.content.Context
import android.net.ConnectivityManager

fun isConnected(context: Context) =
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.isConnectedOrConnecting ?: false
