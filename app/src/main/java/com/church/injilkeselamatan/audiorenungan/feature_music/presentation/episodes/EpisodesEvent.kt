package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes

import android.support.v4.media.MediaMetadataCompat

sealed class EpisodesEvent {
    data class DownloadEpisode(val song: MediaMetadataCompat) : EpisodesEvent()
    data class RemoveDownloadedEpisode(val song: MediaMetadataCompat) : EpisodesEvent()
    data class PlayToogle(val episode: MediaMetadataCompat, val isPlay: Boolean) : EpisodesEvent()
}