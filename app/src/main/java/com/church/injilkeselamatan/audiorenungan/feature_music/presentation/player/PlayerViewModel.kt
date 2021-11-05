package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.SongUseCases
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.MusicService
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.*
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.SongsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val getSongUseCases: SongUseCases
) : ViewModel() {


    val playbackStateCompat = musicServiceConnection.playbackState.asStateFlow()
    val mediaMetadataCompat = musicServiceConnection.nowPlaying.asStateFlow()

    private val _curSongDuration = MutableStateFlow(0L)
    val curSongDuration = _curSongDuration.asStateFlow()

    private val _songs = mutableStateOf(SongsState())
    val songs: State<SongsState> = _songs

    private val _curSongIndex = MutableStateFlow(-1)
    val curSongIndex = _curSongIndex.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadSongs()
    }

    private fun loadSongs() {
        loadingJob?.cancel()
        loadingJob = getSongUseCases.getSongs().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        _songs.value = songs.value.copy(
                            songs = it,
                            isLoading = false,
                        )
                    }

                }
                is Resource.Loading -> {
                    _songs.value = songs.value.copy(
                        isLoading = true,
                    )
                }
                is Resource.Error -> {
                    _songs.value = songs.value.copy(
                        isLoading = false,
                        errorMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun playMediaId(mediaId: String) {

        val nowPlaying = musicServiceConnection.nowPlaying.value

        val transportControls = musicServiceConnection.transportControls
        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaId == nowPlaying?.id) {
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {

                    }
                }
            }
        } else {
            Log.d(TAG, "playFromMediaId: $mediaId")
            transportControls.playFromMediaId(mediaId, null)
        }
    }

    fun onEvent(event: PlayerEvents) {
        when (event) {
            is PlayerEvents.PlayFromMediaId -> playMediaId(event.mediaId)
            is PlayerEvents.PlayOrPause -> {
                if (event.isPlay) {
                    musicServiceConnection.transportControls.play()
                } else {
                    musicServiceConnection.transportControls.pause()
                }
            }
            is PlayerEvents.SkipToNext -> {
                musicServiceConnection.transportControls.skipToNext()
            }
            is PlayerEvents.SkipToPrevious -> {
                musicServiceConnection.transportControls.skipToPrevious()
            }
            is PlayerEvents.SeekTo -> {
                musicServiceConnection.transportControls.seekTo(event.position)
            }
        }
    }

    fun currentPlayingPosition(): Flow<Long> = flow {
        while (true) {
            val pos = playbackStateCompat.value?.currentPlayBackPosition ?: 0L
            _curSongDuration.emit(MusicService.curSongDuration)
            _curSongIndex.emit(MusicService.curSongIndex)
            emit(pos)
            delay(100L)
        }
    }
}

private const val TAG = "PlayerViewModel"