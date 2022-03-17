package com.church.injilkeselamatan.audio_domain.use_case

import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.church.injilkeselamatan.audio_domain.model.Song
import com.church.injilkeselamatan.audio_domain.repository.SongRepository
import com.church.injilkeselamatan.core.util.Resource
import com.church.injilkeselamatan.core.util.extensions.album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetSongs(
    private val repository: SongRepository
) {

    operator fun invoke(album: String? = null): Flow<Resource<List<Song>>> {
        return if (album != null) {
            var mutable = listOf<Song>()

            repository.getSongs(false).map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { songs ->
                            val filteredSongs = songs.filter { it.album == album }
                            mutable = filteredSongs
                        }
                        Resource.Success<List<Song>>(mutable.sortedBy { it.id })
                    }
                    is Resource.Loading -> {
                        resource.data?.let { songs ->
                            val filteredSongs = songs.filter { it.album == album }
                            mutable = filteredSongs
                        }
                        Resource.Loading<List<Song>>(mutable.sortedBy { it.id })
                    }
                    is Resource.Error -> {
                        resource.data?.let { songs ->
                            val filteredSongs = songs.filter { it.album == album }
                            mutable = filteredSongs
                        }
                        Resource.Error<List<Song>>(resource.message, mutable.sortedBy { it.id })
                    }
                }
            }
        } else {
            repository.getSongs(false)
        }
    }

    fun getMediaMetadataCompats(album: String? = null): Flow<Resource<List<MediaMetadataCompat>>> {
        return flow {
            emit(Resource.Loading<List<MediaMetadataCompat>>())
            if (album != null) {
                emit(Resource.Success(repository.mediaMetadataCompats.filter { it.album == album }
                    .toList()))
            } else {
                emit(Resource.Success(repository.mediaMetadataCompats))
            }
            Log.i(TAG, "get media metadata: ${repository.mediaMetadataCompats}")
        }
    }
}

private const val TAG = "GetSongs"