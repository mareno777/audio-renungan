package com.church.injilkeselamatan.core.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStoreUser: DataStore<Preferences> by preferencesDataStore(name = "user_info")
val Context.dataStoreAudio: DataStore<Preferences> by preferencesDataStore(name = "audio_renungan")