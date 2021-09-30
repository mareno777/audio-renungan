package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.church.injilkeselamatan.audiorenungan.feature_music.data.repository.SongRepositoryImpl
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.MediaItemData
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.MusicDto
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.id
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlayEnabled
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPrepared
import com.google.android.exoplayer2.offline.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val repository: SongRepository,
    private val musicServiceConnection: MusicServiceConnection,
    private val downloadManager: DownloadManager
) : ViewModel() {

    var songList = mutableStateOf<List<MusicDto>>(listOf())

    private val _mediaItems = MutableLiveData<List<MediaItemData>>()
    val mediaItems: LiveData<List<MediaItemData>> = _mediaItems


    val playbackStateCompat = musicServiceConnection.playbackState
    val mediaMetadataCompat = musicServiceConnection.nowPlaying

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


    fun subscribe(parentId: String) {
        musicServiceConnection.subscribe(parentId, subscriptionCallback)
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

    fun downloadSong(song: String) {
        val bundle = Bundle()
        bundle.putString(MEDIA_METADATA_COMPAT_FOR_DOWNLOAD, song)
        musicServiceConnection.sendCommand("download_song", bundle)
    }
}

const val MEDIA_METADATA_COMPAT_FOR_DOWNLOAD =
    "com.church.injilkeselamatan.audiorenungan.bundles.mediametadata"