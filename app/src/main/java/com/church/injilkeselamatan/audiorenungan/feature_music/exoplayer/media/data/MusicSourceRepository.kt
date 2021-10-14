package com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.PixelSize
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.MusicDbEntity
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.local.models.toSong
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.*
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.library.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.FileDescriptor
import java.io.IOException
import javax.inject.Inject

class MusicSourceRepository @Inject constructor(
    musicDatabase: MusicDatabase,
    val context: Context
) : AbstractMusicSource() {


    private var catalog = emptyList<MediaMetadataCompat>()
    private val musicDao = musicDatabase.musicDao()

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

    private suspend fun updateCatalog(musicList: List<MusicDbEntity> = emptyList()): List<MediaMetadataCompat>? {
        return withContext(Dispatchers.IO) {
            val response = try {
                val result: MutableList<Song> = mutableListOf()
                val musicFromDb = musicDao.getAllSongs().first()

                result.addAll(
                    musicFromDb.map {
                        it.toSong()
                    }
                )
                result
            } catch (e: Exception) {
                return@withContext null
            }


            val mediaMetadataCompats = response.map { song ->

                //val imageUri = AlbumArtContentProvider.mapUri(Uri.parse(song.imageUri))
                val coilRequest = ImageRequest.Builder(context)
                    .data(song.imageUri)
                    .allowHardware(false)
                    .size(PixelSize(512, 512))
                    .allowConversionToBitmap(true)
                    .build()
                val drawable = ImageLoader(context).execute(coilRequest).drawable
                val bitmap = (drawable as? BitmapDrawable)?.bitmap

                MediaMetadataCompat.Builder()
                    .from(song)
                    .apply {
                        displayIconUri =
                            song.imageUri // Used by ExoPlayer and Notification
                        albumArtUri = song.imageUri
                        albumArt = bitmap
                        displayIcon = bitmap
                    }
                    .build()
            }.toList()

            mediaMetadataCompats.forEach { it.description.extras?.putAll(it.bundle) }
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

private const val TAG = "MusicSourceRepository"