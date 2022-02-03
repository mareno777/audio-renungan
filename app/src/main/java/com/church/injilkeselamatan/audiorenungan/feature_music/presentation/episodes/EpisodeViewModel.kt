package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.SongUseCases
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.MusicServiceConnection
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.download.DownloadListener
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.*
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.SongsState
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val songUseCases: SongUseCases,
    private val downloadManager: DownloadManager,
    private val downloadListener: DownloadListener,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val downloadedLength = MutableStateFlow(0f)
    val maxProgress = MutableStateFlow(1f)

    private val _state = mutableStateOf(SongsState<MediaMetadataCompat>())
    val state: State<SongsState<MediaMetadataCompat>> = _state

    private val _downloadState = mutableStateOf(SongsState<Song>())
    val downloadState: State<SongsState<Song>> = _downloadState

    private var loadEpisodeJob: Job? = null
    private var downloadedJob: Job? = null
    private var downloadingJob: Job? = null

    lateinit var currentSelectedAlbum: String

    init {
        savedStateHandle.get<String>("album")?.let { album ->
            currentSelectedAlbum = album
        }
        loadEpisodes()
        loadDownloadedEpisodes()
        initDownloadEvent(true)
    }

    private fun loadEpisodes() {
        loadEpisodeJob?.cancel()
        loadEpisodeJob =
            songUseCases.getSongs.getMediaMetadataCompats(currentSelectedAlbum).onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { episodes ->
                            _state.value = state.value.copy(
                                songs = episodes,
                                isLoading = false,
                                errorMessage = ""
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = true,
                            errorMessage = ""
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

    fun playingMetadata(): StateFlow<MediaMetadataCompat> {
        return musicServiceConnection.nowPlaying.asStateFlow()
    }

    fun currentPlaybackstate(): StateFlow<PlaybackStateCompat> {
        return musicServiceConnection.playbackState.asStateFlow()
    }

    fun loadDownloadedEpisodes() {
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
        }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: EpisodesEvent) {
        when (event) {
            is EpisodesEvent.DownloadEpisode -> {
                downloadSong(
                    mediaId = event.song.id!!,
                    mediaUri = event.song.mediaUri.toString(),
                    title = event.song.title!!
                )
                initDownloadEvent()
            }
            is EpisodesEvent.RemoveDownloadedEpisode -> {
                Log.d(TAG, "mediaId to remove: ${event.song.id}")
                event.song.id?.let {
                    songUseCases.removeDownloadedSong(it)
                }
            }
            is EpisodesEvent.PlayToogle -> {
                playMediaId(event.episode.id!!)
            }
            is EpisodesEvent.PlayOrPause -> {
                if (event.isPlay) {
                    musicServiceConnection.transportControls.play()
                } else {
                    musicServiceConnection.transportControls.play()
                }
            }
        }
    }

    private fun initDownloadEvent(firstTime: Boolean = false) {
        if (firstTime) {
            songUseCases.downloadSong.initiateService()
        }
        downloadingJob?.cancel()
        downloadingJob = viewModelScope.launch {
            try {
                while (true) {
                    delay(100L)
                    val download = downloadManager.currentDownloads[0]
                    maxProgress.emit(download.contentLength.toFloat())
                    downloadedLength.emit(download.bytesDownloaded.toFloat())
                    loadDownloadedEpisodes()
                }
            } catch (e: IndexOutOfBoundsException) {
                loadDownloadedEpisodes()
                downloadingJob?.cancel()
            }
        }
    }

    fun onState(songId: String): Int? {
        return downloadManager.currentDownloads.find { download ->
            download.request.id == songId
        }?.state
    }

    fun onDownloadComplated(): StateFlow<Download?> {
        return downloadListener.downloadComplated.asStateFlow()
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

    private fun downloadSong(mediaId: String, mediaUri: String, title: String) {
        songUseCases.downloadSong(mediaId, mediaUri, title)
    }
}

private const val TAG = "EpisodeViewModel"