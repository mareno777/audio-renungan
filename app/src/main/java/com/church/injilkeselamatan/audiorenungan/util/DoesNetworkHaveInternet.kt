package com.church.injilkeselamatan.audiorenungan.util

import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


/**
 * Send a ping to googles primary DNS.
 * If successful, that means we have internet.
 */
object DoesNetworkHaveInternet {
    private val TAG = "PingGoogle"

    // Make sure to execute this on a background thread.
    fun execute(): Boolean {
        val urlc =
            URL("http://clients3.google.com/generate_204").openConnection() as HttpURLConnection
        return try {
            urlc.setRequestProperty("User-Agent", "Test")
            urlc.setRequestProperty("Connection", "close")
            urlc.connectTimeout = 1500
            urlc.readTimeout = 3000
            urlc.connect()
            urlc.responseCode == 204 && urlc.getContentLength() == 0
        } catch (e: IOException) {
            Log.e(TAG, "No internet connection. $e")
            false
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Connection timeout $e")
            false
        }
    }
}