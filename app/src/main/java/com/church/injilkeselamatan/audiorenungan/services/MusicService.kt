package com.church.injilkeselamatan.audiorenungan.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.MediaBrowserServiceCompat.BrowserRoot.EXTRA_RECENT
import com.church.injilkeselamatan.audio_data.data_source.local.PREFERENCES_POSITION
import com.church.injilkeselamatan.audio_data.data_source.local.PersistentStorage
import com.church.injilkeselamatan.audio_domain.helper.BrowseTree
import com.church.injilkeselamatan.audio_domain.helper.MEDIA_SEARCH_SUPPORTED
import com.church.injilkeselamatan.audio_domain.helper.UAMP_BROWSABLE_ROOT
import com.church.injilkeselamatan.audio_domain.helper.UAMP_RECENT_ROOT
import com.church.injilkeselamatan.audio_domain.repository.SongRepository
import com.church.injilkeselamatan.audio_domain.use_case.ServiceSongState
import com.church.injilkeselamatan.audio_presentation.NotificationManager
import com.church.injilkeselamatan.core.NETWORK_FAILURE
import com.church.injilkeselamatan.core.util.extensions.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.gms.cast.framework.CastContext
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject


@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    private lateinit var notificationManager: NotificationManager

    // The current player will either be an ExoPlayer (for local playback) or a CastPlayer (for
    // remote playback through a Cast device).
    private lateinit var currentPlayer: Player

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()

    @Inject
    lateinit var storage: PersistentStorage

    @Inject
    lateinit var cachedDataSourceFactory: DataSource.Factory

    @Inject
    lateinit var songRepository: SongRepository

    @Inject
    lateinit var serviceSongState: ServiceSongState

    /**
     * This must be `by lazy` because the source won't initially be ready.
     * See [MusicService.onLoadChildren] to see where it's accessed (and first
     * constructed).
     */
    @Inject
    lateinit var browseTree: BrowseTree

    private var recentSong: MediaMetadataCompat? = null

    private var isForegroundService = false

    private val playerListener = PlayerEventListener()

    @Inject
    lateinit var exoPlayer: ExoPlayer

    /**
     * If Cast is available, create a CastPlayer to handle communication with a Cast session.
     */
    private val castPlayer: CastPlayer? by lazy {
        try {
            val castContext = CastContext.getSharedInstance(this)
            CastPlayer(castContext).apply {
                setSessionAvailabilityListener(UampCastSessionAvailabilityListener())
                addListener(playerListener)
            }
        } catch (e: Exception) {
            // We wouldn't normally catch the generic `Exception` however
            // calling `CastContext.getSharedInstance` can throw various exceptions, all of which
            // indicate that Cast is unavailable.
            // Related internal bug b/68009560.
            Log.i(
                TAG, "Cast is not available on this device. " +
                        "Exception thrown when attempting to obtain CastContext. " + e.message
            )
            null
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate() {
        super.onCreate()

        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.getActivity(this, 0, sessionIntent, PendingIntent.FLAG_IMMUTABLE)
                } else {
                    PendingIntent.getActivity(
                        this,
                        0,
                        sessionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            }

        // Create a new MediaSession.
        mediaSession = MediaSessionCompat(this, TAG)
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }

        /**
         * In order for [MediaBrowserCompat.ConnectionCallback.onConnected] to be called,
         * a [MediaSessionCompat.Token] needs to be set on the [MediaBrowserServiceCompat].
         *
         * It is possible to wait to set the session token, if required for a specific use-case.
         * However, the token *must* be set by the time [MediaBrowserServiceCompat.onGetRoot]
         * returns, or the connection will fail silently. (The system will not even call
         * [MediaBrowserCompat.ConnectionCallback.onConnectionFailed].)
         */
        sessionToken = mediaSession.sessionToken

        /**
         * The notification manager will use our player and media session to decide when to post
         * notifications. When notifications are posted or removed our listener will be called, this
         * allows us to promote the service to foreground (required so that we're not killed if
         * the main UI is not visible).
         */
        notificationManager = NotificationManager(
            this,
            mediaSession.sessionToken,
            PlayerNotificationListener()
        ) {
            //on playback change
            serviceSongState.curSongDuration.value = currentPlayer.duration
            serviceSongState.curSongIndex.value = currentPlayer.currentMediaItemIndex
            if (!mediaSession.isActive) {
                mediaSession.isActive = true
            }
        }

        // The media library is built from a remote JSON file. We'll create the source here,
        // and then use a suspend function to perform the download off the main thread.

        recentSong = storage.loadRecentSong()


        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(UampPlaybackPreparer())
        mediaSessionConnector.setEnabledPlaybackActions(
            PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_SEEK_TO
        )
        mediaSessionConnector.setQueueNavigator(UampQueueNavigator(mediaSession))

        switchToPlayer(
            previousPlayer = null,
            newPlayer = if (castPlayer?.isCastSessionAvailable == true) castPlayer!! else exoPlayer
        )
        notificationManager.showNotificationForPlayer(currentPlayer)
    }

    /**
     * This is the code that causes UAMP to stop playing when swiping the activity away from
     * recents. The choice to do this is app specific. Some apps stop playback, while others allow
     * playback to continue and allow users to stop it with the notification.
     */
    override fun onTaskRemoved(rootIntent: Intent) {
        saveRecentSongToStorage()
        super.onTaskRemoved(rootIntent)

        currentPlayer.stop()
        currentPlayer.clearMediaItems()

        mediaSession.isActive = false

    }

    override fun onDestroy() {
        Log.i(TAG, "SERVICE DESTROYED")
        super.onDestroy()
//        mediaSession.run {
//            isActive = false
//            release()
//        }
//
//        // Cancel coroutines when the service is going away.
//        serviceJob.cancel()
//
//        // Free ExoPlayer resources.
//        exoPlayer.removeListener(playerListener)
//        exoPlayer.release()
    }

    /**
     * Returns the "root" media ID that the client should request to get the list of
     * [MediaItem]s to browse/play.
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {

        /*
         * By default, all known clients are permitted to search, but only tell unknown callers
         * about search if permitted by the [BrowseTree].
         */
        val rootExtras = Bundle().apply {
            putBoolean(
                MEDIA_SEARCH_SUPPORTED,
                browseTree.searchableByUnknownCaller
            )
            putBoolean(CONTENT_STYLE_SUPPORTED, true)
            putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID)
            putInt(CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST)
        }

        /**
         * By default return the browsable root. Treat the EXTRA_RECENT flag as a special case
         * and return the recent root instead.
         */
        val isRecentRequest = rootHints?.getBoolean(EXTRA_RECENT) ?: false
        val browserRootPath = if (isRecentRequest) UAMP_RECENT_ROOT else UAMP_BROWSABLE_ROOT
        Log.i(TAG, "is recent request = $isRecentRequest, root path = $browserRootPath")
        return BrowserRoot(browserRootPath, rootExtras)
    }

    /**
     * Returns (via the [result] parameter) a list of [MediaItem]s that are child
     * items of the provided [parentMediaId]. See [BrowseTree] for more details on
     * how this is build/more details about the relationships.
     */
    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {

        /**
         * If the caller requests the recent root, return the most recently played song.
         */

        if (parentMediaId == UAMP_RECENT_ROOT) {

            if (recentSong != null) {
                result.sendResult(
                    listOf(
                        MediaBrowserCompat.MediaItem(
                            recentSong!!.description,
                            FLAG_PLAYABLE
                        )
                    )
                )
            } else {
                result.detach()
            }
            Log.i(TAG, "parent media = $parentMediaId, recent song = ${recentSong?.title}")


        } else {
            // If the media source is ready, the results will be set synchronously here.

            val resultsSent = songRepository.whenReady { successfullyInitialized ->
                if (successfullyInitialized) {
                    Log.d(TAG, "successfullyInitialized")
                    try {
                        // variable children hanya untuk ditampilkan ke pengguna
                        val children = currentPlaylistItems.map {
                            MediaBrowserCompat.MediaItem(it.description, it.flag)
                        }

                        result.sendResult(children)
                    } catch (e: IllegalStateException) {
                        notifyChildrenChanged(parentMediaId)
                    }

                } else {
                    Log.i(TAG, "not successfullyInitialized")
                    mediaSession.sendSessionEvent(NETWORK_FAILURE, null)
                    result.sendResult(null)
                }
            }

            // If the results are not ready, the service must "detach" the results before
            // the method returns. After the source is ready, the lambda above will run,
            // and the caller will be notified that the results are ready.
            //
            // See [MediaItemFragmentViewModel.subscriptionCallback] for how this is passed to the
            // UI/displayed in the [RecyclerView].
            if (!resultsSent) {
                try {
                    result.detach()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
            Log.i(TAG, "parent media = $parentMediaId, result send = $resultsSent")
        }
        Log.i(TAG, "parent media = $parentMediaId")
    }

    /**
     * Returns a list of [MediaItem]s that match the given search query
     */
    override fun onSearch(
        query: String,
        extras: Bundle?,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {

        val resultsSent = songRepository.whenReady { successfullyInitialized ->
            if (successfullyInitialized) {
                val resultsList = songRepository.onSearch(query, extras ?: Bundle.EMPTY)
                    .map { mediaMetadata ->
                        MediaBrowserCompat.MediaItem(mediaMetadata.description, mediaMetadata.flag)
                    }
                result.sendResult(resultsList)
            }
        }

        if (!resultsSent) {
            result.detach()
        }
    }

    /**
     * Load the supplied list of songs and the song to play into the current player.
     */
    private fun preparePlaylist(
        metadataList: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean,
        playbackStartPositionMs: Long
    ) {
        // Since the playlist was probably based on some ordering (such as tracks
        // on an album), find which window index to play first so that the song the
        // user actually wants to hear plays first.
        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
        currentPlaylistItems = metadataList

        currentPlayer.playWhenReady = playWhenReady
        currentPlayer.stop()
        currentPlayer.clearMediaItems()
        if (currentPlayer == exoPlayer) {

            val mediaSource = metadataList.toMediaSource(cachedDataSourceFactory)
            exoPlayer.apply {
                setMediaSource(mediaSource)
                prepare()
                seekTo(initialWindowIndex, playbackStartPositionMs)
            }
        } else /* currentPlayer == castPlayer */ {
            val items: List<MediaItem> = metadataList.map {
                it.toMediaQueueItem(it)
            }.toList()
            castPlayer?.setMediaItems(items, initialWindowIndex, playbackStartPositionMs)
        }
    }

    private fun switchToPlayer(previousPlayer: Player?, newPlayer: Player) {
        if (previousPlayer == newPlayer) {
            return
        }
        currentPlayer = newPlayer
        if (previousPlayer != null) {
            val playbackState = previousPlayer.playbackState
            if (currentPlaylistItems.isEmpty()) {
                // We are joining a playback session. Loading the session from the new player is
                // not supported, so we stop playback.
                currentPlayer.stop()
                currentPlayer.clearMediaItems()
            } else if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                preparePlaylist(
                    metadataList = currentPlaylistItems,
                    itemToPlay = currentPlaylistItems[previousPlayer.currentMediaItemIndex],
                    playWhenReady = previousPlayer.playWhenReady,
                    playbackStartPositionMs = previousPlayer.currentPosition
                )
            }
        }
        mediaSessionConnector.setPlayer(newPlayer)
        previousPlayer?.stop()
        previousPlayer?.clearMediaItems()
    }

    private fun saveRecentSongToStorage() {

        // Obtain the current song details *before* saving them on a separate thread, otherwise
        // the current player may have been unloaded by the time the save routine runs.
        val description = currentPlaylistItems[currentPlayer.currentMediaItemIndex]
        val position = currentPlayer.currentPosition

        storage.saveRecentSong(
            description,
            position
        )
        //recentSong = storage.loadRecentSong().first() // prevent bug
    }

    private inner class UampCastSessionAvailabilityListener : SessionAvailabilityListener {

        /**
         * Called when a Cast session has started and the user wishes to control playback on a
         * remote Cast receiver rather than play audio locally.
         */
        override fun onCastSessionAvailable() {
            switchToPlayer(currentPlayer, castPlayer!!)
        }

        /**
         * Called when a Cast session has ended and the user wishes to control playback locally.
         */
        override fun onCastSessionUnavailable() {
            switchToPlayer(currentPlayer, exoPlayer)
        }
    }

    private inner class UampQueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat =
            currentPlaylistItems[windowIndex].description
    }

    private inner class UampPlaybackPreparer : MediaSessionConnector.PlaybackPreparer {

        /**
         * UAMP supports preparing (and playing) from search, as well as media ID, so those
         * capabilities are declared here.
         *
         * TODO: Add support for ACTION_PREPARE and ACTION_PLAY, which mean "prepare/play something".
         */
        override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH


        override fun onPrepare(playWhenReady: Boolean) {
            //execute from notification

            Log.i(TAG, "onPrepare: ${recentSong?.title.toString()}")
            if (recentSong != null) {
                onPrepareFromMediaId(
                    recentSong!!.id!!,
                    playWhenReady,
                    recentSong!!.bundle
                )
            }

        }

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            Log.d(TAG, "onPrepareFromMediaId: $mediaId, extras: ${extras.toString()}")
            val playbackStartPositionMs =
                extras?.getLong(
                    PREFERENCES_POSITION,
                    C.TIME_UNSET
                )
                    ?: C.TIME_UNSET

            songRepository.whenReady {
                val itemToPlay = songRepository.mediaMetadataCompats.find { item ->
                    item.id == mediaId
                }
                if (itemToPlay == null) {
                    Log.w(TAG, "Content not found: MediaID=$mediaId")
                    // TODO: Notify caller of the error.
                    serviceScope.launch() {
                        withContext(Dispatchers.IO) {
                            songRepository.getSongs(true).first()
                        }
                        val newItemToPlay =
                            songRepository.mediaMetadataCompats.find { it.id == mediaId }
                            preparePlaylist(
                                metadataList = buildPlaylist(),
                                itemToPlay = newItemToPlay,
                                playWhenReady = playWhenReady,
                                playbackStartPositionMs = playbackStartPositionMs
                            )
                    }
                } else {

                    preparePlaylist(
                        metadataList = buildPlaylist(),
                        itemToPlay = itemToPlay,
                        playWhenReady,
                        playbackStartPositionMs
                    )
                }

            }
        }

        /**
         * This method is used by the Google Assistant to respond to requests such as:
         * - Play Geisha from Wake Up on UAMP
         * - Play electronic music on UAMP
         * - Play music on UAMP
         *
         * For details on how search is handled, see [SongRepository.onSearch].
         */
        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
            songRepository.whenReady {
                songRepository.getSongs(true)
                val metadataList = songRepository.onSearch(query, extras ?: Bundle.EMPTY)
                if (metadataList.isNotEmpty()) {
                    preparePlaylist(
                        metadataList,
                        metadataList[0],
                        playWhenReady,
                        playbackStartPositionMs = C.TIME_UNSET
                    )
                }
            }
        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit

        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean {
            return when (command) {
                "connect" -> {
                    Log.d(TAG, "connecting")
                    serviceScope.launch {
                        songRepository.getSongs(true)
                        Log.d(TAG, "${songRepository.mediaMetadataCompats.toList().size}")
                    }
                    true
                }
                else -> false
            }
        }

        private fun buildPlaylist(/* item: MediaMetadataCompat */): List<MediaMetadataCompat> =
            songRepository.mediaMetadataCompats.toList()
    }

    /**
     * Listen for notification events.
     */
    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MusicService.javaClass)
                )
                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    /**
     * Listen for events from ExoPlayer.
     */
    private inner class PlayerEventListener : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(currentPlayer)
                    if (playbackState == Player.STATE_READY) {

                        // When playing/paused save the current media item in persistent
                        // storage so that playback can be resumed between device reboots.
                        // Search for "media resumption" for more information.
                        try {
                            saveRecentSongToStorage()
                        } catch (e: Exception) {
                            // unknown crash
                            Log.d(TAG, "unknown: $e")
                        }

                        if (!playWhenReady) {
                            // If playback is paused we remove the foreground state which allows the
                            // notification to be dismissed. An alternative would be to provide a
                            // "close" button in the notification which stops playback and clears
                            // the notification.
                            stopForeground(false)
                            isForegroundService = false
                        }
                    }
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Toast.makeText(
                applicationContext,
                error.message.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

/*
 * (Media) Session events
 */

/** Content styling constants */
private const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
private const val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
private const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
private const val CONTENT_STYLE_LIST = 1
private const val CONTENT_STYLE_GRID = 2

private const val TAG = "MusicService"