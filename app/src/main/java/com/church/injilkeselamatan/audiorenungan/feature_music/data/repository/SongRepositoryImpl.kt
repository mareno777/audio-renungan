package com.church.injilkeselamatan.audiorenungan.feature_music.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.room.withTransaction
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.SongsApi
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.Resource
import com.church.injilkeselamatan.audiorenungan.feature_music.data.util.networkBoundResource
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.repository.SongRepository
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.*
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.FileDescriptor
import java.io.IOException
import javax.inject.Inject


class SongRepositoryImpl @Inject constructor(
    private val songsApi: SongsApi,
    private val context: Context,
    private val musicDatabase: MusicDatabase
) : AbstractMusicSource(), SongRepository {

    private var catalog: List<MediaMetadataCompat> = emptyList()

    private val musicDao = musicDatabase.musicDao()

    override fun getSongs(): Flow<Resource<List<Song>>> {
        return networkBoundResource(
            query = {
                musicDao.getAllSongs().map { musicDb ->
                    musicDb.map {
                        Song(
                            it.id,
                            it.title,
                            it.artist,
                            it.album,
                            it.imageUri,
                            it.mediaUri,
                            it.isFavorite
                        )
                    }
                }

            },
            fetch = {
                Log.d(TAG, "fetching")
                songsApi.getSongs().music
            },
            saveFetchResult = { musicDtos ->
                Log.d(TAG, "saving result from server")
                val musicDbs = musicDtos.map {
                    MusicDbEntity(
                        it.id,
                        it.title,
                        it.artist,
                        it.album,
                        it.image,
                        it.source
                    )

                }
                musicDatabase.withTransaction {
                    musicDao.clearAll()
                    musicDao.insertSongs(musicDbs)
                }
            },
            shouldFetch = {
                it.isEmpty()
            }
        )
    }


    override suspend fun load() {
        state = STATE_INITIALIZING
        updateCatalog()?.let { updatedCatalog ->
            catalog = updatedCatalog
            state = STATE_INITIALIZED
        } ?: run {
            catalog = emptyList()
            state = STATE_ERROR
        }
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = catalog.iterator()

    private suspend fun updateCatalog(): List<MediaMetadataCompat>? {
        return withContext(Dispatchers.IO) {
            val response = try {
                val result: MutableList<Song> = mutableListOf()
                val musicFromDb = musicDatabase.musicDao().getAllSongs().first()

                Log.d(TAG, musicFromDb.size.toString())
                result.addAll(
                    musicFromDb.map {
                        Song(
                            it.id,
                            it.title,
                            it.artist,
                            it.album,
                            it.imageUri,
                            it.mediaUri,
                            it.isFavorite
                        )
                    }
                )
                result
            } catch (e: Exception) {
                return@withContext null
            }


            val mediaMetadataCompats = response.map { song ->

                val imageUri = AlbumArtContentProvider.mapUri(Uri.parse(song.imageUri))

                MediaMetadataCompat.Builder()
                    .from(song)
                    .apply {
                        val bitmap = getBitmapFromUri(imageUri, context)
                        displayIconUri = imageUri.toString() // Used by ExoPlayer and Notification
                        albumArtUri = imageUri.toString()
                        albumArt = bitmap
                        displayIcon = bitmap
                    }
                    .build()
            }.toList()

            mediaMetadataCompats.forEach { it.description.extras?.putAll(it.bundle) }
            mediaMetadataCompats
        }
    }

    private fun MediaMetadataCompat.Builder.from(jsonMusic: Song): MediaMetadataCompat.Builder {
        // The duration from the JSON is given in seconds, but the rest of the code works in
        // milliseconds. Here's where we convert to the proper units.


        id = jsonMusic.id
        title = jsonMusic.title
        artist = jsonMusic.artist
        album = jsonMusic.album
        mediaUri = jsonMusic.mediaUri
        albumArtUri = jsonMusic.imageUri
        flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

        // To make things easier for *displaying* these, set the display properties as well.
        displayTitle = jsonMusic.title
        displaySubtitle = jsonMusic.artist
        displayDescription = jsonMusic.album
        displayIconUri = jsonMusic.imageUri

        // Add downloadStatus to force the creation of an "extras" bundle in the resulting
        // MediaMetadataCompat object. This is needed to send accurate metadata to the
        // media session during updates.
        downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED

        // Allow it to be used in the typical builder style.
        return this
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }
}

private const val TAG = "SongRepository"