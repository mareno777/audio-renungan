package com.church.injilkeselamatan.audio_presentation.albums.components

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.church.injilkeselamatan.audio_presentation.R
import com.church.injilkeselamatan.audiorenungan.core_ui.Dimensions
import com.church.injilkeselamatan.audiorenungan.core_ui.MarqueeText
import com.church.injilkeselamatan.audiorenungan.core_ui.lessThan
import com.church.injilkeselamatan.audiorenungan.core_ui.mediaQuery
import com.church.injilkeselamatan.core.util.extensions.displayDescription
import com.church.injilkeselamatan.core.util.extensions.displayIconUri
import com.church.injilkeselamatan.core.util.extensions.isPlaying
import com.church.injilkeselamatan.core.util.extensions.title

@Composable
fun PlayingNowSection(
    modifier: Modifier = Modifier,
    playbackStateCompat: PlaybackStateCompat,
    mediaMetadataCompat: MediaMetadataCompat,
    needToPlay: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(mediaMetadataCompat.displayIconUri)
            .transformations(RoundedCornersTransformation(15f))
            .build()
    )

    Surface(
        elevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .mediaQuery(
                comparator = Dimensions.Height lessThan 600.dp,
                modifier = modifier.fillMaxHeight(0.1f)
            )
            .fillMaxHeight(0.08f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(45.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    MarqueeText(
                        text = mediaMetadataCompat.title ?: "",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = mediaMetadataCompat.displayDescription ?: "",
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Icon(
                painterResource(
                    id = if (playbackStateCompat.isPlaying) {
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
                    .offset(x = 10.dp)
                    .clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null
                    ) {
                        needToPlay(!playbackStateCompat.isPlaying)
                    }
            )
        }
    }
}