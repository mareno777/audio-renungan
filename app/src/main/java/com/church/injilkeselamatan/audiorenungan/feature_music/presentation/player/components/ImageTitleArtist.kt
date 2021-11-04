package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.artist
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.title
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@Composable
fun ImageTitleArtist(
    mediaMetadataCompat: MediaMetadataCompat?,
    playbackStateCompat: PlaybackStateCompat?
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = mediaMetadataCompat?.title ?: "Unknown",
            color = MaterialTheme.colors.onSurface
        )
        Text(
            text = mediaMetadataCompat?.artist ?: "Unknown",
            color = MaterialTheme.colors.onSurface
        )
    }
}