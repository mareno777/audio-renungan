package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.id
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.FeaturedSongState
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.MarqueeText

@Composable
fun FeaturedSongSection(
    modifier: Modifier = Modifier,
    mediaMetadataCompat: MediaMetadataCompat,
    playbackState: PlaybackStateCompat,
    featuredSongState: FeaturedSongState<Song>,
    onPlayPauseClicked: () -> Unit,
    onRetryClicked: () -> Unit
) {
    if (featuredSongState.song != null) {
        Column(modifier = modifier.padding(8.dp)) {
            Text(
                text = "Renungan audio hari ini",
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(CornerSize(8.dp)),
                elevation = 8.dp,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = featuredSongState.song.title,
                            color = Color.White,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = featuredSongState.song.album,
                            color = Color.White,
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                    Icon(
                        painter = if (
                            playbackState.isPlaying &&
                            mediaMetadataCompat.id == featuredSongState.song.id
                        ) {
                            painterResource(id = R.drawable.ic_pause)
                        } else {
                            painterResource(id = R.drawable.ic_play_arrow)
                        },
                        tint = Color.White,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                                onPlayPauseClicked()
                            }
                    )
                }

            }
        }
    } else if (featuredSongState.errorMessage != null) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(CornerSize(8.dp)),
            elevation = 8.dp,
            backgroundColor = Color.Red
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = featuredSongState.errorMessage,
                    color = Color.White,
                    style = MaterialTheme.typography.body1
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onRetryClicked()
                    }
                )
            }
        }
    }
}