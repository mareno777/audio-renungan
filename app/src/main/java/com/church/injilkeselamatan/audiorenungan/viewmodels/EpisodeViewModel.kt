package com.church.injilkeselamatan.audiorenungan.viewmodels

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.data.SongRepository
import com.church.injilkeselamatan.audiorenungan.data.models.MediaItemData
import com.church.injilkeselamatan.audiorenungan.data.models.MusicX
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.id
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPlayEnabled
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPrepared
import com.church.injilkeselamatan.audiorenungan.uamp.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val repository: SongRepository,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    var songList = mutableStateOf<List<MusicX>>(listOf())

//    val rootMediaId: LiveData<String> =
//        Transformations.map(musicServiceConnection.isConnected) { isConnected ->
//            if (isConnected) {
//                musicServiceConnection.rootMediaId
//            } else {
//                null
//            }
//        }

    init {
        getSongs()

    }

    val playbackStateCompat = musicServiceConnection.playbackState
    val mediaMetadataCompat = musicServiceConnection.nowPlaying
    val transportControls by lazy {
        musicServiceConnection.transportControls
    }

    private fun getSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSongs().collect { resource ->
                when (resource) {
                    is Resource.Success -> songList.value = resource.data ?: emptyList()
                    else -> songList.value = emptyList()
                }
            }
        }
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

}