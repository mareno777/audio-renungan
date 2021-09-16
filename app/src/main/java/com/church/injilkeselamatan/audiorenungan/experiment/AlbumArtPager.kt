package com.church.injilkeselamatan.audiorenungan.experiment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.church.injilkeselamatan.audiorenungan.data.models.MusicX
import com.church.injilkeselamatan.audiorenungan.viewmodels.PlayerViewModel
import com.google.accompanist.pager.*
import com.google.android.material.math.MathUtils.lerp
import kotlin.math.absoluteValue



@ExperimentalPagerApi
@Composable
fun HorizontalPagerWithOffsetTransition(pagerState: PagerState, songs: List<MusicX>?) {


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

        ) {
            Box {
                Image(
                    painter = rememberImagePainter(songs?.get(page)?.image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}