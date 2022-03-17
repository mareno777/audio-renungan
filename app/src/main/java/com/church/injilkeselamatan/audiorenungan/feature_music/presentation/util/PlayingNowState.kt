package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util

import android.support.v4.media.MediaMetadataCompat
import com.church.injilkeselamatan.core.NOTHING_PLAYING

data class PlayingNowState(
    val mediaMetadataCompat: MediaMetadataCompat = NOTHING_PLAYING,
    val isPlaying: Boolean = false
)
