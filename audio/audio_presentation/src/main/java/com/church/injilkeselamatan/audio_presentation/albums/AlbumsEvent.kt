package com.church.injilkeselamatan.audio_presentation.albums

sealed class AlbumsEvent {
    data class PlayOrPause(val isPlay: Boolean) : AlbumsEvent()
    object PlayFeatured : AlbumsEvent()
}
