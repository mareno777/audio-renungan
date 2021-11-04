package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.albums.components

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.church.injilkeselamatan.audiorenungan.R
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.album
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.artist
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.isPlaying
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.title
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.Dimensions
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.lessThan
import com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util.mediaQuery

@Composable
fun PlayingNowSection(
    modifier: Modifier = Modifier,
    playbackStateCompat: PlaybackStateCompat?,
    mediaMetadataCompat: MediaMetadataCompat?,
    needToPlay: (Boolean) -> Unit
) {

    val painter = when (mediaMetadataCompat?.album) {
        "Pohon Kehidupan" -> rememberImagePainter(R.drawable.pohon_kehidupan)
        "Belajar Takut Akan Tuhan" -> rememberImagePainter(R.drawable.btat)
        "Saat Teduh Bersama Tuhan" -> rememberImagePainter(R.drawable.stbt)
        else -> null
    }

    Surface(
        elevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .mediaQuery(
                comparator = Dimensions.Height lessThan 600.dp,
                modifier = modifier.fillMaxHeight(0.1f)
            )
            .fillMaxHeight(0.08f)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (painter != null) {

                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(70.dp)
                            .fillMaxWidth()
                    )

                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = mediaMetadataCompat?.title ?: "",
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = mediaMetadataCompat?.artist ?: "",
                        style = MaterialTheme.typography.caption,
                        fontSize = 14.sp
                    )

                }
            }
            Icon(
                painterResource(
                    id = if (playbackStateCompat?.isPlaying == true) {
                        R.drawable.ic_pause
                    } else {
                        R.drawable.ic_play_arrow
                    }
                ),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .width(50.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
                    .clickable {
                        needToPlay(playbackStateCompat?.isPlaying != true)
                    }
            )
        }
    }
}