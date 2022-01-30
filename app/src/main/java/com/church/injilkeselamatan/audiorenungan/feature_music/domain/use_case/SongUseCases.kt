package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

data class SongUseCases(
    val getSongs: GetSongs,
    val getDownloadedSongs: GetDownloadedSongs,
    val updateSong: UpdateSong,
    val downloadSong: DownloadSong,
    val getFeaturedSong: GetFeaturedSong
)