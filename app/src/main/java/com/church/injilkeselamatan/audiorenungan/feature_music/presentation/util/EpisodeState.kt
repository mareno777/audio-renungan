package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util

data class EpisodeState(
    var isPlaying: Boolean = false,
    var isDownloaded: Boolean = false,
    var downloadSize: Int = 0,
    var downloadedLength: Int = 0,
    var onQueue: Boolean = false
) {
    var mediaId: String = ""
    var title: String = ""
}
