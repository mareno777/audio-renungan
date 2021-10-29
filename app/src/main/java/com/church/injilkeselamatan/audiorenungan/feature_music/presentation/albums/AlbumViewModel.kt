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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.SongUseCases
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.NOTHING_PLAYING
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.PersistentStorage
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPrepared
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.title
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.UAMP_ALBUMS_ROOT
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.SongsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val savedSong: PersistentStorage,
    private val songUseCases: SongUseCases
) : ViewModel() {

    var mediaId by mutableStateOf(UAMP_ALBUMS_ROOT)

    private val _state = mutableStateOf(SongsState())
    val state: State<SongsState> = _state

    private var getSongsJob: Job? = null

    private val _recentSong = MutableLiveData(NOTHING_PLAYING)
    val recentSong: LiveData<MediaMetadataCompat> = _recentSong

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
    }

    suspend fun loadRecentSong() {
        _recentSong.postValue(savedSong.loadRecentSong().first())
    }

    fun playingMetadata(): LiveData<MediaMetadataCompat> {
        return musicServiceConnection.nowPlaying
    }

    fun playbackState(): LiveData<PlaybackStateCompat> {

        return musicServiceConnection.playbackState
    }

    fun onEvent(event: AlbumsEvent) {
        val transportControls = musicServiceConnection.transportControls
        Log.d(TAG, "NowPlaying: ${musicServiceConnection.nowPlaying.value?.title.toString()}")
        when (event) {
            is AlbumsEvent.PlayOrPause -> {
                if (event.isPlay) {
                    transportControls.play()
                } else {
                    transportControls.pause()
                }
            }
        }
    }

    fun loadSongs() {

        Log.d(TAG, "NowPlaying: ${musicServiceConnection.nowPlaying.value?.title.toString()}")
        getSongsJob?.cancel()
        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared
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
                    if (isPrepared == false || isPrepared == null) {
                        musicServiceConnection.sendCommand("connect", null)
                        musicServiceConnection.subscribe(mediaId, subscriptionCallback)
                        Log.d(TAG, "subscribe, $mediaId")
                    }
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        errorMessage = resource.message
                    )
                }

            }
            if (playingMetadata().value == null) {
                savedSong.loadRecentSong().first()
            }

        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(mediaId, subscriptionCallback)
    }
}

private const val TAG = "AlbumViewModel"