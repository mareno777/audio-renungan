package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player

import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.MusicService
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor
    (
    private val musicServiceConnection: MusicServiceConnection
) :
    ViewModel() {

    val playbackStateCompat = musicServiceConnection.playbackState
    val mediaMetadataCompat = musicServiceConnection.nowPlaying

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    private val _songs: MutableLiveData<List<MediaMetadataCompat>> = MutableLiveData()
    val songs: LiveData<List<MediaMetadataCompat>> = _songs

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
            while (true) {
                val pos = playbackStateCompat.value?.currentPlayBackPosition ?: 0L

                if (curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                    _curSongIndex.postValue(MusicService.curSongIndex)
                    Log.e("PlayerViewModel", "currentPosition: ${_curSongIndex.value}")
                }
                delay(100L)
            }
        }
    }

    fun play() = musicServiceConnection.transportControls.play()
    fun pause() = musicServiceConnection.transportControls.pause()
    fun skipToPrevious() = musicServiceConnection.transportControls.skipToPrevious()
    fun skipToNext() = musicServiceConnection.transportControls.skipToNext()
    fun seekTo(pos: Long) = musicServiceConnection.transportControls.seekTo(pos)
}