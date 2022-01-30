package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case


import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow

class GetFeaturedSong(
    private val songRepository: SongRepository
) {
    operator fun invoke(): Flow<Resource<Song>> {
        return songRepository.getFeaturedSong()
    }
}