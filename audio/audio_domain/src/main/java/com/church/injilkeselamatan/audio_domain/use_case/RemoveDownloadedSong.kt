package com.church.injilkeselamatan.audio_domain.use_case

import android.content.Context
import com.google.android.exoplayer2.offline.DownloadService

class RemoveDownloadedSong(
    private val context: Context
) {
    operator fun invoke(mediaId: String, serviceClass: Class<out DownloadService>) {
        DownloadService.sendRemoveDownload(
            context,
            serviceClass,
            mediaId,
            true
        )
    }
}