package com.church.injilkeselamatan.audio_presentation

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

const val NOW_PLAYING_CHANNEL_ID = "com.church.injilkeselamatan.audiorenungan.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION_ID = 0xb339 // Arbitrary number used to identify our notification

class NotificationManager(
    context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: (MediaMetadataCompat) -> Unit
) {

    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)

        notificationManager = PlayerNotificationManager
            .Builder(context, NOW_PLAYING_NOTIFICATION_ID, NOW_PLAYING_CHANNEL_ID)
            .setNotificationListener(notificationListener)
            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            .setChannelDescriptionResourceId(R.string.notification_channel_description)
            .setChannelNameResourceId(R.string.notification_channel)
            .setPlayActionIconResourceId(R.drawable.ic_play_arrow)
            .setPauseActionIconResourceId(R.drawable.ic_pause)
            .setFastForwardActionIconResourceId(R.drawable.ic_round_forward_10)
            .setRewindActionIconResourceId(R.drawable.ic_round_replay_10)
            .setNextActionIconResourceId(R.drawable.ic_skip_next)
            .setPreviousActionIconResourceId(R.drawable.ic_skip_previous)
            .build()

        notificationManager.apply {
            setMediaSessionToken(sessionToken)
            setUseRewindAction(true)
            setUseRewindActionInCompactView(true)
            setUseFastForwardAction(true)
            setUseFastForwardActionInCompactView(true)
            setUseNextActionInCompactView(false)
            setUsePreviousActionInCompactView(false)
            setUseChronometer(false)
            setUseStopAction(false)
        }
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotificationForPlayer(player: Player) {
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player) =
            controller.metadata.description.subtitle.toString()

        override fun getCurrentContentTitle(player: Player): CharSequence {
            newSongCallback(controller.metadata)
            return controller.metadata.description.title.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = controller.metadata.description.iconUri
            return if (currentIconUri != iconUri || currentBitmap == null) {
                currentIconUri = iconUri
                currentBitmap = controller.metadata.description.iconBitmap
                currentBitmap?.let { callback.onBitmap(it) }
                controller.metadata.description.iconBitmap
            } else {
                currentBitmap
            }
        }
    }
}
