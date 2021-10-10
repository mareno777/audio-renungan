package com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository

import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {

    fun getSongs(): Flow<Resource<List<Song>>>
}