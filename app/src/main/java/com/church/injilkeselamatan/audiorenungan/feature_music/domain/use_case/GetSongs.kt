package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSongs(
    private val repository: SongRepository
) {

    operator fun invoke(album: String? = null): Flow<Resource<List<Song>>> {
        return if (album != null) {
            repository.getSongs().map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val mutable = mutableListOf<Song>()
                        resource.data?.let { songs ->
                            val filteredSongs = songs.filter { it.album == album }
                            mutable.addAll(filteredSongs)
                        }
                        Resource.Success<List<Song>>(mutable.sortedBy { it.id })
                    }
                    is Resource.Loading -> {
                        Resource.Loading<List<Song>>()
                    }
                    is Resource.Error -> {
                        Resource.Error<List<Song>>(resource.message)
                    }
                }
            }
        } else {
            repository.getSongs()
        }
    }
}