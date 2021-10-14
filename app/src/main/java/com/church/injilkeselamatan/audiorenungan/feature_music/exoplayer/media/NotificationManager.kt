/*
 * Copyright 2020 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.church.injilkeselamatan.audiorenungan.R
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

const val NOW_PLAYING_CHANNEL_ID = "com.church.injilkeselamatan.audiorenungan.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION_ID = 0xb339 // Arbitrary number used to identify our notification

/**
 * A wrapper class for ExoPlayer's PlayerNotificationManager. It sets up the notification shown to
 * the user during audio playback and provides track metadata, such as track title and icon image.
 */
class NotificationManager(
    context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
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
            .build()

        notificationManager.apply {
            setMediaSessionToken(sessionToken)
            setUseRewindAction(false)
            setUseRewindActionInCompactView(false)
            setUseFastForwardAction(false)
            setUseNextActionInCompactView(true)
            setUsePreviousActionInCompactView(true)
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
            newSongCallback()
            return controller.metadata.description.title.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = controller.metadata.description.iconUri
            return if (currentIconUri != iconUri || currentBitmap == null) {

                // Cache the bitmap for the current song so that successive calls to
                // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
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
