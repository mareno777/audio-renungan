package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes.components

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.core.util.extensions.*
import com.google.android.exoplayer2.offline.Download
import java.util.concurrent.TimeUnit

@Composable
fun EpisodeItem(
    modifier: Modifier = Modifier,
    song: MediaMetadataCompat,
    totalBytesToDownload: Float = 0f,
    bytesDownloaded: Float = 0f,
    downloaded: Boolean,
    @Download.State
    state: Int,
    onPlayToggleClicked: () -> Unit,
    onDownloadClicked: () -> Unit,
    onRemoveDownloadClicked: () -> Unit,
    playbackState: PlaybackStateCompat,
    mediaMetadata: MediaMetadataCompat
) {

    val context = LocalContext.current
    var showRemoveDownloadDialog by remember {
        mutableStateOf(false)
    }
    val painter = rememberImagePainter(
        ImageRequest.Builder(context)
            .data(song.albumArt)
            .transformations(RoundedCornersTransformation(55f))
            .build()
    )

    Column(
        modifier = modifier
            .clickable { onPlayToggleClicked() }
            .padding(16.dp)
    ) {
        Row {
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
            text = "${song.displayDescription} • ${millisToDuration(song.duration)}",
            color = Color.Gray,
            style = MaterialTheme.typography.body2,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${song.artist} \u2022 12 November 2021",
            color = Color.Gray,
            style = MaterialTheme.typography.body2,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onPlayToggleClicked) {
                Icon(
                    painter = painterResource(
                        id = if (playbackState.isPlaying
                            && mediaMetadata.id == song.id
                        ) R.drawable.ic_outline_pause
                        else R.drawable.ic_outline_play
                    ),
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Blue,
                    modifier = Modifier.size(35.dp)
                )
            }
            if (state == Download.STATE_DOWNLOADING || state == Download.STATE_QUEUED) {

                if (state == Download.STATE_DOWNLOADING) {
                    PieProgressBar(
                        bytesDownloaded = bytesDownloaded,
                        totalBytesToDownload = totalBytesToDownload,
                        modifier = Modifier.size(25.dp)
                    )
                }
                if (state == Download.STATE_QUEUED) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(25.dp)
                    )
                }
            } else if (
                downloaded
                && state != Download.STATE_DOWNLOADING
                && state != Download.STATE_QUEUED
            ) {
                IconButton(onClick = { showRemoveDownloadDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Remove this media",
                        tint = Color.Blue,
                        modifier = Modifier.size(35.dp)
                    )
                }
            } else {
                IconButton(onClick = { onDownloadClicked() }) {
                    Icon(
                        painter = painterResource(R.drawable.download),
                        contentDescription = "Download this media",
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

        }
    }
    RemoveDownloadDialog(
        isVisible = showRemoveDownloadDialog,
        onRemoveClicked = {
            onRemoveDownloadClicked()
            showRemoveDownloadDialog = false
        },
        onDismissClicked = {
            showRemoveDownloadDialog = false
        }
    )
}

@Composable
fun millisToDuration(millis: Long): String {
    var minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    if (seconds.toInt() >= 50) minutes = TimeUnit.MILLISECONDS.toMinutes(millis + 60000L)
    return String.format("%02d Menit", minutes)
}