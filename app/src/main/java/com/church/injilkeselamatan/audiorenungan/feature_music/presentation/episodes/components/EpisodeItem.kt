package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes.components

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.*
import com.google.android.exoplayer2.offline.Download
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.util.concurrent.TimeUnit

@Composable
fun EpisodeItem(
    song: MediaMetadataCompat,
    maxProgress: Float = 0f,
    downloadedLength: Float = 0f,
    downloaded: Boolean,
    @Download.State
    state: Int?,
    onPlayToggleClicked: () -> Unit,
    onDownloadClicked: () -> Unit,
    playbackState: PlaybackStateCompat,
    mediaMetadata: MediaMetadataCompat,
    context: Context
) {

    val color = MaterialTheme.colors.primary.toArgb()
    val painter = rememberImagePainter(
        ImageRequest.Builder(context)
            .data(song.albumArt)
            .transformations(RoundedCornersTransformation(55f))
            .build()
    )

    Column(
        modifier = Modifier
            .clickable { onPlayToggleClicked() }
            .padding(16.dp)
    ) {
        Row() {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(55.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${song.title}",
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${song.album} • ${millisToDuration(song.duration)}",
            color = Color.Gray,
            style = MaterialTheme.typography.body2,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${song.artist} \u2022 12 November 2021", // FIXME: 13/01/22 add custom bundle for uploadedAt
            color = Color.Gray,
            style = MaterialTheme.typography.body2,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(
                    id = if (playbackState.isPlaying && mediaMetadata.id == song.id) R.drawable.ic_outline_pause
                    else R.drawable.ic_outline_play
                ),
                contentDescription = null,
                tint = if (isSystemInDarkTheme()) Color.White else Color.Blue,
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        onPlayToggleClicked()
                    }
            )
            if (state == Download.STATE_DOWNLOADING || state == Download.STATE_QUEUED) {
                AndroidView(
                    factory = {
                        CircularProgressBar(it)
                    },
                    modifier = Modifier.size(24.dp)
                ) { circularProgressBar ->

                    circularProgressBar.progressBarColor = color
                    circularProgressBar.progressBarWidth = 4.dp.value
                    circularProgressBar.progressMax = 1f
                    circularProgressBar.indeterminateMode = state == Download.STATE_QUEUED

                    if (state == Download.STATE_DOWNLOADING) {
                        circularProgressBar.indeterminateMode = false
                        circularProgressBar.progressMax = maxProgress
                        circularProgressBar.progress = downloadedLength
                    }


                }
            } else if (downloaded && state != Download.STATE_DOWNLOADING && state != Download.STATE_QUEUED) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Remove this media",
                    tint = Color.Blue,
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            // TODO: click to remove downloaded media

                        }
                )
            } else {
                Icon(
                    painterResource(
                        id = R.drawable.download
                    ),
                    contentDescription = "Download this media",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onDownloadClicked()
                        }
                )
            }

        }
    }
}
fun millisToDuration(millis: Long): String {
    var minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    if(seconds.toInt() >= 50) minutes = TimeUnit.MILLISECONDS.toMinutes(millis + 60000L)
    return String.format("%02d Menit", minutes)
}