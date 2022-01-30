package com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case

import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import com.google.android.exoplayer2.offline.Download.STATE_COMPLETED
import com.google.android.exoplayer2.offline.DownloadManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetDownloadedSongs(
    private val repository: SongRepository,
    private val downloadManager: DownloadManager
) {
    operator fun invoke(album: String? = null): Flow<Resource<List<Song>>> {
        val downloadedIndex = downloadManager.downloadIndex
        return if (album != null) {
            repository.getSongs(true).map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val mutable = mutableListOf<Song>()
                        resource.data?.asSequence()?.let { songs ->
                            val filteredSongs =
                                songs.filter {
                                    it.album == album
                                }.filter {
                                    downloadedIndex.getDownload(it.id)?.state == STATE_COMPLETED
                                }
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
            repository.getSongs(false).map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val mutable = mutableListOf<Song>()
                        resource.data?.asSequence()?.let { songs ->

                            mutable.addAll(songs.filter { song ->
                                downloadManager.downloadIndex.getDownload(song.id)?.state == STATE_COMPLETED
                            })
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
        }
    }
}