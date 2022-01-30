package com.church.injilkeselamatan.audiorenungan.feature_music.data.repository

import android.util.Log
import com.church.injilkeselamatan.audiorenungan.BuildConfig
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.toSong
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.*
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.nio.channels.UnresolvedAddressException


class SongRepositoryImpl(
    musicDatabase: MusicDatabase,
    private val client: HttpClient
) : SongRepository {

    private val endpointUrl = "${BuildConfig.BASE_URL}/audio"
    private val musicDao = musicDatabase.musicDao()

    override fun getSongs(fromDbOnly: Boolean): Flow<Resource<List<Song>>> = flow {
        emit(Resource.Loading())
        val songs = musicDao.getAllSongs().map { it.toSong() }

        if (fromDbOnly) {
            emit(Resource.Success(songs))
        } else {
            emit(Resource.Loading(data = songs))

            try {
                val remoteSongs = client.get<MusicApiDto>(endpointUrl).music
                musicDao.deleteAllSongs()
                musicDao.insertSongs(remoteSongs.map { it.toMusicDb() })
            } catch (e: UnresolvedAddressException) {
                Log.e(TAG, e.toString())
                emit(
                    Resource.Error(
                        message = "Couldn't reach server, check your internet connection",
                        data = songs
                    )
                )
            } catch (e: ClientRequestException) {
                Log.e(TAG, e.toString())
                emit(
                    Resource.Error(
                        message = "Couldn't reach server, check your internet connection",
                        data = songs
                    )
                )
            } catch (e: ServerResponseException) {
                Log.e(TAG, e.toString())
                emit(
                    Resource.Error(
                        message = "Couldn't reach server, check your internet connection",
                        data = songs
                    )
                )
            } catch (e: ConnectTimeoutException) {
                emit(Resource.Error(
                    message = e.message ?: "Connection timeout",
                    data = songs
                ))
            }

            val newSongs = musicDao.getAllSongs().map { it.toSong() }
            emit(Resource.Success(newSongs))
        }
    }

    override fun getFeaturedSong(): Flow<Resource<Song>> = flow {
        emit(Resource.Loading())
        try {
            val response = client.get<MusicApiDtoSingle>("$endpointUrl/featured").music.toSong()
            emit(Resource.Success(response))
        } catch (e: UnresolvedAddressException) {
            emit(Resource.Error("Please check your internet connection"))
        } catch (e: ClientRequestException) {
            emit(Resource.Error(e.message))
        } catch (e: ServerResponseException) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        } catch (e: ConnectTimeoutException) {
            emit(Resource.Error(e.message ?: "Connection timeout"))
        }
    }

    override suspend fun updateSong(song: MusicDbEntity): Resource<String> {
        musicDao.setFavoriteSong(song)
        return Resource.Success("Success")
    }

    override suspend fun updateDuration(
        mediaId: String,
        updateSongDto: UpdateSongDto
    ): Flow<Resource<MusicApiDtoSingle>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = client.put<MusicApiDtoSingle>(
                    "$endpointUrl/mediaId"
                ) {
                    contentType(ContentType.Application.Json)
                    body = updateSongDto
                    method = HttpMethod.Put
                }
                emit(Resource.Success(response))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }
}

private const val TAG = "SongRepository"