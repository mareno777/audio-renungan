package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.album
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.MusicSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetSongs(
    private val repository: SongRepository,
    private val musicSource: MusicSource
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
                emit(Resource.Success(musicSource.filter { it.album == album }.toList()))
            } else {
                emit(Resource.Success(musicSource.toList()))
            }
            Log.i(TAG, "get media metadata: ${musicSource.toList()}")
        }
    }
}
private const val TAG = "GetSongs"