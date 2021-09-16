package com.church.injilkeselamatan.audiorenungan.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.church.injilkeselamatan.audiorenungan.data.models.MediaItemData
import com.church.injilkeselamatan.audiorenungan.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.id
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPlayEnabled
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPrepared
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.library.UAMP_BROWSABLE_ROOT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
    ) :
    ViewModel() {

        val rootMediaId: LiveData<String> =
            Transformations.map(musicServiceConnection.isConnected) { isConnected ->
                if (isConnected) musicServiceConnection.rootMediaId else null
            }
    init {
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

    override fun onCleared() {
    }
}