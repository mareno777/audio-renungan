package com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.download

import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DownloadListener : DownloadManager.Listener {

    private val download: Any? = null

    val downloadComplated = MutableStateFlow(download as? Download)

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?
    ) {
        if (download.bytesDownloaded == download.contentLength) {
            // Download complated
            scope.launch {
                downloadComplated.emit(download)
            }
        }
    }
}

private const val TAG = "DownloadListener"