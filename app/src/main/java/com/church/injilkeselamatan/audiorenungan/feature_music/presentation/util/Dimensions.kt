package com.church.injilkeselamatan.audiorenungan.feature_music.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class Dimensions {
    object Width : Dimensions()
    object Height : Dimensions()

    sealed class DimensionOperator {
        object LessThan : DimensionOperator()
        object GreaterThan : DimensionOperator()
    }

    class DimensionComparator(
        val operator: DimensionOperator,
        private val dimensions: Dimensions,
        val value: Dp
    ) {
        fun compare(screenWidth: Dp, screenHeight: Dp): Boolean {
            return if (dimensions is Width) {
                when (operator) {
                    is DimensionOperator.LessThan -> screenWidth < value
                    is DimensionOperator.GreaterThan -> screenWidth > value
                }
            } else {
                when (operator) {
                    is DimensionOperator.LessThan -> screenHeight < value
                    is DimensionOperator.GreaterThan -> screenHeight > value
                }
            }
        }
    }
}

@Composable
fun MediaQuery(comparator: Dimensions.DimensionComparator, content: @Composable () -> Unit) {
    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.dp /
            LocalDensity.current.density
    val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels.dp /
            LocalDensity.current.density
//    Log.d("ScreenSize", "Width: $screenWidth")
//    Log.d("ScreenSize", "Height: $screenHeight")
    if (comparator.compare(screenWidth, screenHeight)) {
        content()
    }
}

infix fun Dimensions.greaterThan(value: Dp): Dimensions.DimensionComparator {
    return Dimensions.DimensionComparator(
        operator = Dimensions.DimensionOperator.GreaterThan,
        dimensions = this,
        value = value
    )
}

infix fun Dimensions.lessThan(value: Dp): Dimensions.DimensionComparator {
    return Dimensions.DimensionComparator(
        operator = Dimensions.DimensionOperator.LessThan,
        dimensions = this,
        value = value
    )
}