package com.church.injilkeselamatan.core.download

import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DownloadListener : DownloadManager.Listener {

    val downloadComplated = MutableLiveData<Boolean>()

    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?
    ) {
        downloadComplated.value = downloadManager.currentDownloads.isEmpty()
    }

    override fun onIdle(downloadManager: DownloadManager) {
        downloadComplated.value = downloadManager.currentDownloads.isEmpty()
    }
}

private const val TAG = "DownloadListener"