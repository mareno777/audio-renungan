package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util

import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

fun Modifier.mediaQuery(
    comparator: Dimensions.DimensionComparator,
    modifier: Modifier
) : Modifier = composed {
    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.dp /
            LocalDensity.current.density
    val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels.dp /
            LocalDensity.current.density

    Log.d("ScreenSize", "Width: $screenWidth")
    Log.d("ScreenSize", "Height: $screenHeight")
    if (comparator.compare(screenWidth, screenHeight)) {
        this.then(modifier)
    } else {
        this
    }
}