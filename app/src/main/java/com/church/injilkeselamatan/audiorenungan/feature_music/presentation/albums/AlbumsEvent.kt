package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums

sealed class AlbumsEvent {
    data class PlayOrPause(val isPlay: Boolean) : AlbumsEvent()
}
