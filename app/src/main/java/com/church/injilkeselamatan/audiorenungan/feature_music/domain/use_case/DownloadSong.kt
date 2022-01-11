package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

import android.content.Context
import android.net.Uri
import android.util.Log
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.download.AudioDownloadService
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService

class DownloadSong(private val context: Context, private val downloadManager: DownloadManager) {

    operator fun invoke(mediaId: String, mediaUri: String, title: String) {
        Log.d(TAG, "mediaId to download: $mediaId")
        val downloadRequest =
            DownloadRequest.Builder(
                mediaId,
                Uri.parse(mediaUri)
            )
                .setCustomCacheKey(title)
                .build()

        DownloadService.sendAddDownload(
            context,
            AudioDownloadService::class.java,
            downloadRequest,
            true
        )

    }

    fun initiateService() {
        if (downloadManager.currentDownloads.size > 0) {
            DownloadService.startForeground(context, AudioDownloadService::class.java)
        }
    }
}

private const val TAG = "DownloadSong"