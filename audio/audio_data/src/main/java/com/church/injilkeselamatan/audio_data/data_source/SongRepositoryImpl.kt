package com.church.injilkeselamatan.audio_data.data_source

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import coil.ImageLoader
import coil.request.ImageRequest
import com.church.injilkeselamatan.audio_data.data_source.local.MusicDatabase
import com.church.injilkeselamatan.audio_data.data_source.local.model.toDbEntity
import com.church.injilkeselamatan.audio_data.data_source.local.model.toSong
import com.church.injilkeselamatan.audio_data.data_source.remote.model.*
import com.church.injilkeselamatan.audio_domain.model.Song
import com.church.injilkeselamatan.audio_domain.repository.SongRepository
import com.church.injilkeselamatan.core.util.Resource
import com.church.injilkeselamatan.core.util.extensions.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.nio.channels.UnresolvedAddressException


class SongRepositoryImpl(
    musicDatabase: MusicDatabase,
    private val client: HttpClient,
    private val imageLoader: ImageLoader,
    private val context: Context
) : SongRepository {

    private val endpointUrl = "https://aws.injilkeselamatan.com:8443/audio"
    private val musicDao = musicDatabase.musicDao()

    override var mediaMetadataCompats: List<MediaMetadataCompat> = emptyList()

    override fun getSongs(fromDbSource: Boolean): Flow<Resource<List<Song>>> = flow {
        val songs = musicDao.getAllSongs().map { it.toSong() }

        if (fromDbSource) {
            emit(Resource.Success(songs))
        } else {
            emit(Resource.Loading(data = songs))
            mediaMetadataCompats = updateCatalog(songs) ?: emptyList()

            try {
                val remoteSongs = client.get<MusicApiDto>(endpointUrl).music
                musicDao.deleteAllSongs()
                musicDao.insertSongs(remoteSongs.map { it.toMusicDb() })
                emit(Resource.Success(songs))
            } catch (e: UnresolvedAddressException) {
                Log.e(TAG, e.toString())
                emit(
                    Resource.Error(
                        message = "Couldn't reach server, check your internet connection",
                        data = songs
                    )
                )
            } catch (e: ClientRequestException) {
                Log.e(TAG, e.toString())
                emit(
                    Resource.Error(
                        message = "Couldn't reach server, check your internet connection",
                        data = songs
                    )
                )
            } catch (e: ServerResponseException) {
                Log.e(TAG, e.toString())
                emit(
                    Resource.Error(
                        message = "Couldn't reach server, check your internet connection",
                        data = songs
                    )
                )
            } catch (e: ConnectTimeoutException) {
                emit(
                    Resource.Error(
                        message = "Connection timeout",
                        data = songs
                    )
                )
            } catch (e: Exception) {
                emit(Resource.Error(
                    message = "Please try again later",
                    data = songs
                ))
            }
        }
        mediaMetadataCompats = updateCatalog(songs) ?: emptyList()
    }

    override fun whenReady(performAction: (Boolean) -> Unit): Boolean {
        performAction(mediaMetadataCompats.isNotEmpty())
        return mediaMetadataCompats.isNotEmpty()
    }

    override fun onSearch(query: String, extras: Bundle): List<MediaMetadataCompat> {
        val focusSearchResult = when (extras[MediaStore.EXTRA_MEDIA_FOCUS]) {
            MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                // For a Genre focused search, only genre is set.
                val genre = extras[MediaStore.EXTRA_MEDIA_GENRE]
                Log.d(TAG, "Focused genre search: '$genre'")
                mediaMetadataCompats.filter { song ->
                    song.genre == genre
                }
            }
            MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                // For an Artist focused search, only the artist is set.
                val artist = extras[MediaStore.EXTRA_MEDIA_ARTIST]
                Log.d(TAG, "Focused artist search: '$artist'")
                mediaMetadataCompats.filter { song ->
                    (song.artist == artist || song.albumArtist == artist)
                }
            }
            MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                // For an Album focused search, album and artist are set.
                val artist = extras[MediaStore.EXTRA_MEDIA_ARTIST]
                val album = extras[MediaStore.EXTRA_MEDIA_ALBUM]
                Log.d(TAG, "Focused album search: album='$album' artist='$artist")
                mediaMetadataCompats.filter { song ->
                    (song.artist == artist || song.albumArtist == artist) && song.album == album
                }
            }
            MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> {
                // For a Song (aka Media) focused search, title, album, and artist are set.
                val title = extras[MediaStore.EXTRA_MEDIA_TITLE]
                val album = extras[MediaStore.EXTRA_MEDIA_ALBUM]
                val artist = extras[MediaStore.EXTRA_MEDIA_ARTIST]
                Log.d(TAG, "Focused media search: title='$title' album='$album' artist='$artist")
                mediaMetadataCompats.filter { song ->
                    (song.artist == artist || song.albumArtist == artist) && song.album == album
                            && song.title == title
                }
            }
            else -> {
                // There isn't a focus, so no results yet.
                emptyList()
            }
        }

        // If there weren't any results from the focused search (or if there wasn't a focus
        // to begin with), try to find any matches given the 'query' provided, searching against
        // a few of the fields.
        // In this sample, we're just checking a few fields with the provided query, but in a
        // more complex app, more logic could be used to find fuzzy matches, etc...
        if (focusSearchResult.isEmpty()) {
            return if (query.isNotBlank()) {
                Log.d(TAG, "Unfocused search for '$query'")
                mediaMetadataCompats.filter { song ->
                    song.title.containsCaseInsensitive(query)
                            || song.genre.containsCaseInsensitive(query)
                }
            } else {
                // If the user asked to "play music", or something similar, the query will also
                // be blank. Given the small catalog of songs in the sample, just return them
                // all, shuffled, as something to play.
                Log.d(TAG, "Unfocused search without keyword")
                return mediaMetadataCompats.shuffled()
            }
        } else {
            return focusSearchResult
        }
    }

    override fun getFeaturedSong(): Flow<Resource<Song>> = flow {
        emit(Resource.Loading())
        try {
            val response = client.get<MusicApiDtoSingle>("$endpointUrl/featured").music.toSong()
            emit(Resource.Success(response))
        } catch (e: UnresolvedAddressException) {
            emit(Resource.Error("Please check your internet connection"))

        } catch (e: ConnectTimeoutException) {
            emit(Resource.Error("Connection timeout"))
        } catch (e: Exception) {
            emit(Resource.Error("Please try again later"))
        }
    }

    override suspend fun updateSong(song: Song): Resource<String> {
        musicDao.setFavoriteSong(song.toDbEntity())
        return Resource.Success("Success")
    }

//    override suspend fun updateDuration(
//        mediaId: String,
//        updateSongDto: UpdateSongDto
//    ): Flow<Resource<MusicApiDtoSingle>> {
//        return flow {
//            emit(Resource.Loading())
//            try {
//                val response = client.put<MusicApiDtoSingle>(
//                    "$endpointUrl/mediaId"
//                ) {
//                    contentType(ContentType.Application.Json)
//                    body = updateSongDto
//                    method = HttpMethod.Put
//                }
//                emit(Resource.Success(response))
//            } catch (e: Exception) {
//                emit(Resource.Error(e.message ?: "Unknown error occurred"))
//            }
//        }
//    }

    override suspend fun checkVersion(currentVersion: Int): Boolean {
        val endpoint = "https://aws.injilkeselamatan.com:8443/version"
        return try {
            val response = client.get<VersionResponseDto>(endpoint).versionResponse.version
            currentVersion >= response
        } catch (e: Exception) {
            Log.e("CheckVersion", "$e")
            true
        }
    }

    private suspend fun updateCatalog(songs: List<Song>): List<MediaMetadataCompat>? {
        return withContext(Dispatchers.IO) {
            if (songs.isEmpty()) {
                return@withContext null
            }

            val albumsUrl = songs.distinctBy { it.imageUri }
                .map { song ->
                    val coilRequest = ImageRequest.Builder(context)
                        .data(song.imageUri)
                        .allowHardware(false)
                        .size(512, 512)
                        .allowConversionToBitmap(true)
                        .build()

                    val drawable = imageLoader.execute(coilRequest).drawable
                    val bitmap = (drawable as? BitmapDrawable)?.bitmap
                    Pair(song.imageUri, bitmap)

                }


            val mediaMetadataCompats = songs.map { song ->

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
            }

            mediaMetadataCompats.forEach {
                it.description.extras?.putAll(it.bundle)
            }
            mediaMetadataCompats
        }
    }

    private fun MediaMetadataCompat.Builder.from(song: Song): MediaMetadataCompat.Builder {
        // The duration from the JSON is given in seconds, but the rest of the code works in
        // milliseconds. Here's where we convert to the proper units.

        id = song.id
        title = song.title
        artist = song.artist
        album = song.album
        mediaUri = song.mediaUri
        albumArtUri = song.imageUri
        flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

        // To make things easier for *displaying* these, set the display properties as well.
        displayTitle = song.title
        displaySubtitle = song.artist
        displayDescription = song.description
        displayIconUri = song.imageUri
        if (song.duration >= 1000) {
            duration = song.duration
        }

        // Add downloadStatus to force the creation of an "extras" bundle in the resulting
        // MediaMetadataCompat object. This is needed to send accurate metadata to the
        // media session during updates.
        downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED

        // Allow it to be used in the typical builder style.
        return this
    }
}

private const val TAG = "SongRepository"