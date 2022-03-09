package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.episodes.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PieProgressBar(
    modifier: Modifier = Modifier,
    bytesDownloaded: Float = 0f,
    totalBytesToDownload: Float = 0f,
    color: Color = MaterialTheme.colors.primary,
    animDuration: Int = 300
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val currentPercentage by animateFloatAsState(
        targetValue = if (animationPlayed) bytesDownloaded / totalBytesToDownload
        else 0f,
        animationSpec = tween(durationMillis = animDuration)
    )

    LaunchedEffect(true) {
        animationPlayed = true
    }

    Box(
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.aspectRatio(1f)) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx())
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * currentPercentage,
                useCenter = true,
            )
        }
    }
}