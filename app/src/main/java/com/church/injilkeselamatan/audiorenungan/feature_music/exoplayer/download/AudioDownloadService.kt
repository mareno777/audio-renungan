package com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.download

import android.app.Notification
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.church.injilkeselamatan.audiorenungan.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

const val DOWNLOAD_NOTIFICATION_ID = 2
const val DOWNLOAD_CHANNEL_ID = "com.church.injilkeselamatan.audiorenungan.DOWNLOADING"

@AndroidEntryPoint
class AudioDownloadService : DownloadService
    (
    DOWNLOAD_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_CHANNEL_ID,
    R.string.notification_channel_download,
    R.string.notification_description_download
) {

    @Inject
    lateinit var downloadMananger: DownloadManager

    @Inject
    lateinit var downloadListener: DownloadListener

    override fun getDownloadManager(): DownloadManager {
        return downloadMananger
    }

    override fun getScheduler(): Scheduler {
        return PlatformScheduler(this, 1)
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {

        val activeDownloads = downloads.size > 0

        return if (!activeDownloads) {
            return DownloadNotificationHelper(this, DOWNLOAD_CHANNEL_ID).buildProgressNotification(
                this,
                android.R.drawable.stat_sys_download,
                null,
                null,
                downloads,
                notMetRequirements
            )
        } else {
            val notification = NotificationCompat.Builder(this, DOWNLOAD_CHANNEL_ID)
                .setOngoing(true)
                .setShowWhen(false)
                .setColor(Color.BLUE)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Mengunduh Audio Renungan")
                .setContentText(percentageFromFloat(downloads[0].percentDownloaded))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .setBigContentTitle("Mengunduh Audio Renungan")
                        .bigText(
                            "${downloads[0].request.customCacheKey}\n${
                                percentageFromFloat(
                                    downloads[0].percentDownloaded
                                )
                            }"
                        )
                )
                .setProgress(
                    downloads[0].contentLength.toInt(),
                    downloads[0].bytesDownloaded.toInt(),
                    false
                ) // FIXME: 9/22/2021 : baru coba false

            notification.build()
        }
    }

    override fun onDestroy() {
        downloadMananger.removeListener(downloadListener)
        super.onDestroy()
    }

    private fun percentageFromFloat(fl: Float): String {
        return String.format(Locale.US, "%d%%", (fl * 1).toInt())
    }

    private fun getDownloadedMedia(): List<MediaItem> {
        val mediaItems: MutableList<MediaItem> = mutableListOf()

        val downloadCursor = downloadMananger.downloadIndex.getDownloads()

        if (downloadCursor.moveToFirst()) {
            do {
                val mediaItem = downloadCursor.download.request.toMediaItem()
                mediaItems.add(mediaItem)
            } while (downloadCursor.moveToNext())
        }

        return mediaItems
    }
}