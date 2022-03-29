package com.church.injilkeselamatan.audio_presentation.player.components

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.church.injilkeselamatan.audio_presentation.R
import com.church.injilkeselamatan.core.util.extensions.isPlaying

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
        IconButton(onClick = { onSkipToPrevious() }) {
            Icon(
                painterResource(id = R.drawable.ic_skip_previous),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        IconButton(onClick = { onRewind() }) {
            Icon(
                painterResource(id = R.drawable.ic_round_replay_10),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        if (playbackStateCompat?.isPlaying == true) {
            IconButton(onClick = { onPauseClicked() }) {
                Icon(
                    painterResource(id = R.drawable.ic_pause),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colors.onSurface
                )
            }
        } else {
            IconButton(onClick = { onPlayClicked() }) {
                Icon(
                    painterResource(id = R.drawable.ic_play_arrow),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        IconButton(onClick = { onForward() }) {
            Icon(
                painterResource(id = R.drawable.ic_round_forward_10),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        IconButton(onClick = { onSkipToNext() }) {
            Icon(
                painterResource(id = R.drawable.ic_skip_next),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = iconColor
            )
        }
    }
}