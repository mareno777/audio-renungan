package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.SongUseCases
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.NOTHING_PLAYING
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.PersistentStorage
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.*
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.MusicSource
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.UAMP_ALBUMS_ROOT
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.FeaturedSongState
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.SongsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val savedSong: PersistentStorage,
    private val songUseCases: SongUseCases
) : ViewModel() {

    var mediaId by mutableStateOf(UAMP_ALBUMS_ROOT)

    private val _state = mutableStateOf(SongsState<Song>())
    val state: State<SongsState<Song>> = _state

    private val _featuredState = mutableStateOf<FeaturedSongState<Song>>(FeaturedSongState())
    val featuredState: State<FeaturedSongState<Song>> = _featuredState

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }

    private var getSongsJob: Job? = null
    private var getFeaturedSongJob: Job? = null

    private val _recentSong = mutableStateOf(NOTHING_PLAYING)
    val recentSong: State<MediaMetadataCompat> = _recentSong

    private val subscriptionCallback =
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                Log.d(TAG, "callback: $parentId")
            }

            override fun onError(parentId: String, options: Bundle) {
                Log.d(TAG, "callback: error $parentId")
                super.onError(parentId, options)
            }
        }

    init {
        loadSongs()
        loadFeaturedSong()
    }

    private fun loadRecentSong() {
        viewModelScope.launch {
            _recentSong.value = savedSong.loadRecentSong().first()
        }
    }

    fun playingMetadata(): StateFlow<MediaMetadataCompat> {
        return musicServiceConnection.nowPlaying
    }

    fun playbackState(): StateFlow<PlaybackStateCompat> {
        return musicServiceConnection.playbackState
    }

    fun onEvent(event: AlbumsEvent) {
        val transportControls = musicServiceConnection.transportControls
        Log.d(TAG, "NowPlaying: ${musicServiceConnection.nowPlaying.value.title}")
        when (event) {
            is AlbumsEvent.PlayOrPause -> {
                if (event.isPlay) {
                    transportControls.play()
                } else {
                    transportControls.pause()
                }
            }
            is AlbumsEvent.PlayFeatured -> {
                playMediaId(featuredState.value.song?.id!!)
            }
        }
    }

    private fun playMediaId(mediaId: String) {

        val transportControls = musicServiceConnection.transportControls
        val isPrepared = musicServiceConnection.playbackState.value.isPrepared
        Log.d(TAG, "mediaId: $mediaId $isPrepared")
        if (isPrepared && mediaId == musicServiceConnection.nowPlaying.value.id) {
            when {
                musicServiceConnection.playbackState.value.isPlaying -> transportControls.pause()
                musicServiceConnection.playbackState.value.isPlayEnabled -> transportControls.play()
                else -> {
                    throw IllegalAccessException("playbackState on unknown state")
                }
            }

        } else {
            transportControls.playFromMediaId(mediaId, null)
        }
    }

    fun loadSongs() {
        getSongsJob?.cancel()
        val isPrepared = musicServiceConnection.playbackState.value.isPrepared
        getSongsJob = songUseCases.getSongs().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.sortedBy { it.id }?.distinctBy { data ->
                        data.album
                    }?.let { albums ->
                        _state.value = state.value.copy(
                            songs = albums,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    Log.d(TAG, "isPrepared: $isPrepared")
                    if (!isPrepared) {
                        loadRecentSong()
                        musicServiceConnection.sendCommand("connect", null)
                        musicServiceConnection.subscribe(mediaId, subscriptionCallback)
                        Log.d(TAG, "subscribe, $mediaId")
                    }
                    Log.d(TAG, "loadSongs success")
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(
                        songs = resource.data ?: emptyList(),
                        isLoading = true
                    )
                    Log.d(TAG, "loadSongs loading")
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        songs = resource.data ?: emptyList(),
                        isLoading = false,
                        errorMessage = resource.message
                    )
                    _eventFlow.emit(UIEvent.ShowSnackbar(resource.message))
                    Log.d(TAG, "loadSongs error")
                }

            }
            if (musicServiceConnection.nowPlaying.value == NOTHING_PLAYING) {
                savedSong.loadRecentSong().first()
            }

        }.launchIn(viewModelScope)
    }


    fun loadFeaturedSong() {
        getFeaturedSongJob?.cancel()
        getFeaturedSongJob = viewModelScope.launch {
            songUseCases.getFeaturedSong().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _featuredState.value = featuredState.value.copy(
                            song = null,
                            isLoading = true
                        )
                    }
                    is Resource.Success -> {
                        _featuredState.value = featuredState.value.copy(
                            song = resource.data,
                            isLoading = false,
                            errorMessage = null
                        )
                        Log.d(TAG, "${resource.data}")
                    }
                    is Resource.Error -> {
                        _featuredState.value = featuredState.value.copy(
                            song = null,
                            isLoading = false,
                            errorMessage = resource.message
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(mediaId, subscriptionCallback)
    }
}

private const val TAG = "AlbumViewModel"