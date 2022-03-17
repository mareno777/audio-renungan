package com.church.injilkeselamatan.audio_domain.use_case

import com.church.injilkeselamatan.audio_domain.repository.SongRepository

class CheckVersion(private val songRepository: SongRepository) {

    suspend fun isLatestVersion(currentVersion: Int): Boolean {
        return songRepository.checkVersion(currentVersion)
    }
}