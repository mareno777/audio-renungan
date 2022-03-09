package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.player.components

import android.support.v4.media.MediaMetadataCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.church.injilkeselamatan.audiorenungan.feature_music.exoplayer.media.extensions.displayIcon
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import kotlin.math.absoluteValue


@ExperimentalPagerApi
@Composable
fun AlbumArtPager(
    songs: List<MediaMetadataCompat>,
    pagerState: PagerState,
    imageLoader: ImageLoader
) {

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        count = songs.size
    ) { page ->
        Card(
            modifier = Modifier
            .graphicsLayer {

                val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                lerp(
                    start = 0.85f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                ).also { scale ->
                    scaleX = scale
                    scaleY = scale
                }

                alpha = lerp(
                    0.5f,
                    1f,
                    1f - pageOffset.coerceIn(0f, 1f)
                )
            }
            .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp
        ) {
            Box {
                Image(
                    painter = rememberImagePainter(
                        data = songs[page].displayIcon,
                        imageLoader = imageLoader
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

            }
        }
    }
}