package com.church.injilkeselamatan.audio_presentation.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.church.injilkeselamatan.audio_domain.use_case.ServiceSongState
import com.church.injilkeselamatan.audio_domain.use_case.SongUseCases
import com.church.injilkeselamatan.audio_presentation.SongsState
import com.church.injilkeselamatan.core.MusicServiceConnection
import com.church.injilkeselamatan.core.NOTHING_PLAYING
import com.church.injilkeselamatan.core.util.Resource
import com.church.injilkeselamatan.core.util.extensions.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val songUseCases: SongUseCases,
    private val serviceSongState: ServiceSongState,
    private val imageLoader: ImageLoader
) : ViewModel() {


    private val _songs = mutableStateOf(SongsState<MediaMetadataCompat>())
    val songs: State<SongsState<MediaMetadataCompat>> = _songs

    private val _recentSong = MutableStateFlow(NOTHING_PLAYING)
    val recentSong = _recentSong.asStateFlow()

    private val _currentPlaybackPosition = MutableStateFlow(0L)
    val currentPlaybackPosition = _currentPlaybackPosition.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadRecentSong()
        loadSongs()
        updateCurrentPlaybackPosition()
    }

    fun playingMediaMetadata(): StateFlow<MediaMetadataCompat> {
        return musicServiceConnection.nowPlaying
    }

    fun playbackState(): StateFlow<PlaybackStateCompat> {
        return musicServiceConnection.playbackState
    }

    fun currentSongDuration() = serviceSongState.curSongDuration

    fun currentSongIndex() = serviceSongState.curSongIndex

    private fun loadRecentSong() {
        viewModelScope.launch {
            _recentSong.emit(songUseCases.loadRecentSong())
        }
    }

    private fun loadSongs() {
        loadingJob?.cancel()
        loadingJob = songUseCases.getSongs.getMediaMetadataCompats().onEach { resource ->

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
//            if (musicServiceConnection.nowPlaying.value == NOTHING_PLAYING) {
//                songUseCases.loadRecentSong().first()
//            }
        }.launchIn(viewModelScope)
    }

    fun getImageLoader(): ImageLoader = imageLoader

    private fun playMediaId(mediaId: String) {

        val transportControls = musicServiceConnection.transportControls
        val isPrepared = playbackState().value.isPrepared
        if (isPrepared && mediaId == musicServiceConnection.nowPlaying.value.id) {
            when {
                playbackState().value.isPlaying -> transportControls.pause()
                playbackState().value.isPlayEnabled -> transportControls.play()
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
                val currPlaybackPosition = currentPlaybackPosition.value
                musicServiceConnection.transportControls.seekTo(currPlaybackPosition + 10_000L)
            }
            is PlayerEvents.FastRewind -> {
                val currPlaybackPosition = currentPlaybackPosition.value
                musicServiceConnection.transportControls.seekTo(currPlaybackPosition - 10_000L)
            }
            is PlayerEvents.SetSleepTimer -> Unit
        }
    }

    private fun updateCurrentPlaybackPosition() {
        viewModelScope.launch {
            do {
                _currentPlaybackPosition.emit(playbackState().value.currentPlayBackPosition)
                delay(100)
            } while (true)
        }
    }

//    fun calculateColorPalette(drawable: Drawable, onFinised: (Color) -> Unit) {
//        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
//        Palette.from(bitmap).generate { palette ->
//            palette?.darkVibrantSwatch?.rgb?.let { colorValue ->
//                onFinised(Color(colorValue))
//            }
//        }
//    }
}

private const val TAG = "PlayerViewModel"