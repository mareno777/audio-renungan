package com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.download

import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.scheduler.Requirements

class DownloadListener : DownloadManager.Listener {

    val downloadComplated = MutableLiveData<Download>()
        .apply {
            postValue(null)
        }
    val onWaitingRequirements = MutableLiveData<Boolean>()
        .apply {
            postValue(false)
        }

    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?
    ) {
        if (download.bytesDownloaded == download.contentLength) {
            // Download complated
            downloadComplated.postValue(download)
        }
        super.onDownloadChanged(downloadManager, download, finalException)
    }

    override fun onWaitingForRequirementsChanged(
        downloadManager: DownloadManager,
        waitingForRequirements: Boolean
    ) {
        onWaitingRequirements.postValue(waitingForRequirements)
        super.onWaitingForRequirementsChanged(downloadManager, waitingForRequirements)
    }

    override fun onRequirementsStateChanged(
        downloadManager: DownloadManager,
        requirements: Requirements,
        notMetRequirements: Int
    ) {
        super.onRequirementsStateChanged(downloadManager, requirements, notMetRequirements)
    }
}

private const val TAG = "DownloadListener"