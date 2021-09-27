package com.church.injilkeselamatan.audiorenungan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.data.SongRepository
import com.church.injilkeselamatan.audiorenungan.data.models.MusicX
import com.church.injilkeselamatan.audiorenungan.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.MusicService
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.*
import com.church.injilkeselamatan.audiorenungan.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor
    (
    private val musicServiceConnection: MusicServiceConnection,
    private val songRepository: SongRepository
) :
    ViewModel() {

    val playbackStateCompat = musicServiceConnection.playbackState
    val mediaMetadataCompat = musicServiceConnection.nowPlaying
    val transportControls = musicServiceConnection.transportControls

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    private val _songs: MutableLiveData<List<MusicX>> = MutableLiveData()
    val songs: LiveData<List<MusicX>> = _songs

    private val _curSongIndex: MutableLiveData<Int> = MutableLiveData()
    val curSongIndex: LiveData<Int> = _curSongIndex


    init {
        updateCurrentPlayerPosition()
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

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            songRepository.getSongs().collect { resource ->
                when (resource) {
                    is Resource.Success -> _songs.postValue(resource.data)
                    is Resource.Loading -> Unit // Show loading indicator
                    is Resource.Error -> Unit //Show error message
                }

            }

            //_songs.postValue(result)

            while (true) {
                val pos = playbackStateCompat.value?.currentPlayBackPosition ?: 0L
                if (curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                    _curSongIndex.postValue(MusicService.curSongIndex)
                }
                delay(100L)
            }
        }
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }
}