package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components

import android.support.v4.media.MediaMetadataCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.church.injilkeselamatan.audiorenungan.core_ui.MarqueeText
import com.church.injilkeselamatan.core.util.extensions.artist
import com.church.injilkeselamatan.core.util.extensions.title

@Composable
fun TitleArtist(
    mediaMetadataCompat: MediaMetadataCompat
) {
    Box(contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if ((mediaMetadataCompat.title ?: "").length > 25) {
                MarqueeText(
                    modifier = Modifier
                        .wrapContentHeight(),
                    text = mediaMetadataCompat.title ?: "",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = mediaMetadataCompat.title ?: "",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mediaMetadataCompat.artist ?: "",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}