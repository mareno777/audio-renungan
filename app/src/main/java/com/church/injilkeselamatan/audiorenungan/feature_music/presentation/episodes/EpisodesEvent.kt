package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes

import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song

sealed class EpisodesEvent {
    data class DownloadEpisode(val song: Song) : EpisodesEvent()
    data class PlayToogle(val episode: Song, val isPlay: Boolean) : EpisodesEvent()
}