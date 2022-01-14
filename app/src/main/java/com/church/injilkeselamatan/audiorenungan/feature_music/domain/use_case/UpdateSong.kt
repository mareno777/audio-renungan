package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.UpdateSongDto
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.toDbEntity
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository

class UpdateSong(
    private val repository: SongRepository
) {
    suspend fun setFavoriteSong(song: Song) = repository.updateSong(
        song.toDbEntity()
    )

    suspend fun updateDuration(mediaId: String, updateSongDto: UpdateSongDto) =
        repository.updateDuration(mediaId, updateSongDto)
}