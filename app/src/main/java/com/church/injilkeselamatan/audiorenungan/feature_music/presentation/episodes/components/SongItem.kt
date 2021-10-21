package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.mikhaellopez.circularprogressbar.CircularProgressBar

@Composable
fun SongItem(
    song: Song,
    maxProgress: Float = 100f,
    downloadedLength: Float = 0f,
    downloaded: Boolean,
    onPlayToggleClicked: () -> Unit,
    onDownloadClicked: () -> Unit
) {
    val color = MaterialTheme.colors.primary.toArgb()

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp)
            .clickable {
                onPlayToggleClicked()
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = rememberImagePainter(
                    data = song.imageUri
                ), modifier = Modifier.size(60.dp),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
                )

            }
        }
        if (!downloaded) {
            AndroidView(
                factory = {
                    CircularProgressBar(it)
                },
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        // TODO: click to download media
                    }
            ) { circularProgressBar ->

                circularProgressBar.progressBarColor = color
                circularProgressBar.progressMax = maxProgress
                circularProgressBar.progress = downloadedLength
            }
        }
        if (downloaded) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Remove this media",
                tint = Color.Green,
                modifier = Modifier
                    .size(24.dp)
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
                    .size(24.dp)
                    .clickable {
                        // TODO: click to download media
                        onDownloadClicked()
                    }
            )
        }
    }
}