package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components

import android.support.v4.media.MediaMetadataCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.displayDescription
import com.church.injilkeselamatan.audiorenungan.feature_music.ui.sourceSansPro

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    mediaMetadataCompat: MediaMetadataCompat,
    onBackClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(onClick = { onBackClicked() }) {
            Icon(
                Icons.Rounded.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.CenterStart),
                tint = MaterialTheme.colors.onSurface
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = "Now Playing",
                fontFamily = sourceSansPro,
                fontSize = 20.sp,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mediaMetadataCompat.displayDescription ?: "",
                color = MaterialTheme.colors.onBackground,
                maxLines = 1,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}