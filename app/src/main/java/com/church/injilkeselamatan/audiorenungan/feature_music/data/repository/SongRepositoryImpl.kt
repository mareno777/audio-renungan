package com.church.injilkeselamatan.audiorenungan.feature_music.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.toSong
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.SongsApi
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.toMusicDb
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.networkBoundResource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SongRepositoryImpl @Inject constructor(
    private val songsApi: SongsApi,
    private val musicDatabase: MusicDatabase
) : SongRepository {

    private val musicDao = musicDatabase.musicDao()

    override fun getSongs(): Flow<Resource<List<Song>>> {
        return networkBoundResource(
            query = {
                musicDao.getAllSongs().map { musicDb ->
                    musicDb.map {
                        it.toSong()
                    }
                }

            },
            fetch = {
                Log.d(TAG, "fetching")
                songsApi.getSongs().music
            },
            saveFetchResult = { musicDtos ->
                Log.d(TAG, "saving result from server")
                val musicDbs = musicDtos.map {
                    it.toMusicDb()
                }
                musicDatabase.withTransaction {
                    musicDao.clearAll()
                    musicDao.insertSongs(musicDbs)
                }
            },
            shouldFetch = {
                Log.d(TAG, "shouldFetch: ${it.isEmpty()}")
                it.isEmpty()
            }
        )
    }

    override suspend fun updateSong(song: MusicDbEntity): Resource<String> {
        musicDao.setFavoriteSong(song)
        return Resource.Success("Success")
    }
}

private const val TAG = "SongRepository"