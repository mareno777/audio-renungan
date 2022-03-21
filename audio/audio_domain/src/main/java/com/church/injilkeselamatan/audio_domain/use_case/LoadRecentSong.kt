package com.church.injilkeselamatan.audio_domain.use_case

import android.support.v4.media.MediaMetadataCompat
import com.church.injilkeselamatan.audio_domain.repository.SongRepository

class LoadRecentSong(private val repository: SongRepository) {

    suspend operator fun invoke(): MediaMetadataCompat {
        return repository.loadRecentSong()
    }
}