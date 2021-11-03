package com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.download

import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class DownloadListener : DownloadManager.Listener {

    private val download: Any? = null

    val downloadComplated = MutableStateFlow(download as? Download)

    private val onWaitingRequirements = MutableStateFlow(false)

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
        super.onDownloadChanged(downloadManager, download, finalException)
    }

    override fun onWaitingForRequirementsChanged(
        downloadManager: DownloadManager,
        waitingForRequirements: Boolean
    ) {
        scope.launch {
            onWaitingRequirements.emit(waitingForRequirements)
        }
        super.onWaitingForRequirementsChanged(downloadManager, waitingForRequirements)
    }
}

private const val TAG = "DownloadListener"