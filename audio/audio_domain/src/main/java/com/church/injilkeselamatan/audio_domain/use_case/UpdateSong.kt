package com.church.injilkeselamatan.audio_domain.use_case

import com.church.injilkeselamatan.audio_domain.model.Song
import com.church.injilkeselamatan.audio_domain.repository.SongRepository

class UpdateSong(
    private val repository: SongRepository
) {
    suspend fun setFavoriteSong(song: Song) = repository.updateSong(song)

//    suspend fun updateDuration(mediaId: String, updateSongDto: UpdateSongDto) =
//        repository.updateDuration(mediaId, updateSongDto)
}