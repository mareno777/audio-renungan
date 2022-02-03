package com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.data

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.PixelSize
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.toSong
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.*
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.AbstractMusicSource
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.STATE_ERROR
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.STATE_INITIALIZED
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.STATE_INITIALIZING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicSourceRepository(
    musicDatabase: MusicDatabase,
    private val context: Context,
    private val imageLoader: ImageLoader
) : AbstractMusicSource() {

    private var catalog = emptyList<MediaMetadataCompat>()
    private val musicDao = musicDatabase.musicDao()

    companion object {
        const val MEDIA_METADATA_DESCRIPTION = "com.injilkeselamatan.audiorenungan.description"
    }

    override suspend fun load() {
        Log.d(TAG, "STATE_INITIALIZING")
        state = STATE_INITIALIZING
        updateCatalog()?.let { updatedCatalog ->
            catalog = updatedCatalog
            state = STATE_INITIALIZED
        } ?: run {
            catalog = emptyList()
            state = STATE_ERROR
        }
    }

    override fun iterator() = catalog.iterator()

    private suspend fun updateCatalog(): List<MediaMetadataCompat>? {
        return withContext(Dispatchers.IO) {
            val response = try {
                val result: MutableList<Song> = mutableListOf()
                val musicFromDb = musicDao.getAllSongs()

                result.addAll(
                    musicFromDb.map {
                        it.toSong()
                    }
                )
                result
            } catch (e: Exception) {
                return@withContext null
            }

            val albumsUrl = response.distinctBy { it.imageUri }
                .map { song ->
                    val coilRequest = ImageRequest.Builder(context)
                        .data(song.imageUri)
                        .allowHardware(false)
                        .size(PixelSize(512, 512))
                        .allowConversionToBitmap(true)
                        .build()


                    val drawable = imageLoader.execute(coilRequest).drawable
                    val bitmap = (drawable as? BitmapDrawable)?.bitmap
                    Pair(song.imageUri, bitmap)

                }


            val mediaMetadataCompats = response.map { song ->

                val bitmap = albumsUrl.find { it.first == song.imageUri }?.second

                MediaMetadataCompat.Builder()
                    .from(song)
                    .apply {
                        displayIconUri = song.imageUri // Used by ExoPlayer and Notification
                        albumArtUri = song.imageUri
                        albumArt = bitmap
                        displayIcon = bitmap
                    }
                    .build()
            }.toList()

            mediaMetadataCompats.forEach {
                it.description.extras?.putAll(it.bundle)
            }
            mediaMetadataCompats
        }
    }

    fun MediaMetadataCompat.Builder.from(jsonMusic: Song): MediaMetadataCompat.Builder {
        // The duration from the JSON is given in seconds, but the rest of the code works in
        // milliseconds. Here's where we convert to the proper units.

        id = jsonMusic.id
        title = jsonMusic.title
        artist = jsonMusic.artist
        album = jsonMusic.album
        mediaUri = jsonMusic.mediaUri
        albumArtUri = jsonMusic.imageUri
        flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        putString(MEDIA_METADATA_DESCRIPTION, jsonMusic.description)

        // To make things easier for *displaying* these, set the display properties as well.
        displayTitle = jsonMusic.title
        displaySubtitle = jsonMusic.artist
        displayDescription = jsonMusic.description
        displayIconUri = jsonMusic.imageUri
        if (jsonMusic.duration >= 1000) {
            duration = jsonMusic.duration
        }

        // Add downloadStatus to force the creation of an "extras" bundle in the resulting
        // MediaMetadataCompat object. This is needed to send accurate metadata to the
        // media session during updates.
        downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED

        // Allow it to be used in the typical builder style.
        return this
    }
}

private const val TAG = "MusicSourceRepository"