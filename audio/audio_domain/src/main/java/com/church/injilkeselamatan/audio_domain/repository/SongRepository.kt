package com.church.injilkeselamatan.audio_domain.repository

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import com.church.injilkeselamatan.audio_domain.model.Song
import com.church.injilkeselamatan.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface SongRepository {

    var mediaMetadataCompats: List<MediaMetadataCompat>

    fun getSongs(fromDbSource: Boolean): Flow<Resource<List<Song>>>

    fun getFeaturedSong(): Flow<Resource<Song>>

    suspend fun loadRecentSong(): MediaMetadataCompat

    fun whenReady(performAction: (Boolean) -> Unit): Boolean

    fun onSearch(query: String, extras: Bundle): List<MediaMetadataCompat>

    suspend fun updateSong(song: Song): Resource<String>

    suspend fun checkVersion(currentVersion: Int): Boolean
//    suspend fun updateDuration(
//        mediaId: String,
//        updateSongDto: UpdateSongDto
//    ): Flow<Resource<MusicApiDtoSingle>>
}