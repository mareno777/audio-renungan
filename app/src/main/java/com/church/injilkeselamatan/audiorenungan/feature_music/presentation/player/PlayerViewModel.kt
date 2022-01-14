package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.media.MediaMetadataCompat
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil.ImageLoader
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
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
    private val getSongUseCases: SongUseCases,
    private val imageLoader: ImageLoader
) : ViewModel() {


    val playbackState = musicServiceConnection.playbackState.asStateFlow()
    val nowPlaying = musicServiceConnection.nowPlaying.asStateFlow()

    private val _curSongDuration = MutableStateFlow(0L)
    val curSongDuration = _curSongDuration.asStateFlow()

    private val _songs = mutableStateOf(SongsState<MediaMetadataCompat>())
    val songs: State<SongsState<MediaMetadataCompat>> = _songs

    private val _curSongIndex = MutableStateFlow(-1)
    val curSongIndex = _curSongIndex.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadSongs()
    }

    private fun loadSongs() {
        loadingJob?.cancel()
        loadingJob = getSongUseCases.getSongs.getMediaMetadataCompats().onEach { resource ->
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

    fun getImageLoader(): ImageLoader = imageLoader

    private fun playMediaId(mediaId: String) {

        val transportControls = musicServiceConnection.transportControls
        val isPrepared = playbackState.value.isPrepared
        if (isPrepared && mediaId == nowPlaying.value.id) {
            when {
                playbackState.value.isPlaying -> transportControls.pause()
                playbackState.value.isPlayEnabled -> transportControls.play()
                else -> {

                }
            }

        } else {
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
            is PlayerEvents.FastForward -> {

            }
            is PlayerEvents.FastRewind -> {

            }
            is PlayerEvents.SetSleepTimer -> {

            }
        }
    }

    fun updateCurrentPlayingPosition(): Flow<Long> = flow {
        while (true) {
            val currentPlaybackPosition = playbackState.value.currentPlayBackPosition
            _curSongDuration.emit(MusicService.curSongDuration)
            _curSongIndex.emit(MusicService.curSongIndex)
            emit(currentPlaybackPosition)
            delay(100L)
        }
    }
}

fun calculateColorPalette(drawable: Drawable, onFinised: (Color) -> Unit) {
    val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
    Palette.from(bitmap).generate { palette ->
        palette?.darkVibrantSwatch?.rgb?.let { colorValue ->
            onFinised(Color(colorValue))
        }
    }
}

private const val TAG = "PlayerViewModel"