package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.toDbEntity
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository

class UpdateSong(
    private val repository: SongRepository
) {

    suspend operator fun invoke(song: Song) = repository.updateSong(
        song.toDbEntity()
    )
}