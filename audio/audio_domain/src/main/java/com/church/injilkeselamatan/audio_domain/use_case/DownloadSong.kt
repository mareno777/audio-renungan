package com.church.injilkeselamatan.audio_domain.use_case

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService

class DownloadSong(
    private val context: Context,
    private val downloadManager: DownloadManager
) {

    operator fun invoke(
        mediaId: String,
        mediaUri: String,
        title: String,
        serviceClass: Class<out DownloadService>
    ) {
        Log.d(TAG, "mediaId to download: $mediaId")
        val downloadRequest = DownloadRequest.Builder(
            mediaId,
            Uri.parse(mediaUri)
        )
            .setCustomCacheKey(title)
            .build()

        DownloadService.sendAddDownload(
            context,
            serviceClass,
            downloadRequest,
            true
        )

    }

    fun initiateService(serviceClass: Class<out DownloadService>) {
        if (downloadManager.currentDownloads.isNotEmpty()) {
            DownloadService.startForeground(context, serviceClass)
        }
    }
}

private const val TAG = "DownloadSong"