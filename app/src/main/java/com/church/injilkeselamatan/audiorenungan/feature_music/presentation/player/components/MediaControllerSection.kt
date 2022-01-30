package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlaying

@Composable
fun MediaControllerSection(
    modifier: Modifier = Modifier,
    playbackStateCompat: PlaybackStateCompat?,
    iconSize: Dp = 48.dp,
    iconColor: Color = MaterialTheme.colors.onSurface,
    onPlayClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onSkipToPrevious: () -> Unit,
    onSkipToNext: () -> Unit,
    onForward: () -> Unit,
    onRewind: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {

        Icon(
            painterResource(id = R.drawable.ic_skip_previous),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .clickable {
                    onSkipToPrevious()
                },
            tint = iconColor
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))

        Icon(
            painterResource(id = R.drawable.ic_round_replay_10),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .clickable {
                    onRewind()
                },
            tint = iconColor
        )

        Spacer(modifier = Modifier.padding(horizontal = 8.dp))

        if (playbackStateCompat?.isPlaying == true) {
            Icon(
                painterResource(id = R.drawable.ic_pause),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .size(iconSize)
                    .clickable {
                        onPauseClicked()
                    }
            )
        } else {
            Icon(
                painterResource(id = R.drawable.ic_play_arrow),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .size(iconSize)
                    .clickable {
                        onPlayClicked()
                    }
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = 8.dp))

        Icon(
            painterResource(id = R.drawable.ic_round_forward_10),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .clickable {
                    onForward()
                },
            tint = iconColor
        )

        Spacer(modifier = Modifier.padding(horizontal = 8.dp))

        Icon(
            painterResource(id = R.drawable.ic_skip_next),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .clickable {
                    onSkipToNext()
                },
            tint = iconColor
        )

    }
}