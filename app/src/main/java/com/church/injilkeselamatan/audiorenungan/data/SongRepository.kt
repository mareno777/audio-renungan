package com.church.injilkeselamatan.audiorenungan.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.church.injilkeselamatan.audiorenungan.data.models.MusicX
import com.church.injilkeselamatan.audiorenungan.data.remote.SongsApi
import com.church.injilkeselamatan.audiorenungan.exoplayer.*
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.extensions.*
import com.church.injilkeselamatan.audiorenungan.exoplayer.media.library.*
import com.church.injilkeselamatan.audiorenungan.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.FileDescriptor
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

class SongRepository @Inject constructor(
    private val songsApi: SongsApi, private val context: Context
) : AbstractMusicSource() {

    private var catalog: List<MediaMetadataCompat> = emptyList()

    init {
        state = STATE_INITIALIZING
    }

    fun getSongs(): Flow<Resource<List<MusicX>>> = flow {
        try {
            emit(Resource.Success<List<MusicX>>(songsApi.getSongs().music))
        } catch (e: HttpException) {
            emit(Resource.Error<List<MusicX>>(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error<List<MusicX>>(e.localizedMessage ?: "An unexpected error occured"))
        }

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
                getSongs().first().data!!
            } catch (e: Exception) {
                return@withContext null
            }


            val mediaMetadataCompats = response.map { song ->

                val imageUri = AlbumArtContentProvider.mapUri(Uri.parse(song.image))

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

    fun MediaMetadataCompat.Builder.from(jsonMusic: MusicX): MediaMetadataCompat.Builder {
        // The duration from the JSON is given in seconds, but the rest of the code works in
        // milliseconds. Here's where we convert to the proper units.


        id = jsonMusic.id
        title = jsonMusic.title
        artist = jsonMusic.artist
        album = jsonMusic.album
        mediaUri = jsonMusic.source
        albumArtUri = jsonMusic.image
        flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

        // To make things easier for *displaying* these, set the display properties as well.
        displayTitle = jsonMusic.title
        displaySubtitle = jsonMusic.artist
        displayDescription = jsonMusic.album
        displayIconUri = jsonMusic.image

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