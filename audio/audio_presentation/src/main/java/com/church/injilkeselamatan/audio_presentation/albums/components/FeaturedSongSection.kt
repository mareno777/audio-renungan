package com.church.injilkeselamatan.audio_presentation.albums.components

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.church.injilkeselamatan.audio_domain.model.Song
import com.church.injilkeselamatan.audio_presentation.R
import com.church.injilkeselamatan.audio_presentation.albums.FeaturedSongState
import com.church.injilkeselamatan.audiorenungan.core_ui.MarqueeText
import com.church.injilkeselamatan.core.util.extensions.id
import com.church.injilkeselamatan.core.util.extensions.isPlaying

@Composable
fun FeaturedSongSection(
    modifier: Modifier = Modifier,
    iconSize: Dp = 50.dp,
    mediaMetadataCompat: MediaMetadataCompat,
    playbackState: PlaybackStateCompat,
    featuredSongState: FeaturedSongState<Song>,
    onPlayPauseClicked: () -> Unit,
    onRetryClicked: () -> Unit
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = "Renungan audio hari ini",
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (featuredSongState.song != null) {
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
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        MarqueeText(
                            text = featuredSongState.song.title,
                            color = Color.White,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = featuredSongState.song.description.toString(),
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
                        modifier = Modifier
                            .weight(0.1f)
                            .size(iconSize)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onPlayPauseClicked()
                            }
                    )
                }

            }
        } else if (featuredSongState.errorMessage != null) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(CornerSize(8.dp)),
                elevation = 8.dp,
                backgroundColor = Color.Red
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = featuredSongState.errorMessage,
                        color = Color.White,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_refresh),
                        tint = Color.White,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(0.1f)
                            .clickable(
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
}