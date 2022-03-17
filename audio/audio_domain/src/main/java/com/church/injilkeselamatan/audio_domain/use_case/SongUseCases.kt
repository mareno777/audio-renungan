package com.church.injilkeselamatan.audio_domain.use_case

data class SongUseCases(
    val getSongs: GetSongs,
    val getDownloadedSongs: GetDownloadedSongs,
    val updateSong: UpdateSong,
    val downloadSong: DownloadSong,
    val removeDownloadedSong: RemoveDownloadedSong,
    val getFeaturedSong: GetFeaturedSong
)