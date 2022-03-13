package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

import android.util.Log
import com.church.injilkeselamatan.audiorenungan.BuildConfig
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.VersionResponseDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.network.sockets.*
import java.nio.channels.UnresolvedAddressException

class CheckVersion(private val client: HttpClient) {

    private val currentVersion = BuildConfig.VERSION_CODE
    private val endpoint = "http://aws.injilkeselamatan.com:8080"

    suspend fun isLatestVersion(): Boolean {
        return try {
            val response =
                client.get<VersionResponseDto>("$endpoint/version").versionResponse.version
            currentVersion >= response
        } catch (e: UnresolvedAddressException) {
            Log.e("CheckVersion", "$e")
            true
        } catch (e: ConnectTimeoutException) {
            Log.e("CheckVersion", e.toString())
            true
        } catch (e: Exception) {
            Log.e("CheckVersion", "$e")
            false
        }
    }
}