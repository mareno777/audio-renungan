package com.church.injilkeselamatan.audio_domain.use_case

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService

class DownloadAudio(
    private val context: Context,
    private val downloadManager: DownloadManager,
    private val serviceClass: Class<out DownloadService>
) {

    fun sendAddDownload(
        mediaId: String,
        mediaUri: String,
        title: String,
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

    fun initiateService() {
        if (downloadManager.currentDownloads.isNotEmpty()) {
            DownloadService.startForeground(context, serviceClass)
        }
    }

    fun sendRemoveDownload(mediaId: String) {
        DownloadService.sendRemoveDownload(
            context,
            serviceClass,
            mediaId,
            true
        )
    }
}

private const val TAG = "DownloadSong"