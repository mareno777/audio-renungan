package com.church.injilkeselamatan.audiorenungan.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.church.injilkeselamatan.audiorenungan.data.SongRepository
import com.church.injilkeselamatan.audiorenungan.data.models.MediaItemData
import com.church.injilkeselamatan.audiorenungan.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.id
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPlayEnabled
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.isPrepared
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.library.UAMP_BROWSABLE_ROOT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val songRepository: SongRepository
) : ViewModel() {

    val playbackStateCompat = musicServiceConnection.playbackState
    val mediaMetadataCompat = musicServiceConnection.nowPlaying
    val transportControls by lazy {
        musicServiceConnection.transportControls
    }

    init {
        musicServiceConnection.subscribe(
            UAMP_BROWSABLE_ROOT,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                }
            })

        musicServiceConnection.sendCommand(UAMP_BROWSABLE_ROOT, null)
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

    fun subscribeToMediaBrowserService(parentId: String) {
        viewModelScope.launch {
            songRepository.load()
        }
        musicServiceConnection.subscribe(parentId, object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                Log.e("HomeViewModel", "children: $children")
            }

            override fun onError(parentId: String) {
                super.onError(parentId)
                Log.e("HomeViewModel", "onError: $parentId")
            }
        })
    }
}