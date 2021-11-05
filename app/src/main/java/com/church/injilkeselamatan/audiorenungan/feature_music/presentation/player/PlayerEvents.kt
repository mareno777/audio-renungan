package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

sealed class PlayerEvents {
    data class PlayOrPause(val isPlay: Boolean) : PlayerEvents()
    data class PlayFromMediaId(val mediaId: String) : PlayerEvents()
    object SkipToPrevious : PlayerEvents()
    object SkipToNext : PlayerEvents()
    data class SeekTo(val position: Long) : PlayerEvents()
}