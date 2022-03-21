package com.church.injilkeselamatan.audio_domain.use_case

data class SongUseCases(
    val getSongs: GetSongs,
    val loadRecentSong: LoadRecentSong,
    val getDownloadedSongs: GetDownloadedSongs,
    val updateSong: UpdateSong,
    val getFeaturedSong: GetFeaturedSong
)