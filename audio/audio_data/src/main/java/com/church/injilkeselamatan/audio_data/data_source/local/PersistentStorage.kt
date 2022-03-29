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

package com.church.injilkeselamatan.audio_data.data_source.local

import android.content.SharedPreferences
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.church.injilkeselamatan.core.util.extensions.*
import com.google.android.exoplayer2.C


class PersistentStorage(
    private val sharedPref: SharedPreferences
) {

    companion object {
        private const val RECENT_MEDIA_ID_KEY = "recent_media_id"
        private const val RECENT_TITLE_KEY = "recent_title"
        private const val RECENT_SUBTITLE_KEY = "recent_subtitle"
        private const val RECENT_ALBUM_KEY = "recent_album"
        private const val RECENT_DESCRIPTION_KEY = "recent_description"
        private const val RECENT_MEDIA_URI_KEY = "recent_media_uri"
        private const val RECENT_ICON_URI_KEY = "recent_icon_uri"
        private const val RECENT_POSITION_KEY = "recent_position"
    }

    /**
     * Store any data which must persist between restarts, such as the most recently played song.
     */


    fun saveRecentSong(
        description: MediaMetadataCompat,
        position: Long
    ) {
        try {
            sharedPref.edit()
                .putString(RECENT_MEDIA_ID_KEY, description.id.toString())
                .putString(RECENT_TITLE_KEY, description.title.toString())
                .putString(RECENT_SUBTITLE_KEY, description.artist.toString())
                .putString(RECENT_ALBUM_KEY, description.album.toString())
                .putString(RECENT_DESCRIPTION_KEY, description.displayDescription.toString())
                .putString(RECENT_MEDIA_URI_KEY, description.mediaUri.toString())
                .putString(RECENT_ICON_URI_KEY, description.displayIconUri.toString())
                .putLong(RECENT_POSITION_KEY, position)
                .apply()

        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }


    }

    fun loadRecentSong(): MediaMetadataCompat? {
        val mediaId = sharedPref.getString(RECENT_MEDIA_ID_KEY, "")
        if (mediaId.isNullOrEmpty()) return null
        return MediaMetadataCompat.Builder().apply {
            id = mediaId
            title = sharedPref.getString(RECENT_TITLE_KEY, "")
            album = sharedPref.getString(RECENT_ALBUM_KEY, "")
            artist = sharedPref.getString(RECENT_SUBTITLE_KEY, "")
            mediaUri = sharedPref.getString(RECENT_MEDIA_URI_KEY, "")
            albumArtUri = sharedPref.getString(RECENT_ICON_URI_KEY, "")
            displayTitle = sharedPref.getString(RECENT_TITLE_KEY, "")
            displaySubtitle = sharedPref.getString(RECENT_SUBTITLE_KEY, "")
            displayDescription = sharedPref.getString(RECENT_DESCRIPTION_KEY, "")
            displayIconUri = sharedPref.getString(RECENT_ICON_URI_KEY, "")
            flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED
            putLong(PREFERENCES_POSITION, sharedPref.getLong(RECENT_POSITION_KEY, C.TIME_UNSET))
        }.build()
    }

//    fun loadRecentSong(): Flow<MediaMetadataCompat> {
//        return context.dataStoreAudio.data.map { preferences ->
//            val mediaId = preferences[RECENT_MEDIA_ID_KEY]
//            if (mediaId != null) {
//                Log.d(TAG, "load: ${preferences[RECENT_POSITION_KEY]}")
//                val mediaMetadataCompat = MediaMetadataCompat.Builder()
//                    .putLong(PREFERENCES_POSITION, preferences[RECENT_POSITION_KEY] ?: C.TIME_UNSET)
//                    .apply {
//                        id = mediaId
//                        title = preferences[RECENT_TITLE_KEY]
//                        album = preferences[RECENT_ALBUM_KEY]
//                        artist = preferences[RECENT_SUBTITLE_KEY]
//                        mediaUri = preferences[RECENT_MEDIA_URI_KEY]
//                        albumArtUri = preferences[RECENT_ICON_URI_KEY]
//                        displayTitle = preferences[RECENT_TITLE_KEY]
//                        displaySubtitle = preferences[RECENT_SUBTITLE_KEY]
//                        displayDescription = preferences[RECENT_DESCRIPTION_KEY]
//                        displayIconUri = preferences[RECENT_ICON_URI_KEY]
//                        flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
//                        downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED
//                    }
//                    .build()
//                mediaMetadataCompat
//            } else {
//                flow {  }
//            }
//        }
//    }
}

private const val TAG = "PersistentStorage"
const val PREFERENCES_POSITION = "preferences_position"
const val PREFERENCES_DURATION = "preferences_duration"