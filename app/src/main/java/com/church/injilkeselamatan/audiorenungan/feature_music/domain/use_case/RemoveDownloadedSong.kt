package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

import android.content.Context
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.download.AudioDownloadService
import com.google.android.exoplayer2.offline.DownloadService

class RemoveDownloadedSong(
    private val context: Context
) {
    operator fun invoke(mediaId: String) {
        DownloadService.sendRemoveDownload(
            context,
            AudioDownloadService::class.java,
            mediaId,
            true
        )
    }
}