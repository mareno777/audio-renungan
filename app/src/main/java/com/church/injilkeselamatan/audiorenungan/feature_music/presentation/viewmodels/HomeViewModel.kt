package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.MediaItemData
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.id
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlayEnabled
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPrepared
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.UAMP_ALBUMS_ROOT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val songRepository: SongRepository
) : ViewModel() {

    var mediaId by mutableStateOf(UAMP_ALBUMS_ROOT)

    val playbackStateCompat = musicServiceConnection.playbackState
    val mediaMetadataCompat = musicServiceConnection.nowPlaying

    private val _mediaItems = MutableLiveData<List<MediaItemData>>()
    val mediaItems: LiveData<List<MediaItemData>> = _mediaItems

    val networkError = Transformations.map(musicServiceConnection.networkFailure) { it }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            val itemList = children.map { child ->
                val subtitle = child.description.subtitle ?: ""
                MediaItemData(
                    child.mediaId!!,
                    child.description.title.toString(),
                    subtitle.toString(),
                    child.description.iconUri!!,
                    child.isBrowsable,
                    0 // we fix later for indicator now playing
                )
            }
            _mediaItems.postValue(itemList)
        }
    }

    init {
        musicServiceConnection.subscribe(mediaId, subscriptionCallback)
        loadSongs()
    }

    fun playMedia(mediaItem: MediaItemData, pauseAllowed: Boolean = true) {
        val nowPlaying = musicServiceConnection.nowPlaying.value

        val transportControls = musicServiceConnection.transportControls
        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId == nowPlaying?.id) {
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        if (pauseAllowed) transportControls.pause() else Unit
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        // Something wrong
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    fun playMediaId(mediaId: String) {

        val nowPlaying = musicServiceConnection.nowPlaying.value

        val transportControls = musicServiceConnection.transportControls
        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaId == nowPlaying?.id) {
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        // Something wrong
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaId, null)
        }
    }

    fun sendCommand(command: String) {
        musicServiceConnection.sendCommand(command, null)
    }

    fun play() = musicServiceConnection.transportControls.play()
    fun pause() = musicServiceConnection.transportControls.pause()

    private fun loadSongs() {
        viewModelScope.launch {
            songRepository.getSongs().collectLatest {
                Log.e("HomeViewModel", it.data?.toString() ?: it.message.toString())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(mediaId, subscriptionCallback)
    }
}