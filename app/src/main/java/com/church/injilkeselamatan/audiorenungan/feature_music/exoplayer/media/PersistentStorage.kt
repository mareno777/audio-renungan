/*
 * Copyright 2020 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.common.NOTHING_PLAYING
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.*
import com.google.android.exoplayer2.C
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PersistentStorage(val context: Context) {

    companion object {
        private const val AUDIO_RENUNGAN = "audio_renungan"
        private val RECENT_MEDIA_ID_KEY = stringPreferencesKey("recent_media_id")
        private val RECENT_TITLE_KEY = stringPreferencesKey("recent_title")
        private val RECENT_SUBTITLE_KEY = stringPreferencesKey("recent_subtitle")
        private val RECENT_ALBUM_KEY = stringPreferencesKey("recent_album")
        private val RECENT_DESCRIPTION_KEY = stringPreferencesKey("recent_description")
        private val RECENT_MEDIA_URI_KEY = stringPreferencesKey("recent_media_uri")
        private val RECENT_ICON_URI_KEY = stringPreferencesKey("recent_icon_uri")
        private val RECENT_POSITION_KEY = longPreferencesKey("recent_position")
    }

    /**
     * Store any data which must persist between restarts, such as the most recently played song.
     */

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AUDIO_RENUNGAN)

    suspend fun saveRecentSong(
        description: MediaMetadataCompat,
        position: Long
    ) {
        try {
            context.dataStore.edit { preferences ->
                preferences[RECENT_MEDIA_ID_KEY] = description.id.toString()
                preferences[RECENT_TITLE_KEY] = description.title.toString()
                preferences[RECENT_SUBTITLE_KEY] = description.artist.toString()
                preferences[RECENT_ALBUM_KEY] = description.album.toString()
                preferences[RECENT_DESCRIPTION_KEY] = description.displayDescription.toString()
                preferences[RECENT_MEDIA_URI_KEY] = description.mediaUri.toString()
                preferences[RECENT_ICON_URI_KEY] = description.displayIconUri.toString()
                preferences[RECENT_POSITION_KEY] = position
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }


    }

    fun loadRecentSong(): Flow<MediaMetadataCompat> {
        return context.dataStore.data.map { preferences ->
            val mediaId = preferences[RECENT_MEDIA_ID_KEY]
            if (mediaId != null) {
                Log.d(TAG, "load: ${preferences[RECENT_POSITION_KEY]}")
                val mediaMetadataCompat = MediaMetadataCompat.Builder()
                    .putLong(PREFERENCES_POSITION, preferences[RECENT_POSITION_KEY] ?: C.TIME_UNSET)
                    .apply {
                        id = mediaId
                        title = preferences[RECENT_TITLE_KEY]
                        album = preferences[RECENT_ALBUM_KEY]
                        artist = preferences[RECENT_SUBTITLE_KEY]
                        mediaUri = preferences[RECENT_MEDIA_URI_KEY]
                        albumArtUri = preferences[RECENT_ICON_URI_KEY]
                        displayTitle = preferences[RECENT_TITLE_KEY]
                        displaySubtitle = preferences[RECENT_SUBTITLE_KEY]
                        displayDescription = preferences[RECENT_DESCRIPTION_KEY]
                        displayIconUri = preferences[RECENT_ICON_URI_KEY]
                        flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                        downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED
                    }
                    .build()
                mediaMetadataCompat
            } else {
                NOTHING_PLAYING
            }
        }
    }
}

private const val TAG = "PersistentStorage"
const val PREFERENCES_POSITION = "preferences_position"
const val PREFERENCES_DURATION = "preferences_duration"