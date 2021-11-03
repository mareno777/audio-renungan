package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.artist
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.title
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@ExperimentalPagerApi
@Composable
fun ImageTitleArtist(
    mediaMetadataCompat: MediaMetadataCompat?,
    playbackStateCompat: PlaybackStateCompat?,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    songs: List<Song>?
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        HorizontalPagerWithOffsetTransition(pagerState, songs)
        Spacer(modifier = Modifier.padding(vertical = 16.dp))
        Column(
            verticalArrangement = Arrangement.Center,
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
}