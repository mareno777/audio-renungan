package com.church.injilkeselamatan.audiorenungan.experiment

import android.support.v4.media.MediaMetadataCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.model.Song
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.android.material.math.MathUtils.lerp
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.absoluteValue


@ExperimentalPagerApi
@Composable
fun HorizontalPagerWithOffsetTransition(pagerState: PagerState, songs: List<MediaMetadataCompat>?) {


    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        Card(
            Modifier
                .graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue


                    lerp(
                        0.85f,
                        1f,
                        1f - pageOffset.coerceIn(0f, 1f)
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
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))

        ) {
            Box {
                songs?.get(page)?.description?.iconUri?.let {
                    GlideImage(
                        imageModel = it,
                        requestOptions = RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.DATA),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }
    }
}