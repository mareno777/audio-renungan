package com.church.injilkeselamatan.audio_domain.use_case


import com.church.injilkeselamatan.audio_domain.model.Song
import com.church.injilkeselamatan.audio_domain.repository.SongRepository
import com.church.injilkeselamatan.core.util.Resource
import kotlinx.coroutines.flow.Flow

class GetFeaturedSong(
    private val songRepository: SongRepository
) {
    operator fun invoke(): Flow<Resource<Song>> {
        return songRepository.getFeaturedSong()
    }
}