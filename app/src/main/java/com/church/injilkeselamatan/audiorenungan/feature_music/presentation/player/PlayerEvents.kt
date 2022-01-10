package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

sealed class PlayerEvents {
    data class PlayOrPause(val isPlay: Boolean) : PlayerEvents()
    data class PlayFromMediaId(val mediaId: String) : PlayerEvents()
    data class SeekTo(
        val position: Long = 0L,
        val positionFraction: Float = 0f
    ) : PlayerEvents()

    data class SetSleepTimer(val timer: Long) : PlayerEvents()
    object SkipToPrevious : PlayerEvents()
    object SkipToNext : PlayerEvents()
    object FastForward : PlayerEvents()
    object FastRewind : PlayerEvents()
}