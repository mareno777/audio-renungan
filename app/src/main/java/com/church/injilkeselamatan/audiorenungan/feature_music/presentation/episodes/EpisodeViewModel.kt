package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes

import android.os.Bundle
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.SongUseCases
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.id
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlayEnabled
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPrepared
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.SongsState
import com.google.android.exoplayer2.offline.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val songUseCases: SongUseCases,
    private val downloadManager: DownloadManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    var downloadedLength = MutableLiveData(0f)
    var maxProgress = MutableLiveData(0f)

    private val _state = mutableStateOf(SongsState())
    val state: State<SongsState> = _state

    private val _downloadState = mutableStateOf(SongsState())
    val downloadState: State<SongsState> = _downloadState

    private var job: Job? = null
    private var downloadedJob: Job? = null
    private var downloadJob: Job? = null

    var currentSelectedAlbum: String? = null

    init {
        savedStateHandle.get<String>("album")?.let { album ->
            currentSelectedAlbum = album
        }
        loadEpisodes()
        loadDownloadedEpisodes()
    }

    private fun loadEpisodes() {
        job?.cancel()
        job = songUseCases.getSongs(currentSelectedAlbum).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { episodes ->
                        _state.value = state.value.copy(
                            songs = episodes,
                            isLoading = false,
                            errorMessage = null
                        )
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
        }.launchIn(viewModelScope)
    }

    private fun loadDownloadedEpisodes() {
        downloadedJob?.cancel()
        downloadedJob = songUseCases.getDownloadedSongs(currentSelectedAlbum).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { episodes ->
                        _downloadState.value = downloadState.value.copy(
                            songs = episodes,
                            isLoading = false,
                            errorMessage = null
                        )
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
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: EpisodesEvent) {
        when (event) {
            is EpisodesEvent.DownloadEpisode -> {
                downloadSong(event.song.id)
                onDownloadEvent(event.song.title)
            }
            is EpisodesEvent.PlayToogle -> {
                playMediaId(event.episode.id)
            }
        }
    }

    private fun onDownloadEvent(title: String) {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            var onDownloadEpisode = state.value.songs.find { it.title == title } ?: return@launch
            try {
                while (true) {
                    delay(100L)
                    val download = downloadManager.currentDownloads[0]
                    maxProgress.postValue(download.contentLength.toFloat())
                    downloadedLength.postValue(download.bytesDownloaded.toFloat())
                }
            } catch (e: IndexOutOfBoundsException) {
                onDownloadEpisode = onDownloadEpisode.copy(isFavorite = true)
                songUseCases.updateSong(onDownloadEpisode)
                loadDownloadedEpisodes()
                job?.cancel()
            }
        }
    }

    private fun playMedia(mediaItem: Song, pauseAllowed: Boolean = true) {
        val nowPlaying = musicServiceConnection.nowPlaying.value

        val transportControls = musicServiceConnection.transportControls
        val isPrepared = musicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.id == nowPlaying?.id) {
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
            transportControls.playFromMediaId(mediaItem.id, null)
        }
    }

    private fun playMediaId(mediaId: String) {

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

    private fun downloadSong(mediaId: String) {
        val bundle = Bundle().apply {
            putString(MEDIA_METADATA_COMPAT_FOR_DOWNLOAD, mediaId)
        }

        musicServiceConnection.sendCommand("download_song", bundle)
    }
}

private const val TAG = "EpisodeViewModel"
const val MEDIA_METADATA_COMPAT_FOR_DOWNLOAD =
    "com.church.injilkeselamatan.audiorenungan.bundles.mediametadata"